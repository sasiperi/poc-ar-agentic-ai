package com.example.agentic.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {
	
	
	@Bean
    ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.create(chatModel);
    }
	
	
	/*
	@Bean
    ChatClient chatClient(ChatClient.Builder builder){
        return builder.build();
    }
    */
	

}
