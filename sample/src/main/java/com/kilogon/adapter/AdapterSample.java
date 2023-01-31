package com.kilogon.adapter;

import static java.util.concurrent.ThreadLocalRandom.current;
import static java.lang.String.format;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdapterSample {
  private final Adapter<Long, String> adapter;

  // @EventListener(ApplicationReadyEvent.class)
  public void execution() {
    adapter.from(() -> current().nextLong())
      .to(log::info)
      .mappedBy(l -> format("Just generated a number: %d", l))
      .adapt();
  }
}
