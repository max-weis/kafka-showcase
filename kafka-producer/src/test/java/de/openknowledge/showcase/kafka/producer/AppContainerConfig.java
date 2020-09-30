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

import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;

/**
 * Provides testcontainers for integration tests.
 */
public class AppContainerConfig implements SharedContainerConfiguration {

  private static final Network NETWORK = Network.newNetwork();

  public static final String TOPIC = "messages";

  private static final String KAFKA_ALIAS = "kafka";

  @Override
  public void startContainers() {
    KAFKA.start();
    KAFKA_PRODUCER.start();
  }

  @Container
  public static final KafkaContainer KAFKA = new KafkaContainer()
      .withNetwork(NETWORK)
      .withNetworkAliases(KAFKA_ALIAS)
      .withEnv("KAFKA_BROKER_ID", "1")
      .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1");

  @Container
  public static final ApplicationContainer KAFKA_PRODUCER = new ApplicationContainer("kafka-showcase/kafka-producer:0")
      .withNetwork(NETWORK)
      .withNetworkAliases("kafka-producer")
      .dependsOn(KAFKA)
      .withEnv("KAFKA_HOST", KAFKA_ALIAS + ":9092")
      .withEnv("KAFKA_TOPIC", TOPIC)
      .withEnv("KAFKA_CLIENT_ID", "kafka-producer")
      .withAppContextRoot("kafka-producer")
      .waitingFor(new WaitAllStrategy().withStrategy(Wait.forHttp("/health/live"))
          .withStartupTimeout(Duration.ofMinutes(1)));

}
