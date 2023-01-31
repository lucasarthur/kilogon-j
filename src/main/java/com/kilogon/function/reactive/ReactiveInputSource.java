package com.kilogon.function.reactive;

import java.util.function.Supplier;

import com.kilogon.kafka.ReactiveKafkaConsumer;
import com.kilogon.kafka.entity.StreamableEntity;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface ReactiveInputSource<T> extends Supplier<Flux<T>> {
  Flux<T> feed();

  default Flux<T> get() { return feed(); }

  static <K, V> ReactiveInputSource<StreamableEntity<K, V>> fromConsumer(ReactiveKafkaConsumer<K, V> consumer) {
    return consumer::consume;
  }
}
