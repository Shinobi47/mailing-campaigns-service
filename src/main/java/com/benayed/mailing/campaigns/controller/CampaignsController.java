package com.benayed.mailing.campaigns.controller;

import java.util.Arrays;
import java.util.List;

import javax.mail.Header;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.benayed.mailing.campaigns.dto.CampaignDto;
import com.benayed.mailing.campaigns.dto.CampaignHeaders;
import com.benayed.mailing.campaigns.dto.SMTPConfig;
import com.benayed.mailing.campaigns.service.CampaignService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1")
public class CampaignsController {
	
	private CampaignService campaignService;

//	@GetMapping(path = "/hi")
//	public ResponseEntity<?> get() throws InterruptedException{
//	    String creative = "mail body";
//	    CampaignHeaders campaignHeaders = CampaignHeaders.builder()
//		      .from("web@gmail.com")
//		      .fromName("W-E-B")
//		      .subject("My Mail Subject")
//		      .replyTo("ReplyTo@adr.com")
//		      .bounceAddr("return-path@gmail.com")
//		      .received("received by me")
//		      .additionnalHeaders(Arrays.asList(new Header("X-h1", "hola que passa"),  new Header("X-h2", "hola que tal"))).build();		
//		Integer batchSize = 1;
//		Integer	intervalBetweenBatchesInSec = 60;
//		Integer mailsToSendBeforeIpRotate = 1;
//		
//		
//		List<Long> groupsIds = Arrays.asList(1L);
//		Long suppressionId = 1L;
//		int offset = 1;
//		int limit = 1;
//		boolean isDataFiltered = true;
//		
//		List<Long> mtasIds = Arrays.asList(1L);		
//		
//		SMTPConfig smtpConfig = SMTPConfig.builder()
//				  .domain("smtp.mailtrap.io")
//				  .port("587")
//				  .username("1f8ddaa0ad6d5d").build();
//		campaignService.send(smtpConfig, campaignHeaders, "ok", "hola@gmail.com");
//		campaignService.processCampaign(batchSize, intervalBetweenBatchesInSec, mailsToSendBeforeIpRotate, campaignHeaders, creative, mtasIds, groupsIds, suppressionId, offset, limit, isDataFiltered);
//		
//		return null;
//	}
//	
	
	@PostMapping(path = "/campaign")
	public ResponseEntity<?> executeCampaign(@RequestBody CampaignDto campaignInfos) throws InterruptedException{
		System.out.println(campaignInfos);
		campaignService.processCampaign(campaignInfos);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
