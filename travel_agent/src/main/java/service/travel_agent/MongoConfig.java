package service.travel_agent;

import service.travel_agent.MongoRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {
    @Bean
    public MongoRepository createMongoRepository() {
        final MongoRepository mongoRepository = new MongoRepository();
        mongoRepository.connectToCollection();
        return mongoRepository;
    }
}