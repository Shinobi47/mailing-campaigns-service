package com.benayed.mailing.campaigns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder	
public class SMTPConfig {
	
	private String domain;
	private String port;
	private String username;
	private String password;
}
