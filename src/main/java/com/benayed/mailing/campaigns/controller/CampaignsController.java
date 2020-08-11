package com.benayed.mailing.campaigns.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1")
public class CampaignsController {

	@GetMapping(path = "/hi")
	public ResponseEntity<?> get(){
		
		return null;
	}

}
