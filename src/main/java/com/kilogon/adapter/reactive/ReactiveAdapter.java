package com.kilogon.adapter.reactive;

import static java.util.Objects.requireNonNull;

import org.springframework.stereotype.Component;

import com.kilogon.function.reactive.ReactiveInputSource;

@Component
public class ReactiveAdapter<I, O> {
  public ReactiveFrom<I, O> from(ReactiveInputSource<I> source) {
    return new ReactiveFrom<>(requireNonNull(source));
  }
}
