package com.kilogon.function;

import java.util.function.Supplier;

@FunctionalInterface
public interface InputSource<T> extends Supplier<T> {
  T feed();

  default T get() {
    return feed();
  }
}
