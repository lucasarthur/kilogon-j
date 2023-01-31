package com.kilogon.function.reactive;

import static java.util.Objects.requireNonNull;

import com.kilogon.kafka.ReactiveKafkaProducer;
import com.kilogon.kafka.entity.StreamableEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveOutputSink<T> {
  Mono<Void> outflow(Flux<T> stream);

  default ReactiveOutputSink<T> andThen(ReactiveOutputSink<T> after) {
    return stream -> outflow(stream).then(requireNonNull(after).outflow(stream));
  }

  static <K, V> ReactiveOutputSink<StreamableEntity<K, V>> toProducer(ReactiveKafkaProducer<K, V> producer) {
    return stream -> producer.produceMany(stream).then();
  }
}
