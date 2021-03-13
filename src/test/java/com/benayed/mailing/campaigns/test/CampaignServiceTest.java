package com.benayed.mailing.campaigns.test;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.benayed.mailing.campaigns.dto.CampaignDto;
import com.benayed.mailing.campaigns.dto.CampaignHeaders;
import com.benayed.mailing.campaigns.dto.DataItemDto;
import com.benayed.mailing.campaigns.dto.MTADto;
import com.benayed.mailing.campaigns.entity.CampaignEntity;
import com.benayed.mailing.campaigns.enums.CampaignStatus;
import com.benayed.mailing.campaigns.exception.TechnicalException;
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
	
	private GreenMail greenMail;
	
	@Captor
	private ArgumentCaptor<CampaignEntity> campaignEntityCaptor;
	
	@BeforeEach
	private void init() {
		campaignService = new CampaignService(campaignResourcesRepository, dataMapper, dataValidator, campaignRepository);

	}
	
	@AfterEach
	private void shut() {
		Optional.ofNullable(greenMail).ifPresent(GreenMail::stop);
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
	public void should_send_3_batches_correctly_and_wait_between_batches() throws InterruptedException, IOException, MessagingException {
		//Arrange	
		String creative = "This is a mail creative";

		int batchSize = 3;
		int intervalBetweenBatchesInSec = 4;
		int mailsToSendBeforeIpRotate = 100;
		
		CampaignDto campaignInfos = CampaignDto.builder()
				.batchSize(batchSize)
				.intervalBetweenBatchesInSec(intervalBetweenBatchesInSec)
				.mailsToSendBeforeIpRotate(mailsToSendBeforeIpRotate)
				.campaignHeaders(CampaignHeaders.builder().build())
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
		Mockito.verify(campaignRepository, Mockito.times(2)).save(campaignEntityCaptor.capture());
		CampaignEntity persistedCampaign = campaignEntityCaptor.getValue();
		Assertions.assertThat(persistedCampaign.getStatus()).isEqualTo(CampaignStatus.TERMINATED);
		Assertions.assertThat(Duration.between(persistedCampaign.getStartTime(), persistedCampaign.getEndTime()).compareTo(Duration.ofSeconds(2*4))).isPositive(); // campaign execution lasts longer than  8 (2*4) seconds.
																																								   // with batch size 3 and 8 emails and interval between batches 4 seconds, campaign should wait twice (3 mails sent then wait 4s then 3 sent then wait 4s then 2 sent). 
	}


	@Test
	public void should_rotate_ip_twice_between_the_two_available_servers() throws InterruptedException, IOException, MessagingException {
		//Arrange
		String creative = "This is a mail creative";

		int batchSize = 100;
		int intervalBetweenBatchesInSec = 4;
		int mailsToSendBeforeIpRotate = 2;
		
		CampaignDto campaignInfos = CampaignDto.builder()
				.batchSize(batchSize)
				.intervalBetweenBatchesInSec(intervalBetweenBatchesInSec)
				.mailsToSendBeforeIpRotate(mailsToSendBeforeIpRotate)
				.campaignHeaders(CampaignHeaders.builder().build())
				.creative(creative)
				.mtasIds(Arrays.asList(1L,2L))
				.groupsIds(Arrays.asList(4L,5L))
				.suppressionId(1L)
				.offset(0)
				.limit(11)
				.isDataFiltered(true).build();
		
		MTADto smtpServer1 = buildMTADto("mtaName1", "localHost", "127.0.0.1", "7000", "username", "password");
		MTADto smtpServer2 = buildMTADto("mtaName2", "localHost", "127.0.0.1", "6000", "username", "password");
		
		List<DataItemDto> dataItems = buildDummyDataListHavingSize(5);
		
		ServerSetup serverSetup = new ServerSetup(Integer.parseInt(smtpServer1.getPort()), smtpServer1.getIp(), ServerSetup.PROTOCOL_SMTP);
		greenMail = new GreenMail(serverSetup);
		greenMail.setUser(smtpServer1.getUsername(), smtpServer1.getPassword());
		greenMail.start();
		
		ServerSetup serverSetup2 = new ServerSetup(Integer.parseInt(smtpServer2.getPort()), smtpServer2.getIp(), ServerSetup.PROTOCOL_SMTP);
		GreenMail greenMailRotation = new GreenMail(serverSetup2);
		greenMailRotation.setUser(smtpServer2.getUsername(), smtpServer2.getPassword());
		greenMailRotation.start();
		

		Mockito.when(campaignResourcesRepository.fetchServersDetails(campaignInfos.getMtasIds())).thenReturn(Arrays.asList(smtpServer1, smtpServer2));
		Mockito.when(campaignResourcesRepository.fetchData(campaignInfos.getGroupsIds(), campaignInfos.getSuppressionId(), campaignInfos.getOffset(), campaignInfos.getLimit(), campaignInfos.getIsDataFiltered())).thenReturn(dataItems);
		
		//Act
		campaignService.processCampaign(campaignInfos);

		//Assert
		Assertions.assertThat(greenMail.getReceivedMessages()).hasSize(3);
		Assertions.assertThat(greenMailRotation.getReceivedMessages()).hasSize(2);
		Mockito.verify(campaignRepository, Mockito.times(2)).save(campaignEntityCaptor.capture());
		CampaignEntity persistedCampaign = campaignEntityCaptor.getValue();
		Assertions.assertThat(persistedCampaign.getStatus()).isEqualTo(CampaignStatus.TERMINATED);
		
		greenMailRotation.stop();
	}
	
	@Test
	public void should_persist_campaign_with_failed_status_when_processing_campaign_fails() throws InterruptedException, IOException, MessagingException {
		//Arrange
		CampaignDto campaignInfos = CampaignDto.builder().campaignHeaders(CampaignHeaders.builder().build()).creative("dummy").build();
		Mockito.when(campaignResourcesRepository.fetchServersDetails(Mockito.any())).thenThrow(new TechnicalException()); //could be any exception we are testing the catch block

		//Act
		org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> 
		campaignService.processCampaign(campaignInfos));

		//Assert
		Mockito.verify(campaignRepository, Mockito.times(2)).save(campaignEntityCaptor.capture());
		CampaignEntity persistedCampaign = campaignEntityCaptor.getValue();
		Assertions.assertThat(persistedCampaign.getEndTime()).isNotNull();
		Assertions.assertThat(persistedCampaign.getStartTime()).isNotNull();
		Assertions.assertThat(persistedCampaign.getStatus()).isEqualTo(CampaignStatus.FAILED);
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	public void should_throw_exception_when_creative_is_null_or_empty(String creative) throws InterruptedException, IOException, MessagingException {
		//Arrange
		CampaignDto campaignInfos = CampaignDto.builder().campaignHeaders(CampaignHeaders.builder().build()).creative(creative).build();

		//Act
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> 
		campaignService.processCampaign(campaignInfos));
		
		//Assert
		//=> Exception raised

	}
	
	@ParameterizedTest
	@NullAndEmptySource
	public void should_throw_exception_when_campaign_info_is_null(String creative) throws InterruptedException, IOException, MessagingException {
		//Arrange
		CampaignDto campaignInfos = null;
		//Act
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> 
		campaignService.processCampaign(campaignInfos));
		
		//Assert
		//=> Exception raised

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
