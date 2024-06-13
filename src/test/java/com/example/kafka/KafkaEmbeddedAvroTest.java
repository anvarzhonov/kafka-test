package com.example.kafka;

import com.example.kafka.support.TestKafkaConfig;
import example.avro.User;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import static com.example.kafka.KafkaEmbeddedStringTest.TEST_TOPIC;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestKafkaConfig.class)
@RunWith(SpringRunner.class)
public class KafkaEmbeddedAvroTest {
    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;
    @Autowired
    private Consumer<String, User> consumer;

    @Test
    public void testReceivingKafkaEvents() {
        // Arrange
        User user = User.newBuilder()
                .setId(1)
                .setName("alesha")
                .setFavoriteColor("red")
                .build();

        // Act
//        kafkaTemplate.send(new ProducerRecord<>(TEST_TOPIC, "key", user));
        kafkaTemplate.send(TEST_TOPIC, "key", user);

        // Assert
        ConsumerRecord<String, User> singleRecord = KafkaTestUtils.getSingleRecord(consumer, TEST_TOPIC);
        System.out.println(singleRecord);
        assertThat(singleRecord).isNotNull();
        assertThat(singleRecord.value()).isEqualTo(user);

    }

    @After
    public void afterAll() {
        consumer.close();
    }
}
