package com.benayed.mailing.campaigns.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.Header;

import com.fasterxml.jackson.annotation.JsonSetter;

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
    
    private List<Header> additionnalHeaders;
    
    
    @JsonSetter("additionnalHeaders")
    public void setAdditionnalHeaders(List<Map<String, String>> headers) {
    	List<Header> additionnalHeaders = new ArrayList<Header>();
    	
    	for(Map<String, String> header : headers) {
    		additionnalHeaders.add(new Header(header.get("name"), header.get("value")));
    	}    	
    	this.additionnalHeaders = List.copyOf(additionnalHeaders);
    }
}
