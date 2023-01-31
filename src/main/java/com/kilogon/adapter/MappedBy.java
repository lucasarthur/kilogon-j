package com.kilogon.adapter;

import java.util.function.Function;

import com.kilogon.function.InputSource;
import com.kilogon.function.OutputSink;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MappedBy<I, O> {
  private final InputSource<I> source;
  private final OutputSink<O> sink;
  private final Function<I, O> mapper;

  public void adapt() {
    sink.outflow(mapper.apply(source.feed()));
  }
}
