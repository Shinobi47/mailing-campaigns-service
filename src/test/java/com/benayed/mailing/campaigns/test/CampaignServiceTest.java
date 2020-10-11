package com.benayed.mailing.campaigns.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.benayed.mailing.campaigns.dto.CampaignDto;
import com.benayed.mailing.campaigns.dto.CampaignHeaders;
import com.benayed.mailing.campaigns.dto.DataItemDto;
import com.benayed.mailing.campaigns.dto.MTADto;
import com.benayed.mailing.campaigns.repository.CampaignRepository;
import com.benayed.mailing.campaigns.service.CampaignResourcesRepository;
import com.benayed.mailing.campaigns.service.CampaignService;
import com.benayed.mailing.campaigns.utils.DataMapper;
import com.benayed.mailing.campaigns.utils.DataValidator;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {

	private CampaignService campaignService;
	
	@Mock
	private CampaignResourcesRepository campaignResourcesRepository;
	
	@Mock
	private CampaignRepository campaignRepository;
	
	private DataMapper dataMapper = new DataMapper();
	
	private DataValidator dataValidator = new DataValidator();
	
//	private GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	private GreenMail greenMail;
	
	@BeforeEach
	private void init() {
		campaignService = new CampaignService(campaignResourcesRepository, dataMapper, dataValidator, campaignRepository);

	}
	
	@AfterEach
	private void shut() {
		greenMail.stop();
	}
	
	@Test
	public void should_send_one_mail() throws InterruptedException, IOException, MessagingException {
		//Arrange
		String bounceAddr = "BounceAddr@gmail.com";
		String from = "from@gmail.com";
		String fromName = "fromName";
		String subject = "Subject1";
		String replyTo = "ReplyTo@gmail.com";
		String received = "received";
		Header additionnalHeader = new Header("headerName", "headerValue");
		
		String creative = "This is a mail creative";

		int batchSize = 10;
		int intervalBetweenBatchesInSec = 20;
		int mailsToSendBeforeIpRotate = 7;
		
		CampaignDto campaignInfos = CampaignDto.builder()
				.batchSize(batchSize)
				.intervalBetweenBatchesInSec(intervalBetweenBatchesInSec)
				.mailsToSendBeforeIpRotate(mailsToSendBeforeIpRotate)
				.campaignHeaders(buildCampaignHeaders(bounceAddr, from, fromName, subject, replyTo, received, additionnalHeader))
				.creative(creative)
				.mtasIds(Arrays.asList(1L,2L))
				.groupsIds(Arrays.asList(4L,5L))
				.suppressionId(1L)
				.offset(0)
				.limit(11)
				.isDataFiltered(true).build();
		
		MTADto smtpServer = buildMTADto("mtaName", "localHost", "127.0.0.1", "7000", "username", "password");
		
		List<DataItemDto> dataItems = buildDummyDataListHavingSize(1);
		
		ServerSetup serverSetup = new ServerSetup(Integer.parseInt(smtpServer.getPort()), smtpServer.getIp(), ServerSetup.PROTOCOL_SMTP);
		greenMail = new GreenMail(serverSetup);
		greenMail.setUser(smtpServer.getUsername(), smtpServer.getPassword());
		greenMail.start();

		Mockito.when(campaignResourcesRepository.fetchServersDetails(Arrays.asList(1L,2L))).thenReturn(Arrays.asList(smtpServer));
		Mockito.when(campaignResourcesRepository.fetchData(campaignInfos.getGroupsIds(), campaignInfos.getSuppressionId(), campaignInfos.getOffset(), campaignInfos.getLimit(), campaignInfos.getIsDataFiltered())).thenReturn(dataItems);
		
		//Act
		campaignService.processCampaign(campaignInfos);

		//Assert
//		assertTrue(greenMail.waitForIncomingEmail(5000, 2));
		Message[] messages = greenMail.getReceivedMessages();
		
		Assertions.assertThat(messages).hasSize(1);
		String receivedHeaders = GreenMailUtil.getHeaders(messages[0]);
		validateReceivedHeaders(bounceAddr, from, fromName, subject, replyTo, received, additionnalHeader, dataItems.get(0).getProspectEmail(), receivedHeaders);
		Assertions.assertThat(GreenMailUtil.getBody(messages[0])).isEqualTo(creative);
				
	}	
	
	
	@Test
	public void should_send_one_maila() throws InterruptedException, IOException, MessagingException {
		//Arrange
		String bounceAddr = "BounceAddr@gmail.com";
		String from = "from@gmail.com";
		String fromName = "fromName";
		String subject = "Subject1";
		String replyTo = "ReplyTo@gmail.com";
		String received = "received";
		Header additionnalHeader = new Header("headerName", "headerValue");
		
		String creative = "This is a mail creative";

		int batchSize = 3;
		int intervalBetweenBatchesInSec = 4;
		int mailsToSendBeforeIpRotate = 100;
		
		CampaignDto campaignInfos = CampaignDto.builder()
				.batchSize(batchSize)
				.intervalBetweenBatchesInSec(intervalBetweenBatchesInSec)
				.mailsToSendBeforeIpRotate(mailsToSendBeforeIpRotate)
				.campaignHeaders(buildCampaignHeaders(bounceAddr, from, fromName, subject, replyTo, received, additionnalHeader))
				.creative(creative)
				.mtasIds(Arrays.asList(1L,2L))
				.groupsIds(Arrays.asList(4L,5L))
				.suppressionId(1L)
				.offset(0)
				.limit(11)
				.isDataFiltered(true).build();
		
		MTADto smtpServer = buildMTADto("mtaName", "localHost", "127.0.0.1", "7000", "username", "password");
		
		List<DataItemDto> dataItems = buildDummyDataListHavingSize(8);
		
		ServerSetup serverSetup = new ServerSetup(Integer.parseInt(smtpServer.getPort()), smtpServer.getIp(), ServerSetup.PROTOCOL_SMTP);
		greenMail = new GreenMail(serverSetup);
		greenMail.setUser(smtpServer.getUsername(), smtpServer.getPassword());
		greenMail.start();

		Mockito.when(campaignResourcesRepository.fetchServersDetails(campaignInfos.getMtasIds())).thenReturn(Arrays.asList(smtpServer));
		Mockito.when(campaignResourcesRepository.fetchData(campaignInfos.getGroupsIds(), campaignInfos.getSuppressionId(), campaignInfos.getOffset(), campaignInfos.getLimit(), campaignInfos.getIsDataFiltered())).thenReturn(dataItems);
		
		//Act
		campaignService.processCampaign(campaignInfos);

		//Assert
		Message[] messages = greenMail.getReceivedMessages();
		
		Assertions.assertThat(messages).hasSize(8);
		for(int i = 0; i < messages.length; i++) {
			String recipientEmail = dataItems.get(i).getProspectEmail();
			String receivedHeaders = GreenMailUtil.getHeaders(messages[i]);
			validateReceivedHeaders(bounceAddr, from, fromName, subject, replyTo, received, additionnalHeader, recipientEmail, receivedHeaders);
			Assertions.assertThat(GreenMailUtil.getBody(messages[i])).isEqualTo(creative);
		}
		Mockito.verify(campaignRepository, Mockito.times(1)).save(Mockito.any());
		
		test batches ? send time ?
		
	}

	private void validateReceivedHeaders(String bounceAddr, String from, String fromName, String subject,
			String replyTo, String received, Header additionnalHeader,	String recipientEmail, String receivedHeaders) {
		Assertions.assertThat(receivedHeaders).contains("Return-Path: <" + bounceAddr + ">");
		Assertions.assertThat(receivedHeaders).contains("Received: " + received);
		Assertions.assertThat(receivedHeaders).contains("Date");
		Assertions.assertThat(receivedHeaders).contains("From: " + fromName + " <" + from + ">");
		Assertions.assertThat(receivedHeaders).contains("Reply-To: " + replyTo);
		Assertions.assertThat(receivedHeaders).contains("To: " + recipientEmail);
		Assertions.assertThat(receivedHeaders).contains("Subject: " + subject);
		Assertions.assertThat(receivedHeaders).contains(additionnalHeader.getName() + ": " + additionnalHeader.getValue());
	}

	private MTADto buildMTADto(String mtaName, String dns, String ip, String port, String username, String password) {
		return MTADto.builder()
				.id(6L)
				.name(mtaName)
				.dns(dns)
				.ip(ip)
				.port(port)
				.username(username)
				.password(password).build();
	}

	private CampaignHeaders buildCampaignHeaders(String bounceAddr, String from, String fromName, String subject,
			String replyTo, String received, Header additionnalHeader) {
		return CampaignHeaders.builder()
				.from(from)
				.fromName(fromName)
				.subject(subject)
				.replyTo(replyTo) 
				.bounceAddr(bounceAddr)
				.received(received)
				.additionnalHeaders(Arrays.asList(additionnalHeader)).build();
	}
	
	private List<DataItemDto> buildDummyDataListHavingSize(int listSize) {
		List<DataItemDto> dummyData = new ArrayList<DataItemDto>();
		for(int i = 0; i < listSize; i++) {
			dummyData.add(DataItemDto.builder()
				.prospectEmail("toMail" + i +"@gmail.com")
				.isp("GMAIL").build());
		}
		return dummyData;
	}
}
