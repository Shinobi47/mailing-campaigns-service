package com.benayed.mailing.campaigns.dto;

import java.util.Map;

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
    private String bounceAddr;
    private String received;
    
    private Map<String, String> additionnalHeaders;

}
