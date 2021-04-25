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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.TreeSet;

@Document(collection = "users")
@TypeAlias("user")
@NoArgsConstructor
@Setter
@Getter
public class User implements UserDetails {

    @Id
    private ObjectId id;

    @JsonView(View.Rest.class)
    private String name;
    @JsonView(View.Rest.class)
    private String username;
    @JsonView(View.Rest.class)
    private String password;

    private TreeSet<Topic> topics;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
