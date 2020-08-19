package com.benayed.mailing.campaigns;

import java.util.List;

import javax.mail.Header;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CampaignHeaders {
    private String from;
    private String fromName;
    private String subject;
    private String replyTo; 
    private String bounceAddr;  //You may want to set this to a generic address, different than the From: header, so you can process remote bounces. This done by setting mail.smtp.from property in JavaMail.
    							// bounce address = return path, reverse path, envelope from, envelope sender, MAIL FROM, 2821-FROM, return address,
    private String received;
    
    private List<Header> additionnalHeaders;


}
