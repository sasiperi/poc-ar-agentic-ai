package com.example.agentic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.agentic.service.StatementAgenticAIService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class PocArAgenticAiApplication { //implements CommandLineRunner {
	
	///poc-ar-agentic-ai/src/main/resources/raw-statements
	// /poc-ar-agentic-ai/src/main/resources/reviewed-statements
	@Value("${app.raw-statements}")
    private String rawStatementsPath;
	
    @Value("${app.reviewed-statements}")
    private String reviewedStatementsPath;
    
    @Autowired
    StatementAgenticAIService stmtAgents;
    
    @Autowired
    private ResourceLoader resourceLoader;

	public static void main(String[] args) {
		SpringApplication.run(PocArAgenticAiApplication.class, args);
		
		
	}
	

	@Scheduled(fixedRate = 10000)
	public void checkFolder() throws Exception
	{
		Resource source = resourceLoader.getResource("classpath:" + rawStatementsPath);
		File dir = source.getFile();
		
		log.info("Agent started working: " + dir.getCanonicalPath() + "  Absolute: " + dir.getAbsolutePath()  + " PATH: " + dir.getPath());
		
		
	    
		for (File file : dir.listFiles(f -> f.getName().endsWith(".pdf"))) 
	    {
			log.info("Found File: " + file.getName());
			
			try 
			{
				
				stmtAgents.doStatementProcessing(file);
			    
			}catch(Exception e)
			{
				e.printStackTrace();
				
			}finally {
				
				Path sourceFile = file.toPath();
				
				Resource targetRes = resourceLoader.getResource("classpath:" + reviewedStatementsPath);
				Path target = Paths.get(targetRes.getFile().getCanonicalPath(), file.getName());
		        try 
		        {
		        	
		        	Files.move(sourceFile, target, StandardCopyOption.REPLACE_EXISTING);
	                log.info("File Moved to Target: " + target);
	                
	            } catch (IOException moveEx) {
	                log.error("Exception Occured while koving the file: " , moveEx);
	            }
	            
			}
		
	    }
	    

	}
	
	
	// For real time monitoring uncomment below.
	/*
	@Override
    public void run(String... args) throws Exception
    {
		startFolderMonitoring();
    }
	private void startFolderMonitoring() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(rawStatementsPath);
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path filePath = path.resolve((Path) event.context());
                            if (filePath.toString().endsWith(".pdf")) {
                                processStatement(filePath.toFile());
                            }
                        }
                    }
                    key.reset();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
  */

}
