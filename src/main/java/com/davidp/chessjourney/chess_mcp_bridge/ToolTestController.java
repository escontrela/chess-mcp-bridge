package com.davidp.chessjourney.chess_mcp_bridge;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ToolTestController {

    private final ActiveUserTool tool;

    ToolTestController(ActiveUserTool tool) {
        this.tool = tool;
    }

    @GetMapping("/test/getActiveUser")
    public Object testGetActiveUser() {

        return tool.getActiveUser();
    }

    @PostMapping("/test/postQuote")
    public Object testPostQuote(@RequestBody ActiveUserTool.Quote quote) {

        return tool.postQuote(quote);
    }
}