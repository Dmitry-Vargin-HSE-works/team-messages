package com.giggle.team.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.TreeSet;

@Getter
@Setter
public class Topic {
    @Id
    private ObjectId id;
    private TreeSet<UserEntity> users;
    private String kafkaTopic;
    private String stompDestination;
}
