package com.example.kafkastreamktable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
public class KafkaStreamKtableApplication implements CommandLineRunner {

    @Bean
    KafkaProducer<String, Item> kafkaProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");


        KafkaProducer<String, Item> kafkaProducer = new KafkaProducer<String, Item>(properties, Serdes.String().serializer(), ItemSerdes.itemSerdes().serializer());
        return  kafkaProducer;
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamKtableApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktable1");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, Item> inputStream = streamsBuilder.stream("ktable", Consumed.with(Serdes.String(), ItemSerdes.itemSerdes()));

        inputStream
                .groupBy((k, v) -> v.getId(), Serialized.with(Serdes.Integer(), ItemSerdes.itemSerdes()))
                .windowedBy(SessionWindows.with(1000L * 20).until(15 * 60 * 1000L))
                .aggregate(
                        () -> 1L,
                        (key, value, aggregate) -> {
                            if (aggregate == null) return (long) value.getCount();
                            return aggregate * value.getCount();
                        },
                        (aggKey, aggOne, aggTwo) -> aggOne * aggTwo,
                        Materialized.with(Serdes.Integer(), Serdes.Long())
                )
                .toStream()
                .peek((k, v) -> System.out.println(k.toString() + ": " + v.toString()));

        Topology topology = streamsBuilder.build();
        System.out.println(topology.describe());



        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        kafkaStreams.start();
    }
}
