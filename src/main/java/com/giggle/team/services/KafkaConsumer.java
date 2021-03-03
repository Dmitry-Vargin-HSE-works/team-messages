package com.giggle.team.services;

import com.giggle.team.models.ChatMessage;
import com.giggle.team.storage.MessageStorage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private  MessageStorage storage;

    @Autowired
    private  SimpMessagingTemplate template;

    private final String kafkaTopic = "Chat-Topic";

    /**
     * Receives messages from a specified Topic and sending it to to Websocket.
     */
    @KafkaListener(topics = "Chat-Topic")
    public void consumer(ConsumerRecord<?, ?> consumerRecord) {
        String[] message = consumerRecord.value().toString().split("-");
        log.info("Consumed data : '{}' from Kafka Topic : {}", Arrays.toString(message), kafkaTopic);
        storage.add(Arrays.toString(message));
        // just to show message received from topic. not needed as such.
        // below line sends data to websocket i.e to web browser
        this.template.convertAndSend("/topic/public",
                new ChatMessage(message[0], message[1], message[2], ChatMessage.MessageType.valueOf(message[3])));
    }

}