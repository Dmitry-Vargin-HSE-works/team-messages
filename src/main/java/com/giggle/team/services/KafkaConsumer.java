package com.giggle.team.services;


import com.giggle.team.entities.ChatMessage;
import com.giggle.team.storage.MessageStorage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final MessageStorage storage;

    private final SimpMessagingTemplate template;

    @Value("chat-topic")
    String kafkaTopic = "topic";

    @Autowired
    public KafkaConsumer(MessageStorage storage, SimpMessagingTemplate template) {
        this.storage = storage;
        this.template = template;
    }

    /**
     * Receives messages from a specified Topic and sending it to to Websocket.
     */
    @KafkaListener(topics = "chat-topic")
    public void consumer(ConsumerRecord<?, ?> consumerRecord) {
        String[] message = consumerRecord.value().toString().split("-");
        log.info("Consumed data : '{}' from Kafka Topic : {}", Arrays.toString(message), kafkaTopic);
        storage.add(Arrays.toString(message)); // just to show message received from topic. not needed as such.
        // below line sends data to websocket i.e to web browser
        this.template.convertAndSend("/topic/public",
                new ChatMessage(message[0], message[1]));
    }
}