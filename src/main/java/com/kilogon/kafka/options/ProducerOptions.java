package com.kilogon.kafka.options;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.kafka.sender.SenderOptions;

@Component
@RequiredArgsConstructor
public class ProducerOptions<K, V> {
  private final KafkaProperties properties;
  private final ObjectMapper mapper;

  public SenderOptions<K, V> options(Serializer<K> keySerializer) {
    return SenderOptions.<K, V>create(producerProperties())
      .withKeySerializer(keySerializer)
      .withValueSerializer(jsonSerializer());
  }

  private JsonSerializer<V> jsonSerializer() {
    JsonSerializer<V> serializer = new JsonSerializer<>(mapper);
    serializer.configure(producerProperties(), false);
    return serializer;
  }

  private Map<String, Object> producerProperties() {
    return properties.buildProducerProperties();
  }
}
