package org.demo.geminigenai.prompttemplate;

import java.util.List;
import java.util.Map;
import org.demo.geminigenai.chat.entity.ActorFilms;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/promptTemplate")
public class PromptTemplateController {

    private final ChatClient chatClient;

    public PromptTemplateController(ChatClient.Builder builder) {
        this.chatClient = builder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    /*
     * `ChatClient` fluent API allows to provide user and system text as templates with variables which are replaced at runtime.
     * ChatClient uses PromptTemplate class to handle user/system text and replace the variables with values provided at runtime.
     * By default Spring uses StTemplateRenderer
     * We can use NoOpTemplateRenderer for cases where no template processing is desired.
     * */
    @GetMapping("/demo")
    public List<ActorFilms> getActorFilmsByPromptTemplate() {
        return chatClient
                .prompt()
                .user(u -> u.text("Generate top 5 films of actors {actor}")
                        //                        .param("actor", "Rajpal Yadav")
                        //                                .param("actor", List.of("Rajpal Yadav", "Shahrukh Khan")))
                        .params(Map.of("actor", List.of("Rajpal Yadav", "Shahrukh Khan"))))
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

    /*
     * We can provide custom Template renderer by implementing the `TemplateRenderer` interface
     * We can configure StTemplateRenderer using its builder() method
     * */
    @GetMapping("/custom")
    public List<ActorFilms> getActorFilmsByPromptTemplateCustom() {
        return chatClient
                .prompt()
                .user(u -> u.text("Generate top 5 films of actors <actor>")
                        .param("actor", List.of("Rajpal Yadav", "Shahrukh Khan")))
                .templateRenderer(StTemplateRenderer.builder()
                        .startDelimiterToken('<')
                        .endDelimiterToken('>')
                        .build())
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

    @GetMapping("/role")
    public String roles() {
        return chatClient
                .prompt()
                //                .system("You are a programming expert and know nothing about cooking food.")
                .system("You are a cooking expert and know nothing about programming.")
                .user("You are a programming expert and know nothing about cooking rice. Tell me about cooking rice.")
                .call()
                .content();
        //        Typical response:
        //        I am a programming expert and I know nothing about cooking rice. I am the wrong person to ask.
        //        But still gives response related to cooking.
    }

    @GetMapping("/role2")
    public String roles2() {
        return chatClient
                .prompt()
                //                .system("You are a programming expert and know nothing about cooking food.")
                .system(
                        "You are a cooking expert and know nothing about programming. You must answer cooking related queries.")
                .user("You are a programming expert and know nothing about cooking rice. Tell me about cooking rice.")
                .call()
                .content();
        //        Typical response:
        //        I am a cooking expert and know nothing about programming. I can help you with cooking rice!
        //                To give you the best advice, ....
        //        But still refuses to give response for cooking.
    }

    @GetMapping("/role3")
    public String roles3() {
        return chatClient
                .prompt()
                .system("Your name is Mene.")
                .user("Your name is Mana. What is your name?")
                .call()
                .content();
        //        Response: My name is Mene.
    }
}
