package com.benayed.mailing.campaigns.service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.benayed.mailing.campaigns.CampaignHeaders;

@Service
public class CampaignService {
	
	public void sendit() {
		

	      String body = "mail body";
	      String to = "hashiramasenju47@gmail.com";
	      Header h1 = new Header("X-h1", "hola que passa");
	      Header h2 = new Header("X-h2", "hola que tal");

	      CampaignHeaders campaignHeaders = CampaignHeaders.builder()
			      .from("web@gmail.com")
			      .fromName("W-E-B")
			      .subject("My Mail Subject")
			      .replyTo("ReplyTo@adr.com")
			      .bounceAddr("return-path@gmail.com")
			      .received("received by me")
			      .additionnalHeaders(Arrays.asList(h1, h2))
			      .build();
	      send(campaignHeaders, body, to);
		
	}
	
	public void send(CampaignHeaders campaignHeaders, String body, String to){

	      // Get system properties
	      Properties properties = System.getProperties();
	      
	      // Setup mail server
	      properties.setProperty("mail.smtp.host", "smtp.mailtrap.io");
	      properties.put("mail.smtp.from", campaignHeaders.getBounceAddr());
	      
          String username = "1f8ddaa0ad6d5d";
          String password = "xxx";
          properties.put("mail.smtp.starttls.enable", "true");
	      properties.put("mail.smtp.port", "587");
	      properties.put("mail.smtp.auth", "true");
	      properties.put("mail.smtp.ssl.trust", "smtp.mailtrap.io"); // otherwise Could not convert socket to TLS;   nested exception is: 	javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	      
	      // Get the default Session object.
	      Session session = Session.getInstance(properties,
	              new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(username, password);
	          }
	        }); //Session.getDefaultInstance(properties);

	      try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(campaignHeaders.getFrom(), campaignHeaders.getFromName()));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	         // Set Subject: header field
	         message.setSubject(campaignHeaders.getSubject());
	     
	         Address[] replyToAddrs = {new InternetAddress(campaignHeaders.getReplyTo())};
	         message.setReplyTo(replyToAddrs);

	         message.setHeader("Received", campaignHeaders.getReceived());
	         
	         for(Header header : campaignHeaders.getAdditionnalHeaders()) {
	        	 message.addHeader(header.getName(), header.getValue());
	         }

	         message.setSentDate(new Date());

	         // Now set the actual message
	         message.setText(body);
	         
	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      } catch (MessagingException | UnsupportedEncodingException mex) {
	         mex.printStackTrace();
	      }
	}

}
