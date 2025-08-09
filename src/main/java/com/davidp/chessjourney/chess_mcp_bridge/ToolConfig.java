package com.davidp.chessjourney.chess_mcp_bridge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;

@Configuration
public class ToolConfig {
  @Bean
  ToolCallbackProvider toolProvider(ActiveUserTool activeUserTool) {
    return MethodToolCallbackProvider
        .builder()
        .toolObjects(activeUserTool)  // recoge métodos anotados con @Tool
        .build();
  }
}