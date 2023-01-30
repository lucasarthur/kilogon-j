package com.kilogon.kafka.entity;

import org.apache.kafka.common.header.Headers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProduceableEntity<K, V> {
  private String topic;
  private K key;
  private V value;
  private Headers headers;

  public static <K, V> ProduceableEntity<K, V> of(String topic, K key, V value, Headers headers) {
    return ProduceableEntity.<K, V>builder().topic(topic).key(key).value(value).headers(headers).build();
  }

  public String topic() { return topic; }
  public K key() { return key; }
  public V value() { return value; }
  public Headers headers() { return headers; }
}
