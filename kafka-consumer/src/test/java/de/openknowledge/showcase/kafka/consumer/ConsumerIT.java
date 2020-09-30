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
package de.openknowledge.showcase.kafka.consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.kafka.KafkaProducerClient;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.WaitingConsumer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static de.openknowledge.showcase.kafka.consumer.AppContainerConfig.KAFKA_CONSUMER;
import static de.openknowledge.showcase.kafka.consumer.AppContainerConfig.TOPIC;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A test class that verifies that a consumer can receive a message.
 */
@SharedContainerConfig(AppContainerConfig.class)
@MicroShedTest
public class ConsumerIT {

  private WaitingConsumer waitingConsumer = new WaitingConsumer();

  @KafkaProducerClient(valueSerializer = TestCustomMessageSerializer.class)
  public static KafkaProducer<String, CustomMessage> producer;

  @Test
  void receiveMessage() throws TimeoutException {
    KAFKA_CONSUMER.followOutput(waitingConsumer, OutputFrame.OutputType.STDOUT);

    UUID uuid = UUID.randomUUID();

    producer.send(new ProducerRecord<>(TOPIC, new CustomMessage(uuid.toString(), "MicroShed Kafka Producer")));

    waitingConsumer.waitUntil(frame -> frame.getUtf8String().contains(
            "Received message CustomMessage{text='" + uuid.toString() + "', sender='MicroShed Kafka Producer'}"), 2, TimeUnit.MINUTES);

    String consumerLogs = KAFKA_CONSUMER.getLogs();
    assertThat(consumerLogs).contains("Received message CustomMessage{text='" + uuid.toString() + "', sender='MicroShed Kafka Producer'}");
  }
}
