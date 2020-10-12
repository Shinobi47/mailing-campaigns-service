package com.benayed.mailing.campaigns.utils;

import static com.benayed.mailing.campaigns.constants.Constants.ILLEGAL_CUSTOM_HEADERS_CHARACTERS;

import java.util.Objects;

import javax.mail.Header;

import org.springframework.stereotype.Component;

import com.benayed.mailing.campaigns.dto.CampaignHeaders;

@Component
public class DataValidator {

	
	public void validateHeaders(CampaignHeaders headers) {
		if(headers == null)
			throw new IllegalArgumentException("campaign headers must not be null");
		
		if(headers.getAdditionnalHeaders() == null)
			return;
					
		if(doesHeadersContainNullAdditionnalHeader(headers))
			throw new IllegalArgumentException("NULL detected in your additionnal headers ! (either the value, the name or the header itself)");

		if(anyAdditionnalHeaderContainsIllegalCharacter(headers))
			throw new IllegalArgumentException("your additionnal headers contain Illegal characters, cannot send campaign");
	}

	private boolean doesHeadersContainNullAdditionnalHeader(CampaignHeaders headers) {
		return headers.getAdditionnalHeaders().stream().anyMatch(header -> header == null || header.getName() == null || header.getValue() == null);
	}

	private boolean anyAdditionnalHeaderContainsIllegalCharacter(CampaignHeaders headers) {
		return headers.getAdditionnalHeaders().stream()
		.filter(Objects::nonNull)
		.anyMatch(this::headerContainsAnIllegalCharacter);
	}

	private boolean headerContainsAnIllegalCharacter(Header header) {
		return ILLEGAL_CUSTOM_HEADERS_CHARACTERS.stream()
				.anyMatch(illegalCharacter -> header.getName().contains(illegalCharacter) || header.getValue().contains(illegalCharacter));
	}
}
