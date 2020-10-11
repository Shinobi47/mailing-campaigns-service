package com.benayed.mailing.campaigns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.benayed.mailing.campaigns.entity.CampaignEntity;

public interface CampaignRepository extends JpaRepository<CampaignEntity, Long>{
	
}
