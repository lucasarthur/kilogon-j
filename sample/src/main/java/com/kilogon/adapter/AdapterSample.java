package com.kilogon.adapter;

import static java.lang.String.format;
import static java.util.concurrent.ThreadLocalRandom.current;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdapterSample {
  private final Adapter<Long, String> adapter;

  @AdapterExecution
  public void execution() {
    adapter.from(() -> current().nextLong())
      .to(log::info)
      .mappedBy(l -> format("Just generated a number: %d", l))
      .adapt();
  }
}
