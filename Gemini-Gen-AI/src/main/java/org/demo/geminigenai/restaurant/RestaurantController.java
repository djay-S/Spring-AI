package org.demo.geminigenai.restaurant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    private final ChatClient chatClient;

    private final String SYSTEM_PROMPT =
            "Your name is Raj. You are a waiter at 'Desi Restaurant'. Your task is to take orders from customers and suggest them dishes based on customer preference and restaurant menu.";

    public RestaurantController(
            ChatClient.Builder builder, PromptChatMemoryAdvisor chatMemory, VectorStore vectorStore) {
        this.chatClient = builder.defaultAdvisors(
                        QuestionAnswerAdvisor.builder(vectorStore).build())
                .defaultAdvisors(chatMemory)
                .build();
    }

    @GetMapping("/assistant")
    public String inquire(@RequestParam(value = "question", defaultValue = "Hi") final String question) {
        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(question)
                .advisors(adv -> adv.param(ChatMemory.CONVERSATION_ID, "waiter"))
                .call()
                .content();
    }
}
