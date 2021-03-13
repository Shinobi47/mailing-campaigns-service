package com.benayed.mailing.campaigns.utils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.mail.Header;

import org.springframework.stereotype.Component;

import com.benayed.mailing.campaigns.dto.CampaignDto;
import com.benayed.mailing.campaigns.dto.MTADto;
import com.benayed.mailing.campaigns.dto.SMTPConfig;
import com.benayed.mailing.campaigns.entity.CampaignEntity;
import com.benayed.mailing.campaigns.enums.CampaignStatus;

@Component
public class DataMapper {
	
	public SMTPConfig toSmtpConfig(MTADto mta) {
		return SMTPConfig.builder()
		.domain(mta.getDns())
		.port(mta.getPort())
		.username(mta.getUsername())
		.password(mta.getPassword()).build();
	}
	
	public CampaignEntity toEntity(CampaignDto dto, CampaignStatus status) {
		return dto == null ? null : CampaignEntity.builder()
		.id(dto.getId())
		.batchSize(dto.getBatchSize())
		.intervalBetweenBatchesInSec(dto.getIntervalBetweenBatchesInSec())
		.mailsToSendBeforeIpRotate(dto.getMailsToSendBeforeIpRotate())
		.fromHeader(dto.getCampaignHeaders().getFrom())
		.fromNameHeader(dto.getCampaignHeaders().getFromName())
		.subjectHeader(dto.getCampaignHeaders().getSubject())
		.replyToHeader(dto.getCampaignHeaders().getReplyTo())
		.bounceAddrHeader(dto.getCampaignHeaders().getBounceAddr())
		.receivedHeader(dto.getCampaignHeaders().getReceived())
		.additonnalHeaders(toJoinedStringHeaders(dto.getCampaignHeaders().getAdditionnalHeaders()))
		.mailBody(dto.getCreative())
		.mtasIds(toJoinedString(dto.getMtasIds()))
		.groupsIds(toJoinedString(dto.getGroupsIds()))
		.suppressionId(dto.getSuppressionId())
		.offset(dto.getOffset())
		.limit(dto.getLimit())
		.isDataFiltered(dto.getIsDataFiltered())
		.status(status)
		.startTime(dto.getStartTime())
		.endTime(dto.getEndTime()).build();
	}

	private String toJoinedString(List<Long> mtasIds) {
		return mtasIds == null ? null :
			mtasIds.stream().map(String::valueOf).collect(Collectors.joining(";"));
	}

	private String toJoinedStringHeaders(List<Header> headers) {
		return headers == null ? null :
			headers.stream()
				.filter(Objects::nonNull)
				.map(header -> header.getName() + ":" + header.getValue())
				.collect(Collectors.joining(";"));
	}
}
