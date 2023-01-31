package com.kilogon.function;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

@FunctionalInterface
public interface OutputSink<T> extends Consumer<T> {
  void outflow(T element);

  default void accept(T element) { outflow(element); }

  default OutputSink<T> andThen(OutputSink<? super T> after) {
    return element -> { outflow(element); requireNonNull(after).outflow(element); };
  }
}
