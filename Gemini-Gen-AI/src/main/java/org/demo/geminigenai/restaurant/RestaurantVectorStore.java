package org.demo.geminigenai.restaurant;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.geminigenai.restaurant.entity.FoodRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantVectorStore implements ApplicationRunner {

    private final VectorStore vectorStore;
    private final FoodRepository foodRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Restaurant Vector Store started");
        Integer count = vectorStore
                .similaritySearch(SearchRequest.builder().query("test").topK(1).build())
                .size();
        log.info("Restaurant Vector Store count: {}", count);
        if (count < 1) {
            int foodCount = foodRepository.findAll().size();
            log.info("Restaurant Database food count: {}", foodCount);
            foodRepository.findAll().forEach(food -> {
                Document document = new Document("id: %s, name: %s, description: %s, price: %s"
                        .formatted(food.getFoodId(), food.getFoodName(), food.getDescription(), food.getPrice()));
                vectorStore.add(List.of(document));
            });
            log.info("Restaurant Vector Store updated");
        }
    }
}
