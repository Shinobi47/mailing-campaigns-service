package com.benayed.mailing.campaigns.test;

import java.util.Arrays;

import javax.mail.Header;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.benayed.mailing.campaigns.dto.CampaignHeaders;
import com.benayed.mailing.campaigns.utils.DataValidator;

@ExtendWith(MockitoExtension.class)
public class DataValidatorTest {
	
	private DataValidator dataValidator;
	
	@BeforeEach
	private void init() {
		dataValidator = new DataValidator();

	}
		
	@Test
	public void should_throw_exception_when_additionnal_Headers_contain_null_header(){
		//Arrange
		Header invalidHeader = null;
		CampaignHeaders campaignHeaders = CampaignHeaders.builder()
				.additionnalHeaders(Arrays.asList(invalidHeader)).build();
		//Act
		Assertions.assertThrows(IllegalArgumentException.class, () -> 
		dataValidator.validateHeaders(campaignHeaders));

		//Assert
		//=> exception thrown
	}

	@Test
	public void should_throw_exception_when_additionnal_Headers_contain_header_with_null_name(){
		//Arrange
		Header invalidHeader = new Header(null, "valid");
		CampaignHeaders campaignHeaders = CampaignHeaders.builder()
				.additionnalHeaders(Arrays.asList(invalidHeader)).build();
		//Act
		Assertions.assertThrows(IllegalArgumentException.class, () -> 
		dataValidator.validateHeaders(campaignHeaders));

		//Assert
		//=> exception thrown
	}

	
	@Test
	public void should_throw_exception_when_additionnal_Headers_contain_header_with_null_value(){
		//Arrange
		Header invalidHeader = new Header("valid", null);
		CampaignHeaders campaignHeaders = CampaignHeaders.builder()
				.additionnalHeaders(Arrays.asList(invalidHeader)).build();
		//Act
		Assertions.assertThrows(IllegalArgumentException.class, () -> 
		dataValidator.validateHeaders(campaignHeaders));

		//Assert
		//=> exception thrown
	}

	@ParameterizedTest
	@ValueSource(strings = {";invalid", "invalid;", "inv;alid", ":invalid", "invalid:", "inv:alid"})
	public void should_throw_exception_when_additionnalHeaders_contain_header_with_value_having_illegal_character(String invalidCharacter){
		//Arrange
		Header invalidHeader = new Header("valid", invalidCharacter);
		CampaignHeaders campaignHeaders = CampaignHeaders.builder()
				.additionnalHeaders(Arrays.asList(invalidHeader)).build();
		//Act
		Assertions.assertThrows(IllegalArgumentException.class, () -> 
		dataValidator.validateHeaders(campaignHeaders));

		//Assert
		//=> exception thrown
	}
	
	@ParameterizedTest
	@ValueSource(strings = {";invalid", "invalid;", "inv;alid", ":invalid", "invalid:", "inv:alid"})
	public void should_throw_exception_when_additionnalHeaders_contain_header_with_name_having_illegal_character(String invalidCharacter){
		//Arrange
		Header invalidHeader = new Header(invalidCharacter, "valid");
		CampaignHeaders campaignHeaders = CampaignHeaders.builder()
				.additionnalHeaders(Arrays.asList(invalidHeader)).build();
		//Act
		Assertions.assertThrows(IllegalArgumentException.class, () -> 
		dataValidator.validateHeaders(campaignHeaders));

		//Assert
		//=> exception thrown
	}
	
	@Test
	public void should_not_throw_exception_when_headers_are_valid(){
		//Arrange
		Header invalidHeader = new Header("valid", "valid");
		CampaignHeaders campaignHeaders = CampaignHeaders.builder()
				.additionnalHeaders(Arrays.asList(invalidHeader)).build();
		//Act
		dataValidator.validateHeaders(campaignHeaders);

		//Assert
		//=> exception not thrown
	}
	
	@Test
	public void should_not_throw_exception_when_additionnal_headers_are_null(){
		//Arrange
		CampaignHeaders campaignHeaders = CampaignHeaders.builder()
				.additionnalHeaders(null).build();
		//Act
		dataValidator.validateHeaders(campaignHeaders);

		//Assert
		//=> exception not thrown
	}
	
	
}
