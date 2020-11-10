package com.giggle.team.repositories;

import com.giggle.team.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.giggle.team")
public interface UserRepository extends MongoRepository<User, String> {
}
