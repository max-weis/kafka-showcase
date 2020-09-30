/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.showcase.kafka.producer;

import io.restassured.RestAssured;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.kafka.KafkaConsumerClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.openknowledge.showcase.kafka.producer.AppContainerConfig.KAFKA_PRODUCER;
import static de.openknowledge.showcase.kafka.producer.AppContainerConfig.TOPIC;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A test class that verifies that a producer can send a message.
 */
@SharedContainerConfig(AppContainerConfig.class)
@MicroShedTest
public class ProducerIT {

  @KafkaConsumerClient(groupId = "kafka-consumer", topics = TOPIC,
      keyDeserializer = StringDeserializer.class, valueDeserializer = TestCustomMessageDeserializer.class)
  public static KafkaConsumer<String, CustomMessage> consumer;

  @Test
  void sendMessage() throws Exception {
    // first poll throws an exception. polling twice resets the offset and fixes the problem
    consumer.poll(Duration.ofSeconds(1));

    UUID uuid = UUID.randomUUID();

    RestAssured.given()
        .queryParam("msg", uuid.toString())
        .when()
        .get("/api/messages")
        .then()
        .statusCode(202);

    List<ConsumerRecord<String, CustomMessage>> records = new ArrayList<>();
    consumer.poll(Duration.ofSeconds(30))
        .records(TOPIC)
        .forEach(records::add);

    assertThat(records).hasSize(1);
    assertThat(records.get(0).value()).isEqualTo(new CustomMessage(uuid.toString()));
  }
}
