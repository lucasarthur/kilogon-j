package com.kilogon.adapter;

import static java.lang.String.format;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.ThreadLocalRandom.current;
import static reactor.core.publisher.Flux.interval;
import static reactor.core.scheduler.Schedulers.boundedElastic;

import org.springframework.stereotype.Component;

import com.kilogon.adapter.reactive.ReactiveAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReactiveAdapterSample {
  private final ReactiveAdapter<Long, String> adapter;

  @AdapterExecution
  public void execution() {
    adapter.from(() -> interval(ofMillis(250L)).map(l -> current().nextLong()))
      .onEachTo(log::info)
      .mappedBy(l -> format("Just generated a number: %d", l))
      .adaptAndSubscribeOn(boundedElastic());
  }
}
