package com.kilogon.adapter;

import static java.util.Objects.requireNonNull;

import org.springframework.stereotype.Component;

import com.kilogon.function.InputSource;

@Component
public class Adapter<I, O> {
  public From<I, O> from(InputSource<I> source) {
    return new From<>(requireNonNull(source));
  }
}
