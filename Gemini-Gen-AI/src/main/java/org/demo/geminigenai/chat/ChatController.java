package org.demo.geminigenai.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat() {
        return chatClient.prompt()
                .user("What is your name?")
                .call()
                .content();
    }

//    To get the streaming response use: curl -N --location 'localhost:8080/stream'
    @GetMapping("/stream")
    Flux<String> stream() {
        return chatClient.prompt()
                .user("I am visiting Hyderabad. Can you suggest me 10 restaurants to visit.")
                .stream()
                .content()
                .delayElements(Duration.ofSeconds(1));
    }
}
