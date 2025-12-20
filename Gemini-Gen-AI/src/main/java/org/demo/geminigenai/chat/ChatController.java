package org.demo.geminigenai.chat;

import org.demo.geminigenai.chat.entity.ActorFilms;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

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

    @GetMapping("/response")
    public ChatResponse getChatResponse() {
        return chatClient.prompt()
                .user("What is the current timestamp?")
                .call()
                .chatResponse();
    }

    @GetMapping("/entity")
    public ActorFilms  getActorFilms() {
        return chatClient.prompt()
                .user("Generate the filmography of actors Rajpal Yadav")
                .call()
                .entity(ActorFilms.class);
    }

    @GetMapping("/entities")
    public List<ActorFilms> getActorFilmsList() {
        return chatClient.prompt()
                .user("Generate the filmography of actors Rajpal Yadav, Shahrukh Khan, Akshay Kumar")
                .call()
                .entity(new ParameterizedTypeReference<List<ActorFilms>>(){});
    }
}
