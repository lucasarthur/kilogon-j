package com.kilogon.kafka;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static reactor.core.scheduler.Schedulers.boundedElastic;

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
  private final AtomicBoolean consuming = new AtomicBoolean(true);
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
      .subscribeOn(boundedElastic())
      .map(utils::ack)
      .map(StreamableEntity::of)
      .doOnError($ -> log.error($.getMessage(), $))
      .onErrorComplete()
      .repeat(consuming::get);
  }

  public void stop() {
    if (!isNull(consumer)) {
      consuming.compareAndSet(true, false);
      consumer = null;
    }
  }

	@Override public void destroy() { stop(); }
	@Override public void close() { stop(); }
}
