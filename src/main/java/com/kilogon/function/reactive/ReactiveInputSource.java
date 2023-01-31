package com.kilogon.function.reactive;

import java.util.function.Supplier;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface ReactiveInputSource<T> extends Supplier<Flux<T>> {
  Flux<T> feed();

  default Flux<T> get() { return feed(); }
}
