package com.example.agentic.service.agents;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.agentic.domain.StatementData;
import com.example.agentic.repository.StatementRepository;

import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class StatementExtractorAgent {
	
	@Autowired
	private ChatClient chatClient;
	
	
	@Value("classpath:agents/agent01-read-extract-statement.prompt")
    private Resource extractAgentRes;
	
	@Autowired
    private StatementRepository statementRepo;
	

	public StatementData extractStatement(File file) {
		
		// Agent 1: Extract PDF with PagePdfDocumentReader
		Resource pdfResource = new FileSystemResource(file);
		
		 var config = PdfDocumentReaderConfig.builder()
					.withPageTopMargin(0)
					.withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
						.withNumberOfTopTextLinesToDelete(0)
						.build())
					.withPagesPerDocument(1)
					.build();
		
		
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource, config);
            
        List<Document> documents = pdfReader.read();
        
        String rawText = documents.stream()
        			  .map(doc -> doc.getFormattedContent())
        			  .reduce("", (a, b) -> a + "\n" + b);

        StatementData statement = extractStructuredData(rawText);
        statementRepo.save(statement);
        
        log.info("extracted: {} " +  statement.toString());
        
		return statement;
		
	}
	
	private StatementData extractStructuredData(String rawText) {
        try {
        	
        	  PromptTemplate template = new PromptTemplate(extractAgentRes);
        	  Prompt prompt = template.create(Map.of("text", rawText));
        	  StatementData statementData = chatClient
        			  					    .prompt(prompt)
        			  					    .call()
        			  					    .entity(StatementData.class);
        	 
        	  return statementData;
        	
        } catch (Exception e) {
            throw e;
        }
    }


}
