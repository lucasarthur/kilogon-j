package com.kilogon.adapter;

import static com.kilogon.function.reactive.ReactiveInputSource.consumer;
import static com.kilogon.kafka.util.KafkaUtils.stringDeserializer;
import static reactor.core.scheduler.Schedulers.boundedElastic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kilogon.adapter.reactive.ReactiveAdapter;
import com.kilogon.kafka.ReactiveKafkaConsumer;
import com.kilogon.kafka.entity.StreamableEntity;
import com.kilogon.model.Person;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsumerAdapterSample {
  private final ReactiveAdapter<StreamableEntity<String, Person>, String> adapter;
  private final ReactiveKafkaConsumer<String, Person> consumer;

  @Value("${spring.kafka.string-key-person-topic}") private String topic;

  @AdapterExecution
  public void execution() {
    adapter.from(consumer(consumer.with(stringDeserializer(), topic)))
      .onEachTo(log::info)
      .mappedBy(entity -> entity.valueAs(Person.class).getStringId())
      .adaptAndSubscribeOn(boundedElastic());
  }
}
