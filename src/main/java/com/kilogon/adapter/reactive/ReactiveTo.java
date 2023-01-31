package com.kilogon.adapter.reactive;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import com.kilogon.function.reactive.ReactiveInputSource;
import com.kilogon.function.reactive.ReactiveOutputSink;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReactiveTo<I, O> {
  private final ReactiveInputSource<I> source;
  private final ReactiveOutputSink<O> sink;

  public ReactiveMappedBy<I, O> mappedBy(Function<I, O> mapper) {
    return new ReactiveMappedBy<>(source, sink, requireNonNull(mapper));
  }
}
