package org.demo.geminigenai.restaurant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    private final ChatClient chatClient;

    private final String SYSTEM_PROMPT =
            "You are a waiter at 'Desi Restaurant'. Your task is to take orders from customers and suggest them dishes based on customer preference and restaurant menu.";

    public RestaurantController(ChatClient.Builder builder, PromptChatMemoryAdvisor chatMemory) {
        this.chatClient = builder.defaultAdvisors(chatMemory).build();
    }

    @GetMapping("/assistant")
    public String inquire(@RequestParam(value = "question", defaultValue = "Hi") final String question) {
        return chatClient.prompt().system(SYSTEM_PROMPT).user(question).advisors(adv -> adv.param(ChatMemory.CONVERSATION_ID, "waiter")).call().content();
    }
}
