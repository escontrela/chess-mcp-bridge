package com.davidp.chessjourney.chess_mcp_bridge;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ToolTestController {
    private final ActiveUserTool tool;

    ToolTestController(ActiveUserTool tool) {
        this.tool = tool;
    }

    @GetMapping("/test/getActiveUser")
    public Object testTool() {
        return tool.getActiveUser();
    }
}