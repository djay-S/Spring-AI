package org.demo.geminigenai.chat;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.demo.geminigenai.chat.entity.ActorFilms;
import org.demo.geminigenai.chat.entity.Student;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {
    private final ChatClient chatClient;
    private final ChatModel chatModel;

    public ChatController(ChatClient.Builder builder, ChatModel chatModel) {
        this.chatClient = builder
                //        SimpleLoggerAdvisor can also be added during the prompt call using the .advisors() method
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.chatModel = chatModel;
    }

    @GetMapping("/chat")
    public String chat() {
        return chatClient.prompt().user("What is your name?").call().content();
    }

    //    To get the streaming response use: curl -N --location 'localhost:8080/stream'
    @GetMapping("/stream")
    Flux<String> stream() {
        return chatClient.prompt().user("I am visiting Hyderabad. Can you suggest me 10 restaurants to visit.").stream()
                .content()
                .delayElements(Duration.ofSeconds(1));
    }

    @GetMapping("/response")
    public ChatResponse getChatResponse() {
        return chatClient.prompt().user("What is the current timestamp?").call().chatResponse();
    }

    @GetMapping("/entity")
    public ActorFilms getActorFilms() {
        return chatClient
                .prompt()
                .user("Generate top 5 films of actor Rajpal Yadav")
                .call()
                .entity(ActorFilms.class);
    }

    @GetMapping("/entity/chat-model")
    public ActorFilms getActorFilmsLowLevel() throws Exception {
        BeanOutputConverter<ActorFilms> beanOutputConverter = new BeanOutputConverter<>(ActorFilms.class);
        final String format = beanOutputConverter.getFormat();
        final String template = "Generate top 5 films of actor Rajpal Yadav. {format}";
        final Generation generation = chatModel
                .call(PromptTemplate.builder()
                        .template(template)
                        .variables(Map.of("format", format))
                        .build()
                        .create())
                .getResult();
        String response = generation.getOutput().getText();
        if (response == null) {
            throw new Exception();
        }
        return beanOutputConverter.convert(response);
    }

    /*
     * In the above example, Spring AI appends format instruction to the user text to match the mentioned entity
     * eg: "Respond in JSON format matching the following schema" followed by the JSON for ActorFilms.class
     * Since this is part of the user instructions, the model can include text like "Sure, here is the JSON ..."
     *
     * By using advisors, Spring AI uses capability of the model itself eg: OpenAI's "JSON Mode" or "Structured Outputs," or Gemini's response schema
     * The schema is sent as a specific parameter in the API request body.
     * The model’s output is constrained at the token-generation level to ensure it matches the Java class structure.
     *
     * */
    @GetMapping("/entity/advisor")
    public ActorFilms getActorFilmsViaAdvisor() {
        return chatClient
                .prompt()
                .user("Generate top 5 films of actor Rajpal Yadav")
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .call()
                .entity(ActorFilms.class);
    }

    @GetMapping("/entities")
    public List<ActorFilms> getActorFilmsList() {
        return chatClient
                .prompt()
                .user("Generate top 5 films of actor Rajpal Yadav, Shahrukh Khan, Akshay Kumar")
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

    /*
     * This gives both the `ChatReponse` and entity response
     * */
    @GetMapping("responseEntity")
    public ResponseEntity<ChatResponse, ActorFilms> getActorFilmsResponseEntity() {
        return chatClient
                .prompt()
                .user("Generate top 5 films of actor Rajpal Yadav")
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .call()
                .responseEntity(ActorFilms.class);
    }

    /*
     * `ChatClient` fluent API allows to provide user and system text as templates with variables which are replaced at runtime.
     * ChatClient uses PromptTemplate class to handle user/system text and replace the variables with values provided at runtime.
     * By default Spring uses StTemplateRenderer
     * We can use NoOpTemplateRenderer for cases where no template processing is desired.
     * */
    @GetMapping("/promptTemplate")
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

    @GetMapping("/generate/student")
    public List<Student> getStudents() {
        return chatClient
                .prompt()
                .user("Give me list of 5 students")
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .call()
                .entity(new ParameterizedTypeReference<List<Student>>() {});
    }

    @GetMapping("/generate/student/chat-model")
    public Student[] getStudentsChatModel() throws Exception {
        final BeanOutputConverter<Student[]> beanOutputConverter = new BeanOutputConverter<>(Student[].class);
        //        final BeanOutputConverter<Student[]> beanOutputConverter1 = new BeanOutputConverter<>(new
        // ParameterizedTypeReference<Student[]>() {});
        final String format = beanOutputConverter.getFormat();
        final String template = "Give me list of 5 students. {format}";
        final Generation generation = chatModel
                .call(PromptTemplate.builder()
                        .template(template)
                        .variables(Map.of("format", format))
                        .build()
                        .create())
                .getResult();
        String text = generation.getOutput().getText();
        if (text == null) {
            throw new Exception();
        }
        return beanOutputConverter.convert(text);
    }
}
