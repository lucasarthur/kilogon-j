package com.kilogon.kafka.options;

import java.util.Collection;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.kafka.receiver.ReceiverOptions;

@Component
@RequiredArgsConstructor
@SuppressWarnings("resource")
public class ConsumerOptions<K, V> {
  private final KafkaProperties properties;
  private final ObjectMapper mapper;

  public ReceiverOptions<K, V> options(Collection<String> topics, Deserializer<K> keyDeserializer) {
    return ReceiverOptions.<K, V>create(consumerProperties())
      .withKeyDeserializer(keyDeserializer)
      .withValueDeserializer(jsonDeserializer())
      .commitBatchSize(1)
      .subscription(topics);
  }

  private JsonDeserializer<V> jsonDeserializer() {
    JsonDeserializer<V> deserializer = new JsonDeserializer<>(new TypeReference<V>(){}, mapper).trustedPackages("*");
    deserializer.configure(consumerProperties(), false);
    return deserializer;
  }

  private Map<String, Object> consumerProperties() {
    return properties.buildConsumerProperties();
  }
}
