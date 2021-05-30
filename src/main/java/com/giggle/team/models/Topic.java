package com.giggle.team.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Document(collection = "topics")
@TypeAlias("topic")
@NoArgsConstructor
@Getter
@Setter
public class Topic {
    @Id
    private ObjectId id;
    private List<UserEntity> users = new LinkedList<>();
    private String kafkaTopic;
    private String stompDestination;

    public Topic(String kafkaTopic, String stompDestination) {
        this.kafkaTopic = kafkaTopic;
        this.stompDestination = stompDestination;
    }

    public void addUser(UserEntity user) {
        users.add(user);
    }

}
