package com.benayed.mailing.campaigns.service;

import static com.benayed.mailing.campaigns.enums.CampaignStatus.FAILED;
import static com.benayed.mailing.campaigns.enums.CampaignStatus.SENDING;
import static com.benayed.mailing.campaigns.enums.CampaignStatus.SUCCESS;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.benayed.mailing.campaigns.dto.CampaignDto;
import com.benayed.mailing.campaigns.dto.CampaignHeaders;
import com.benayed.mailing.campaigns.dto.DataItemDto;
import com.benayed.mailing.campaigns.dto.SMTPConfig;
import com.benayed.mailing.campaigns.exception.TechnicalException;
import com.benayed.mailing.campaigns.repository.CampaignRepository;
import com.benayed.mailing.campaigns.utils.DataMapper;
import com.benayed.mailing.campaigns.utils.DataValidator;
import com.google.common.collect.Iterables;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CampaignService {
	
	private CampaignResourcesRepository campaignResourcesRepository;
	private DataMapper dataMapper;
	private DataValidator dataValidator;
	private CampaignRepository campaignRepository;
	

	public void processCampaign(CampaignDto campaignInfos) throws InterruptedException {
		
		Assert.notNull(campaignInfos, "Campaigns infos cannot be null");
		dataValidator.validateHeaders(campaignInfos.getCampaignHeaders());
		Assert.isTrue(!StringUtils.isBlank(campaignInfos.getCreative()), "creative cannot be null or empty");
		
		try {
			log.info("Processing the campaign ..."); 
			
			LocalDateTime startTime = LocalDateTime.now();
			campaignInfos.setStartTime(startTime);
			Long campaignId = campaignRepository.save(dataMapper.toEntity(campaignInfos, SENDING)).getId();
			campaignInfos.setId(campaignId);
			List<SMTPConfig> smtpServersConfig = campaignResourcesRepository.fetchServersDetails(campaignInfos.getMtasIds()).stream()
					.map(dataMapper::toSmtpConfig).collect(Collectors.toList());
			
			List<DataItemDto> dataItems = campaignResourcesRepository.fetchData(campaignInfos.getGroupsIds(), campaignInfos.getSuppressionId(), campaignInfos.getOffset(), campaignInfos.getLimit(), campaignInfos.getIsDataFiltered());

			sendCampaign(campaignInfos.getBatchSize(), campaignInfos.getIntervalBetweenBatchesInSec(), campaignInfos.getMailsToSendBeforeIpRotate(), dataItems, smtpServersConfig, campaignInfos.getCampaignHeaders(), campaignInfos.getCreative(), campaignInfos.getTestAfter(), campaignInfos.getTestMails());
			
			campaignInfos.setEndTime(LocalDateTime.now());

			campaignRepository.save(dataMapper.toEntity(campaignInfos, SUCCESS));
			log.info("Campaign Terminated successfully !");
			
		}catch(Exception e) {
			log.error("Error while processing the campaign");
			campaignInfos.setEndTime(LocalDateTime.now());
			campaignRepository.save(dataMapper.toEntity(campaignInfos, FAILED));
			throw e;
		}

	}

	private void sendEmail(SMTPConfig smtpConfig, CampaignHeaders campaignHeaders, String body, String recipient) {
		
		Properties properties = System.getProperties();
	
		// Setup mail server
		properties.setProperty("mail.smtp.host", smtpConfig.getDomain());
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", smtpConfig.getPort());
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.trust", smtpConfig.getDomain()); // otherwise Could not convert socket to TLS; nested
															// exception is: javax.net.ssl.SSLHandshakeException:
															// PKIX path building failed:
															// sun.security.provider.certpath.SunCertPathBuilderException:
															// unable to find valid certification path to requested
															// target
	
		if(campaignHeaders.getBounceAddr() != null)
			properties.put("mail.smtp.from", campaignHeaders.getBounceAddr()); //You may want to set this to a generic address, different than the From: header, so you can process remote bounces. This done by setting mail.smtp.from property in JavaMail.
																			   // bounce address = return path, reverse path, envelope from, envelope sender, MAIL FROM, 2821-FROM, return address,
		
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpConfig.getUsername(), smtpConfig.getPassword());
			}
		}); // Session.getDefaultInstance(properties);
	
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(campaignHeaders.getFrom(), campaignHeaders.getFromName()));
			message.setSubject(campaignHeaders.getSubject());
			if(campaignHeaders.getReplyTo() != null) {
				Address[] replyToAddrs = {new InternetAddress(campaignHeaders.getReplyTo())};
				message.setReplyTo(replyToAddrs);
			}
			message.setHeader("Received", campaignHeaders.getReceived());
			for (Entry<String, String> header : campaignHeaders.getAdditionnalHeaders() != null ? campaignHeaders.getAdditionnalHeaders().entrySet() : new HashSet<Entry<String, String>> ()) {
				message.addHeader(header.getKey(), header.getValue());
			}
			message.setSentDate(new Date());
			message.setText(body);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

			// Send message
			Transport.send(message);
			
		} catch (MessagingException | UnsupportedEncodingException mex) {
			log.error("Exception raised when sending email:", mex);
			throw new TechnicalException(mex);
		}
		
	}

	private synchronized void sendCampaign(Integer batchSize, Integer intervalBetweenBatchesInSec, Integer mailsToSendBeforeIpRotate, List<DataItemDto> dataItems, List<SMTPConfig> smtpServersConfig, CampaignHeaders campaignHeaders, String creative, Integer testAfter, List<String> testMails) throws InterruptedException {
		Assert.isTrue(!CollectionUtils.isEmpty(smtpServersConfig), "cannot start campaign with null or empty smtp servers config !");
		Assert.isTrue(!CollectionUtils.isEmpty(dataItems), "cannot start campaign with null or empty data !");
		
		Iterator<SMTPConfig> serversIterator = Iterables.cycle(smtpServersConfig).iterator();
		
		List<String> data = dataItems.stream().map(DataItemDto::getProspectEmail).collect(Collectors.toList());

	    SMTPConfig smtpConfig = serversIterator.next();
		Instant sendStart = Instant.now();
		
		log.info("Starting campaign with {} smtp servers and {} data items", smtpServersConfig.size(), dataItems.size());
		log.info("smtp is gonna rotate every {} e-mail sent. {} emails are going to be sent every {} seconds", mailsToSendBeforeIpRotate, batchSize, intervalBetweenBatchesInSec);
		for(int index = 1; index <= data.size(); index++) {
			
			sendEmail(smtpConfig, campaignHeaders, creative, data.get(index-1));

			if(shouldRotateDomain(mailsToSendBeforeIpRotate, index)) {
				smtpConfig = serversIterator.next();
			}
			
			if(batchSizeReached(batchSize, index)) {
				Duration sendDuration = Duration.between(sendStart, Instant.now());
				
				waitIfNeeded(intervalBetweenBatchesInSec, sendDuration);
				sendStart = Instant.now();
			}
			
			if(sendQuotaReachedBeforeSendingTestMails(testAfter, index)) {
				sendTestMails(smtpConfig, campaignHeaders, creative, testMails);
				
			}
			
		}
		log.info("Campaign sent successfully !");
	}

	private void sendTestMails(SMTPConfig smtpConfig, CampaignHeaders campaignHeaders, String creative,	List<String> testMails) {
		if(!testMails.isEmpty()) {
			
			log.info("Sending test mails ...");
			for(String testMail : testMails) {
				sendEmail(smtpConfig, campaignHeaders, creative, testMail);
			}			
		}
		else {
			log.info("No test mails were sent");
		}
	}

	private boolean sendQuotaReachedBeforeSendingTestMails(Integer testAfter, int index) {

		return testAfter != null && testAfter != 0  && index % testAfter == 0;
	}
	
	
	private void waitIfNeeded(Integer intervalBetweenBatchesInSec, Duration sendDuration) throws InterruptedException {
		if(intervalBetweenBatchesInSec != 0 && sendLastedLessThanBatchInterval(intervalBetweenBatchesInSec, sendDuration)) {
			waitTheRemainingTimeToReachBatchInterval(intervalBetweenBatchesInSec, sendDuration);
		}
	}

	private boolean shouldRotateDomain(Integer mailsToSendBeforeIpRotate, int index) { // rotation threshold reached
		return mailsToSendBeforeIpRotate != null && mailsToSendBeforeIpRotate != 0 && index % mailsToSendBeforeIpRotate == 0;
	}
	
	private boolean batchSizeReached(Integer batchSize, int index) {
		return batchSize != null && batchSize != 0 && index % batchSize == 0;
	}

	private synchronized void waitTheRemainingTimeToReachBatchInterval(Integer intervalBetweenBatchesInSec, Duration sendDuration)
			throws InterruptedException {
		Assert.notNull(intervalBetweenBatchesInSec, "Interval between batches is null, cannot wait !");
		wait(sendDuration.minusSeconds(intervalBetweenBatchesInSec).abs().toMillis());
	}

	private boolean sendLastedLessThanBatchInterval(Integer intervalBetweenBatchesInSec, Duration sendDuration) {
		return sendDuration.minusSeconds(intervalBetweenBatchesInSec).isNegative();
	}	

}
