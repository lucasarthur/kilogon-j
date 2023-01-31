package com.kilogon.function.reactive;

import static java.util.Objects.requireNonNull;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveOutputSink<T> {
  Mono<Void> outflow(Flux<T> stream);

  default ReactiveOutputSink<T> andThen(ReactiveOutputSink<T> after) {
    return stream -> outflow(stream).then(requireNonNull(after).outflow(stream));
  }
}
