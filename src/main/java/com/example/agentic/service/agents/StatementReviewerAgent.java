package com.example.agentic.service.agents;


import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.agentic.domain.StatementData;
import com.example.agentic.domain.ValidationResult;
import com.example.agentic.repository.ValidationResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StatementReviewerAgent {
	
	private Map<String, AccountTypeRules> validationRules;
	
	@Autowired
	ValidationResultRepository resultRepo;
	

	@Value("classpath:rules.yml")
    private Resource rulesRes;
	
	
	@PostConstruct
    public void init() throws IOException {
        
        loadValidationRules();
        
    }
	
	
	public ValidationResult validateStatement(StatementData statement) 
	{
		 
        String accountType = getAccountType(statement.getAccountId());
        AccountTypeRules rules = validationRules.getOrDefault(accountType, validationRules.get("standard"));
        String status;
        String reason;
        
        List<String> violations = new ArrayList<>();
        for (ValidationRule rule : rules.rules()) {
            if (evaluateRule(rule, statement)) {
              
            	violations.add(rule.id() + " : " + rule.reason() + ";");
                
            } 
        }

        if (violations.isEmpty()) {
        	status = "Approved";
        	reason = "All checks passed";
        } else {
        	status = "Failed";
        	reason = "Failed Rules" + violations.toString();
        }
        
        ValidationResult validationResult = new ValidationResult(statement.getAccountId(), status, reason);
        resultRepo.save(validationResult);
        
        log.info("reviewed result: {} " +  validationResult.toString());
        
        return validationResult;
        
	}
	
	 
	private void loadValidationRules() throws IOException {
        
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        validationRules = mapper.readValue(rulesRes.getFile(), ValidationRulesConfig.class).accountTypes();
    }
	
	 // This would a real rule framework like drools
	 // returns if rules passes, which means checks failed. And it's invalid statement.
	 private boolean evaluateRule(ValidationRule rule, StatementData statement) {
	        AccountTypeRules accountRules = validationRules.get(getAccountType(statement.getAccountId()));
	        switch (rule.condition()) {
	            case "amount > 10000":
	                return statement.getAmount() > 10000;
	            case "amount > 50000":
	                return statement.getAmount() > 50000;
	            case "daysSinceDate > 30":
	                return ChronoUnit.DAYS.between(statement.getDate(), LocalDate.now()) > 30;
	            case "daysSinceDate > 60":
	                return ChronoUnit.DAYS.between(statement.getDate(), LocalDate.now()) > 60;
	            case "invalidItems":
	                return !accountRules.rules().stream()
	                    .filter(r -> r.id().equals("valid_items"))
	                    .findFirst()
	                    .map(r -> r.validItems().contains(statement.getItems()))
	                    .orElse(false);
	            default:
	                return false;
	        }
	    }

	    private String getAccountType(String accountId) {
	        return validationRules.entrySet().stream()
	            .filter(entry -> entry.getValue().approvedAccounts().contains(accountId))
	            .map(Map.Entry::getKey)
	            .findFirst()
	            .orElse("standard");
	    }

}
record ValidationRulesConfig(Map<String, AccountTypeRules> accountTypes) {}
record AccountTypeRules(List<String> approvedAccounts, List<ValidationRule> rules) {}
record ValidationRule(String id, String condition, String reason, int priority, List<String> validItems) {
    ValidationRule {
        if (validItems == null) {
            validItems = List.of();
        }
    }
}
