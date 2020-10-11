package com.benayed.mailing.campaigns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DataItemDto {

	private String prospectEmail;
	private String isp;
}
