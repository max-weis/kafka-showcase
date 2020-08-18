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

import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * TracingInterceptor traces methods that are annotated by {@link Tracing}.
 */
@Tracing
@Interceptor
public class TracingInterceptor implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(TracingInterceptor.class);

  private Tracer tracer = Configuration.fromEnv().getTracer();

  @AroundInvoke
  public Object trace(InvocationContext ctx) throws Exception {
    Message message = (Message)ctx.getParameters()[0];
    ConsumerRecord<String, CustomMessage> record = (ConsumerRecord<String, CustomMessage>)message.unwrap(ConsumerRecord.class);

    TracingKafkaUtils.buildAndFinishChildSpan(record, tracer);

    return ctx.proceed();
  }
}
