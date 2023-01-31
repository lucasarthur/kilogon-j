package com.kilogon.adapter.reactive;

import static java.util.Objects.requireNonNull;

import com.kilogon.function.reactive.ReactiveInputSource;
import com.kilogon.function.reactive.ReactiveOutputSink;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReactiveFrom<I, O> {
  private final ReactiveInputSource<I> source;

  public ReactiveTo<I, O> to(ReactiveOutputSink<O> sink) {
    return new ReactiveTo<>(source, requireNonNull(sink));
  }
}
