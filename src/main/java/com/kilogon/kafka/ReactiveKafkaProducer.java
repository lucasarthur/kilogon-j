package com.kilogon.kafka;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static reactor.core.publisher.Mono.just;
import static reactor.core.scheduler.Schedulers.boundedElastic;
import static reactor.kafka.sender.SenderRecord.create;
import static reactor.util.retry.Retry.max;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import com.kilogon.kafka.entity.ProduceableEntity;
import com.kilogon.kafka.util.KafkaUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReactiveKafkaProducer<K, V> implements AutoCloseable, DisposableBean {
	private final KafkaUtils utils;
  private KafkaSender<K, V> producer;

	public ReactiveKafkaProducer<K, V> with(Serializer<K> keySerializer) {
		producer = utils.producer(keySerializer);
		return this;
	}

  public Mono<SenderResult<V>> produceOne(String topic, V value) {
		return produceOne(topic, null, value);
	}

	public Mono<SenderResult<V>> produceOne(String topic, K key, V value) {
		return produceOne(topic, key, value, null);
	}

	public Mono<SenderResult<V>> produceOne(String topic, K key, V value, Headers headers) {
		return produceOne(just(ProduceableEntity.of(topic, key, value, headers)));
	}

	public Mono<SenderResult<V>> produceOne(Mono<ProduceableEntity<K, V>> entity) {
		return produceMany(entity.flux()).single();
	}

	public Flux<SenderResult<V>> produceMany(Flux<ProduceableEntity<K, V>> entities) {
		return entities
			.map($ -> new ProducerRecord<>($.topic(), null, $.key(), $.value(), $.headers()))
			.map($ -> create($, $.value()))
			.transform($ -> produce($.subscribeOn(boundedElastic())));
	}

	private Flux<SenderResult<V>> produce(Publisher<? extends SenderRecord<K, V, V>> records) {
		return requireNonNull(producer).send(records)
			.subscribeOn(boundedElastic())
			.doOnError(e -> log.error(e.getMessage(), e))
			.retryWhen(max(3l).transientErrors(true));
	}

	private void _close() {
		if (!isNull(producer)) {
			producer.close();
			producer = null;
		}
	}

	@Override public void destroy() { _close(); }
	@Override public void close() { _close(); }
}
