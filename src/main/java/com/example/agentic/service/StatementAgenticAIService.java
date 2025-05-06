package com.example.agentic.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.agentic.domain.StatementData;
import com.example.agentic.domain.ValidationResult;
import com.example.agentic.service.agents.CommunicationsAgent;
import com.example.agentic.service.agents.StatementExtractorAgent;
import com.example.agentic.service.agents.StatementReviewerAgent;



@Service
public class StatementAgenticAIService {
	
	@Autowired
	StatementExtractorAgent stmtReadAgent;
	
	@Autowired
	StatementReviewerAgent stmtReviewAgent;
	
	@Autowired
	CommunicationsAgent commsAgent;
	
	public void doStatementProcessing(File file)
	{
		//Agent - 01: Processing/teading statements
		StatementData readStatement = stmtReadAgent.extractStatement(file);
		
		//Agent - 02:  Reviewing the statements and validating, with rules.
		ValidationResult result = stmtReviewAgent.validateStatement(readStatement);
		
		//Agent - 03:  Managing communications. Sending mails for manual review, for fails
		if("Failed".equals(result.getStatus()))
			commsAgent.sendReviewEmail(result);
	}
	
	
}

