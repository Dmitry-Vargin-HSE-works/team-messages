package com.giggle.team.storage;

import com.giggle.team.services.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageStorage {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final List<String> arrayList = new ArrayList<>();

    public void add(String message) {
        arrayList.add(message);
    }

    public String toString() {
        log.info("Calling MessageStorage.toString(");
        StringBuilder info = new StringBuilder();
        arrayList.forEach(info::append);
        log.info("info :" + info.toString());
        return info.toString();
    }

    public void clear() {
        arrayList.clear();
    }
}
