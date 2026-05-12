package com.mizcausevic.kafkarealtimeattribution.streams;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

public final class KafkaTopologyFactory {
    private KafkaTopologyFactory() {
    }

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream("ad-clicks")
                // The real processor would deserialize event payloads and join click,
                // session, and conversion records before sinking weighted attribution.
                .mapValues(value -> "touch-event:" + value)
                .to("touch-enriched");

        builder.stream("touch-enriched")
                .mapValues(value -> "attribution-credit:" + value)
                .to("channel-attribution");

        return builder.build();
    }

    public static Map<String, String> summary() {
        return Map.of(
                "sourceTopics", "ad-clicks, sessions, conversions",
                "intermediateTopic", "touch-enriched",
                "sinkTopic", "channel-attribution",
                "processingMode", "weighted multi-touch attribution with replay-safe ledger semantics");
    }

    public static Properties streamProperties() {
        Properties properties = new Properties();
        properties.put("application.id", "kafka-real-time-attribution");
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("default.key.serde", Serdes.StringSerde.class);
        properties.put("default.value.serde", Serdes.StringSerde.class);
        return properties;
    }
}
