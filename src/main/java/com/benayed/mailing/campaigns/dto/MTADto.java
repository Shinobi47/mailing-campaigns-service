package com.benayed.mailing.campaigns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class MTADto {

	private Long id;
	private String name;
	private String dns;
	private String ip;
	private String port;
	private String username;
	private String password;
}
