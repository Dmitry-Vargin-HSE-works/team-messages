package com.giggle.team.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.TreeSet;

@Document(collection = "topics")
@Getter
@Setter
public class Topic {
    @Id
    private ObjectId id;
    private TreeSet<UserEntity> users;
    private String kafkaTopic;
    private String stompDestination;

    public Topic(String kafkaTopic, String stompDestination) {
        this.kafkaTopic = kafkaTopic;
        this.stompDestination = stompDestination;
        this.users = new TreeSet<>();
    }

    public void addUser(UserEntity user){
        users.add(user);
    }
}
