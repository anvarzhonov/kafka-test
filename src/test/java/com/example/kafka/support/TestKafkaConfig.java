package com.example.kafka.support;

import example.avro.User;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.EmbeddedKafkaKraftBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Collections;
import java.util.Map;

import static com.example.kafka.KafkaEmbeddedStringTest.TEST_TOPIC;

@TestConfiguration
@AllArgsConstructor
@Slf4j
public class TestKafkaConfig {
//    private final EmbeddedKafkaBroker embeddedKafkaBroker;

    @Bean
    public EmbeddedKafkaBroker embeddedKafkaBroker() {
        return new EmbeddedKafkaKraftBroker(1, 1, TEST_TOPIC);
    }

    @Bean
    MockSchemaRegistryClient mockSchemaRegistryClient() {
        return new MockSchemaRegistryClient();
    }

    @Bean
    public DefaultKafkaProducerFactory producerFactory() {
        Map<String, Object> props = KafkaTestUtils.producerProps(embeddedKafkaBroker());
        props.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, true);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaSerV1.class.getName());
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        return new DefaultKafkaProducerFactory(props, new StringSerializer(), new KafkaSerV1());
    }

    @Bean
    public ConsumerFactory<String, example.avro.User> consumerFactory() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaDerV1.class.getName());
        consumerProps.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        consumerProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    @Bean
    public Consumer<String, example.avro.User> consumer() {
        Consumer<String, example.avro.User> consumer = consumerFactory().createConsumer();
        consumer.subscribe(Collections.singleton(TEST_TOPIC));
        return consumer;
    }

    @Bean
    public KafkaTemplate<String, example.avro.User> kafkaTemplate() {
        KafkaTemplate<String, example.avro.User> kafkaTemplate = new KafkaTemplate<String, example.avro.User>(producerFactory());
        return kafkaTemplate;
    }


//    @Bean
//    public ContainerProperties containerProperties() {
//        ContainerProperties containerProperties = new ContainerProperties(TEST_TOPIC);
//        containerProperties.setMessageListener((MessageListener<String, Object>) record -> {
//            log.info("Сообещение получено: {}", record.value());
//        });
//        return containerProperties;
//    }
//
//    @Bean
//    KafkaMessageListenerContainer kafkaMessageListenerContainer() {
//        var listenerContainer = new KafkaMessageListenerContainer<>(consumerFactory(), containerProperties());
//        listenerContainer.start();
//
//        return null;
//    }

}
