package com.giggle.team.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Topic {
    @Id
    private ObjectId id;
    private String stompDestination;
}
