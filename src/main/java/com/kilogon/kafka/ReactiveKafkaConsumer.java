package com.kilogon.kafka;

import static java.util.Objects.requireNonNull;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.scheduler.Schedulers.boundedElastic;
import static reactor.util.retry.Retry.max;

import java.util.function.Consumer;

import org.apache.kafka.common.serialization.Deserializer;
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
public class ReactiveKafkaConsumer<K, V> {
  private final KafkaUtils utils;
  private KafkaReceiver<K, V> consumer;

  public ReactiveKafkaConsumer<K, V> with(Deserializer<K> keyDeserializer, String... topics) {
		consumer = utils.consumer(keyDeserializer, topics);
		return this;
	}

  public void doOnEach(Consumer<ConsumableEntity<K, V>> action) {
    consume().doOnNext(action).subscribe();
  }

  private Flux<ConsumableEntity<K, V>> consume() {
    return requireNonNull(consumer)
      .receive()
      .subscribeOn(boundedElastic())
      .map(utils::ack)
      .map(ConsumableEntity::of)
      .doOnError($ -> log.error($.getMessage(), $))
      .retryWhen(max(3l).transientErrors(true))
      .onErrorResume($ -> empty())
      .repeat();
  }
}
