package org.demo.geminigenai.restaurant;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RestaurantChatMemory {

    private final DataSource dataSource;

    @Bean
    PromptChatMemoryAdvisor promptChatMemoryAdvisor() {
        JdbcChatMemoryRepository jdbcChatMemoryRepository =
                JdbcChatMemoryRepository.builder().dataSource(dataSource).build();

        MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .build();

        return PromptChatMemoryAdvisor.builder(messageWindowChatMemory).build();
    }
}
