package com.kilogon.kafka.entity;

import org.apache.kafka.common.header.Headers;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import reactor.kafka.receiver.ReceiverRecord;

@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class StreamableEntity<K, V> {
  private String topic;
  private K key;
  private V value;
  private Headers headers;

  public static <K, V> StreamableEntity<K, V> of(String topic, K key, V value, Headers headers) {
    return StreamableEntity.<K, V>builder().topic(topic).key(key).value(value).headers(headers).build();
  }

  public static <K, V> StreamableEntity<K, V> of(ReceiverRecord<K, V> record) {
    return StreamableEntity.<K, V>builder()
      .topic(record.topic())
      .key(record.key())
      .value(record.value())
      .headers(record.headers())
      .build();
  }

  public String topic() { return topic; }
  public K key() { return key; }
  public V value() { return value; }
  public <R> R valueAs(Class<R> type) { return type.isInstance(value) ? type.cast(value) : mapper().convertValue(value, type); }
  public Headers headers() { return headers; }

  private ObjectMapper mapper() { return new ObjectMapper(); }
}
