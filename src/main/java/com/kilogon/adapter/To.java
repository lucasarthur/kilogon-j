package com.kilogon.adapter;

import java.util.function.Function;

import com.kilogon.function.InputSource;
import com.kilogon.function.OutputSink;

import static java.util.Objects.requireNonNull;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class To<I, O> {
  private final InputSource<I> source;
  private final OutputSink<O> sink;

  public MappedBy<I, O> mappedBy(Function<I, O> mapper) {
    return new MappedBy<>(source, sink, requireNonNull(mapper));
  }
}
