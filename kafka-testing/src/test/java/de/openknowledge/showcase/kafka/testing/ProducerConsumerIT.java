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
package de.openknowledge.showcase.kafka.testing;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.WaitingConsumer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static de.openknowledge.showcase.kafka.testing.AppContainerConfig.KAFKA_CONSUMER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A test class that verifies that a consumer can receive a message send by a producer.
 */
@SharedContainerConfig(AppContainerConfig.class)
@MicroShedTest
public class ProducerConsumerIT {

  private WaitingConsumer waitingConsumer = new WaitingConsumer();

  @Test
  void sendAndReceiveMessage() throws TimeoutException {
    KAFKA_CONSUMER.followOutput(waitingConsumer, OutputFrame.OutputType.STDOUT);

    UUID uuid = UUID.randomUUID();

    RestAssured
        .given()
        .queryParam("msg", uuid.toString())
        .when()
        .get("/api/messages")
        .then()
        .statusCode(202);

    waitingConsumer.waitUntil(frame -> frame.getUtf8String()
        .contains("Received message CustomMessage{text='" + uuid.toString() + "', sender='Reactive Messaging Producer'}"), 2, TimeUnit.MINUTES);

    String consumerLogs = KAFKA_CONSUMER.getLogs();
    assertThat(consumerLogs).contains("Received message CustomMessage{text='" + uuid.toString() + "', sender='Reactive Messaging Producer'}");
  }
}
