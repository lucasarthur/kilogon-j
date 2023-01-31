package com.kilogon.function;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

@FunctionalInterface
public interface OutputSink<T> extends Consumer<T> {
  void outflow(T t);

  default void accept(T t) {
    outflow(t);
  }

  default OutputSink<T> andThen(OutputSink<? super T> after) {
    return t -> { outflow(t); requireNonNull(after).outflow(t); };
  }
}
