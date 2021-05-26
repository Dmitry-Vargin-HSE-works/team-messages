package com.giggle.team.repositories;

import com.giggle.team.models.Topic;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends MongoRepository<Topic, ObjectId> {

  List<Topic> findAll();

  Topic findByStompDestination(String destination);
}
