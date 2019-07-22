package com.example.kafkastreamktable;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;

@RestController
public class HelloController {
    @Autowired
    KafkaProducer<String, Item> kafkaProducer;

    @GetMapping("/")
    public void producer(@RequestParam Integer count) {
        for (int i = 0; i < count; i++) {
            Item item = new Item(i % 2, i);
            ProducerRecord<String, Item> record = new ProducerRecord<>("ktable", item);
            kafkaProducer.send(record);
        }
    }
}
