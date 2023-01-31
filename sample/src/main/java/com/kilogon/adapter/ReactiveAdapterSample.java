package com.kilogon.adapter;

import static reactor.core.publisher.Flux.interval;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.lang.String.format;
import static reactor.core.scheduler.Schedulers.boundedElastic;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kilogon.adapter.reactive.ReactiveAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReactiveAdapterSample {
  private final ReactiveAdapter<Long, String> adapter;

  // @EventListener(ApplicationReadyEvent.class)
  public void execution() {
    adapter.from(() -> interval(ofMillis(250L)).map(l -> current().nextLong()))
      .to(stream -> stream.doOnNext(log::info).then())
      .mappedBy(l -> format("Just generated a number: %d", l))
      .adaptAndSubscribeOn(boundedElastic());
  }
}
