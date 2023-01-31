package com.kilogon.adapter.reactive;

import java.util.function.Function;

import com.kilogon.function.reactive.ReactiveInputSource;
import com.kilogon.function.reactive.ReactiveOutputSink;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RequiredArgsConstructor
public class ReactiveMappedBy<I, O> {
  private final ReactiveInputSource<I> source;
  private final ReactiveOutputSink<O> sink;
  private final Function<I, O> mapper;

  public Mono<Void> adapt() {
    return sink.outflow(source.feed().map(mapper));
  }

  public void adaptAndSubscribe() {
    adapt().subscribe();
  }

  public void adaptAndSubscribeOn(Scheduler scheduler) {
    adapt().subscribeOn(scheduler).subscribe();
  }
}
