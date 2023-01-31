package com.kilogon.kafka;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.scheduler.Schedulers.boundedElastic;
import static reactor.util.retry.Retry.max;
import static java.util.function.Function.identity;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.kilogon.kafka.entity.StreamableEntity;
import com.kilogon.kafka.util.KafkaUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReactiveKafkaConsumer<K, V> implements AutoCloseable, DisposableBean {
  private final AtomicBoolean canceled = new AtomicBoolean(false);
  private final KafkaUtils utils;
  private KafkaReceiver<K, V> consumer;

  public ReactiveKafkaConsumer<K, V> with(Deserializer<K> keyDeserializer, String... topics) {
		consumer = utils.consumer(keyDeserializer, topics);
		return this;
	}

  public void doOnEach(Consumer<StreamableEntity<K, V>> action) {
    mappingAndThen(identity(), action);
  }

  public <R> void mappingAndThen(Function<StreamableEntity<K, V>, R> mapper, Consumer<R> action) {
    consume().map(mapper).doOnNext(action).subscribe();
  }

  public Flux<StreamableEntity<K, V>> consume() {
    return requireNonNull(consumer)
      .receive()
      .takeUntil($ -> canceled.get())
      .subscribeOn(boundedElastic())
      .map(utils::ack)
      .map(StreamableEntity::of)
      .doOnError($ -> log.error($.getMessage(), $))
      .retryWhen(max(3l).transientErrors(true))
      .onErrorResume($ -> empty())
      .repeat();
  }

  public void stop() {
    if (!isNull(consumer)) {
      canceled.compareAndSet(false, true);
      consumer = null;
    }
  }

	@Override public void destroy() { stop(); }
	@Override public void close() { stop(); }
}
