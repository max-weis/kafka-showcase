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

import java.util.Objects;

/**
 * A DTO that represents a custom message.
 */
public class CustomMessage {

  private String text;

  private String sender;

  public CustomMessage() {
  }

  public CustomMessage(final String text) {
    this(text, "Reactive Messaging Producer");
  }

  private CustomMessage(final String text, final String sender) {
    this.text = text;
    this.sender = sender;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(final String sender) {
    this.sender = sender;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomMessage that = (CustomMessage) o;
    return Objects.equals(text, that.text) &&
           Objects.equals(sender, that.sender);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, sender);
  }

  @Override
  public String toString() {
    return "CustomMessage{" + "text='" + text + '\'' + ", sender='" + sender + '\'' + '}';
  }
}
