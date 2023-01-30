package com.kilogon.kafka.config;

import static reactor.kafka.sender.KafkaSender.create;
import static reactor.kafka.sender.internals.ProducerFactory.INSTANCE;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.kilogon.kafka.options.ProducerOptions;

import lombok.RequiredArgsConstructor;
import reactor.kafka.sender.KafkaSender;

@Configuration
@RequiredArgsConstructor
public class ProducerConfig<K, V> {
  private final ProducerOptions<K, V> options;

  @Bean("kafka-producer")
  @Scope("prototype")
  KafkaSender<K, V> producer(Serializer<K> keySerializer) {
    return create(INSTANCE, options.options(keySerializer));
  }
}
