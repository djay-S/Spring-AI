package org.demo.geminigenai.multimodality;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/multimodal")
public class MultimodalityController {

    private final ChatClient chatClient;

    @Value("/idea.png")
    private ClassPathResource imageResource;

    public MultimodalityController(ChatClient.Builder builder) {
        this.chatClient = builder.defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }

    @GetMapping("/image")
    public String image() {
        return chatClient
                .prompt()
                .user(u -> u.text("Explain the image.").media(MimeTypeUtils.IMAGE_PNG, imageResource))
                .call()
                .content();
    }

    @GetMapping("/upload")
    public String upload(
            @RequestParam("upload") final MultipartFile file,
            @RequestParam(value = "text", defaultValue = "Explain the upload.") final String text) {
        MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
        return chatClient
                .prompt()
                .user(u -> u.text(text).media(mimeType, file.getResource()))
                .call()
                .content();
    }
}
