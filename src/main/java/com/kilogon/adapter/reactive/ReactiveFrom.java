package com.kilogon.adapter.reactive;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.kilogon.function.reactive.ReactiveInputSource;
import com.kilogon.function.reactive.ReactiveOutputSink;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReactiveFrom<I, O> {
  private final ReactiveInputSource<I> source;

  public ReactiveTo<I, O> to(ReactiveOutputSink<O> sink) {
    return new ReactiveTo<>(source, requireNonNull(sink));
  }

  public ReactiveTo<I, O> onEachTo(Consumer<O> action) {
    return to(stream -> stream.doOnNext(action).then());
  }
}
