package org.demo.geminigenai.toolcalling;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tool")
public class ToolController {

    private final ChatClient chatClient;

    public ToolController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/call")
    public String inquire(@RequestParam(value = "question", defaultValue = "Hi") final String question) {
        return chatClient.prompt(question).tools(new DateTimeTools()).call().content();
    }
}
