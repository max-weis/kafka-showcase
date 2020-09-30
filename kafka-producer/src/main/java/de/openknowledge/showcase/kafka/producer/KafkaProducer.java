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
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicLong;

import io.opentracing.Span;
import io.opentracing.Tracer;

/**
 * Kafka producer that sends messages to a kafka topic. The topic is configured in the microprofile-config.properties and server.xml.
 */
@ApplicationScoped
public class KafkaProducer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

  @Inject
  @ConfigProperty(name = "mp.messaging.outgoing.messages.topic")
  private String topic;

  @Inject
  private Tracer tracer;

  private Subscriber subscriber;

  private AtomicLong requested = new AtomicLong();

  public void send(final CustomMessage message) {
    LOG.info("Send message: {}", message);

    ProducerRecord<String, CustomMessage> record = new ProducerRecord<>(topic, message);

    Span span = this.tracer.buildSpan("send").asChildOf(this.tracer.activeSpan()).start();
    record.headers().add("uber-trace-id", span.context().toString().getBytes());

    if (requested.get() > 0) {
      subscriber.onNext(Message.of(record));
      span.finish();
    }
  }

  @Outgoing("messages")
  public Publisher<Message> process() {
    return subscriber -> {
      KafkaProducer.this.subscriber = subscriber;
      subscriber.onSubscribe(new Subscription() {
        @Override
        public void request(final long l) {
            KafkaProducer.this.requested.addAndGet(l);
        }

        @Override
        public void cancel() {
        }
      });
    };
  }
}
