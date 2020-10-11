package com.benayed.mailing.campaigns.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.benayed.mailing.campaigns.dto.DataItemDto;
import com.benayed.mailing.campaigns.dto.MTADto;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CampaignResourcesRepository {

	private RestTemplate restTemplate;
	
	@Value("${servers-service-url}")
	private String mtasUrl;
	
	@Value("${assets-service-url}")
	private String dataUrl;
	
	public CampaignResourcesRepository(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public List<DataItemDto> fetchData(List<Long> groupsIds, Long suppressionId, Integer offset, Integer limit, Boolean isFiltered) {
		Assert.isTrue(!CollectionUtils.isEmpty(groupsIds), "cannot fetch data with null or empty groupsIds");

		String dataPath = "/groups/" + groupsIds.stream().map(String::valueOf).collect(Collectors.joining("-")) + "/data";

		String url = UriComponentsBuilder.fromHttpUrl(dataUrl + dataPath)
				.queryParam("filtered", true) 
				.queryParam("suppression-id", 1)
				.queryParam("offset", 1)
				.queryParam("limit", 1)
				.toUriString();
		try {
			List<DataItemDto> data = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<DataItemDto>>() {}).getBody();
			return data;
		}catch(HttpClientErrorException e) {
			if(HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				return List.of();
			}
			throw e;
		}

	}
	
	public List<MTADto> fetchServersDetails(List<Long> mtasIds) {
		Assert.isTrue(!CollectionUtils.isEmpty(mtasIds), "cannot fetch MTAs with null or empty mtasIds");
		
		String mtasPath = "/mtas/" + mtasIds.stream().map(String::valueOf).collect(Collectors.joining(","));
		try {
			return restTemplate.exchange(mtasUrl + mtasPath, HttpMethod.GET, null, new ParameterizedTypeReference<List<MTADto>>() {}).getBody();
		}catch(HttpClientErrorException e) {
			if(HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				log.warn("The call : {} returned no config !", mtasPath);
				return List.of();
			}
			throw e;
		}
	}
    
}
