package com.giggle.team.models;

import com.fasterxml.jackson.annotation.JsonView;
import com.giggle.team.config.View;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.TreeSet;

@Document(collection = "users")
@TypeAlias("user")
@NoArgsConstructor
@Setter
@Getter
public class User {

    @Id
    private ObjectId id;

    @JsonView(View.Rest.class)
    private String name;
    @JsonView(View.Rest.class)
    private String email;
    @JsonView(View.Rest.class)
    private String password;

    private TreeSet<Topic> topics;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }



}
