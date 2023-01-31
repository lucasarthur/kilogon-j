package com.kilogon.adapter;

import static java.util.Objects.requireNonNull;

import com.kilogon.function.InputSource;
import com.kilogon.function.OutputSink;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class From<I, O> {
  private final InputSource<I> source;

  public To<I, O> to(OutputSink<O> sink) {
    return new To<>(source, requireNonNull(sink));
  }
}
