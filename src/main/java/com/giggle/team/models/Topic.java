package com.giggle.team.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Topic {
    @Id
    private ObjectId id;
}
