package com.example.kafkastreamktable;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ItemSerdes implements Serde<Item> {
    private static ItemSerialize itemSerialize = new ItemSerialize();
    private static ItemDerializer itemDerializer = new ItemDerializer();
    private static ItemSerdes itemSerdes = new ItemSerdes();

    public static ItemSerdes itemSerdes() {
        return itemSerdes;
    }

    private ItemSerdes() {

    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<Item> serializer() {
        return itemSerialize;
    }

    @Override
    public Deserializer<Item> deserializer() {
        return itemDerializer;
    }
}
