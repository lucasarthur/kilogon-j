package com.kilogon.kafka.config;

import static reactor.kafka.receiver.KafkaReceiver.create;
import static reactor.kafka.receiver.internals.ConsumerFactory.INSTANCE;

import java.util.Collection;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.kilogon.kafka.options.ConsumerOptions;

import lombok.RequiredArgsConstructor;
import reactor.kafka.receiver.KafkaReceiver;

@Configuration
@RequiredArgsConstructor
public class ConsumerConfig<K, V> {
  private final ConsumerOptions<K, V> options;

  @Bean("kafka-consumer")
  @Scope("prototype")
  KafkaReceiver<K, V> kafkaReceiver(Collection<String> topics, Deserializer<K> keyDeserializer) {
    return create(INSTANCE, options.options(topics, keyDeserializer));
  }
}
