package com.example.kafkastreamktable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.web.context.ContextLoader;

import java.util.Map;

public class ItemDerializer implements Deserializer<Item> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Item deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Item.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void close() {

    }
}
