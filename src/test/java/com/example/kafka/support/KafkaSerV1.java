package com.example.kafka.support;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class KafkaSerV1 extends KafkaAvroSerializer {
    public KafkaSerV1() {
        super.schemaRegistry = new MockSchemaRegistryClient();
    }
}