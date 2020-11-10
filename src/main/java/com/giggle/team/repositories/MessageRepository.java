package com.giggle.team.repositories;

import com.giggle.team.entities.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.giggle.team")
public interface MessageRepository extends MongoRepository<Message, String> {
}
