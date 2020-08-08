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

import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Notification;

/**
 * Kafka message generator that sends messages to a kafka topic. The topic is configured in the microprofile-config.properties.
 */
@ApplicationScoped
public class KafkaReactiveMessagingGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaReactiveMessagingGenerator.class);

  @Inject
  @ConfigProperty(name = "mp.messaging.outgoing.messages.topic")
  private String topic;

  @Outgoing("messages")
  public Flowable<ProducerRecord<String, CustomMessage>> generate() {
    return Flowable
        .interval(2, TimeUnit.SECONDS)
        .doOnEach(KafkaReactiveMessagingGenerator::logMessage)
        .map(this::createMessage);
  }

  private static void logMessage(Notification<Long> notification) {
    LOG.info("Send generated message {}", notification.getValue());
  }

  private ProducerRecord<String, CustomMessage> createMessage(Long tick) {
    return new ProducerRecord<>(topic, new CustomMessage(String.format("%s", LocalDateTime.now())));
  }
}
