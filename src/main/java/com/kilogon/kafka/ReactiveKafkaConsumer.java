package com.kilogon.kafka;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.isNull;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.scheduler.Schedulers.boundedElastic;
import static reactor.util.retry.Retry.max;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.kilogon.kafka.entity.ConsumableEntity;
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
		if (isNull(consumer)) consumer = utils.consumer(keyDeserializer, topics);
		return this;
	}

  public void doOnEach(Consumer<ConsumableEntity<K, V>> action) {
    consume().doOnNext(action).subscribe();
  }

  public Flux<ConsumableEntity<K, V>> consume() {
    return requireNonNull(consumer)
      .receive()
      .takeUntil($ -> canceled.get())
      .subscribeOn(boundedElastic())
      .map(utils::ack)
      .map(ConsumableEntity::of)
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

  public Deserializer<String> stringKeys() { return new StringDeserializer(); }
	public Deserializer<Long> longKeys() { return new LongDeserializer(); }
}