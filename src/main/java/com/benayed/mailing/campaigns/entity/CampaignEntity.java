package com.benayed.mailing.campaigns.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.benayed.mailing.campaigns.enums.CampaignStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = "CAMPAIGN")
public class CampaignEntity {

//	zid TU
//	async ...

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "campaign_generator")
	@SequenceGenerator(name="campaign_generator", sequenceName = "CMP_ID_PK_SEQ", allocationSize = 1)
	@Column(name = "CMP_ID")
	private Long id;

	@Column(name = "CMP_BAT_SIZE")
	private Integer batchSize;

	@Column(name = "CMP_BAT_INT_SEC")
	private Integer intervalBetweenBatchesInSec;

	@Column(name = "CMP_IP_ROT_CNT")
	private Integer mailsToSendBeforeIpRotate;

	@Column(name = "CMP_FROM_HEAD")
	private String fromHeader;

	@Column(name = "CMP_FROM_NAME_HEAD")
	private String fromNameHeader;

	@Column(name = "CMP_SUB_HEAD")
	private String subjectHeader;

	@Column(name = "CMP_REP_TO_HEAD")
	private String replyToHeader;

	@Column(name = "CMP_BNC_ADR_HEAD")
	private String bounceAddrHeader;

	@Column(name = "CMP_RCV_HEAD")
	private String receivedHeader;

	@Column(name = "CMP_ADD_HEAD")
	private String additonnalHeaders;

	@Column(name = "CMP_MAIL_BODY")
	private String mailBody;

	@Column(name = "CMP_MTA_IDS")
	private String mtasIds;

	@Column(name = "CMP_GRP_IDS")
	private String groupsIds;

	@Column(name = "CMP_SUPP_ID")
	private Long suppressionId;

	@Column(name = "CMP_OFFSET")
	private Integer offset;

	@Column(name = "CMP_LIMIT")
	private Integer limit;

	@Column(name = "CMP_IS_DATA_FIL")
	private Boolean isDataFiltered;

	@Column(name = "CMP_STATUS")
	private CampaignStatus status;
	
	@Column(name = "CMP_START_TIME")
	private LocalDateTime startTime;
	
	@Column(name = "CMP_END_TIME")
	private LocalDateTime endTime;

}
