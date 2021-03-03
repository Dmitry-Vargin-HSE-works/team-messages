package com.giggle.team.config;

import com.giggle.team.listener.UserListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CollectionsConfig {
    @Bean
    public Map<String, ArrayList<UserListener>> listenersMap(){
        return new HashMap<>();
    }
}
