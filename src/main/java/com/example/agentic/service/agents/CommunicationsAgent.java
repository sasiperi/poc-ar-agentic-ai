package com.example.agentic.service.agents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.agentic.domain.ValidationResult;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommunicationsAgent {
	@Autowired
    private JavaMailSender mailSender;
	
	public void sendReviewEmail(ValidationResult result) 
	{
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("review@acme.com");
        message.setSubject("Statement Needs Review");
        message.setText("Statement #" + result.getStatementId() + " needs review: " + result.getReason());
        mailSender.send(message);
        
        log.info("Sent Mail for Rview");
    }

}
