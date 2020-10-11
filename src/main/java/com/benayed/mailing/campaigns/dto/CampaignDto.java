package com.benayed.mailing.campaigns.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class CampaignDto {
	
	private Integer batchSize;
	private Integer intervalBetweenBatchesInSec;
	private Integer mailsToSendBeforeIpRotate;
	private CampaignHeaders campaignHeaders;
	private String creative;
	private List<Long> mtasIds;
	private List<Long> groupsIds;
	private Long suppressionId;
	private Integer offset;
	private Integer limit;
	private Boolean isDataFiltered;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

}
