package com.kilogon.adapter;

import static com.kilogon.function.reactive.ReactiveOutputSink.producer;
import static com.kilogon.kafka.util.KafkaUtils.stringSerializer;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.ThreadLocalRandom.current;
import static reactor.core.publisher.Flux.interval;
import static reactor.core.scheduler.Schedulers.boundedElastic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kilogon.adapter.reactive.ReactiveAdapter;
import com.kilogon.kafka.ReactiveKafkaProducer;
import com.kilogon.kafka.entity.StreamableEntity;
import com.kilogon.model.Person;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProducerAdapterSample {
  private final ReactiveAdapter<Long, StreamableEntity<String, Person>> adapter;
  private final ReactiveKafkaProducer<String, Person> producer;

  @Value("${spring.kafka.string-key-person-topic}") private String topic;

  // @EventListener(ApplicationReadyEvent.class)
  public void execution() {
    adapter.from(() -> interval(ofSeconds(2L)))
      .to(producer(producer.with(stringSerializer())))
      .mappedBy(l -> toProduceable(newPerson()))
      .adaptAndSubscribeOn(boundedElastic());
  }

  private StreamableEntity<String, Person> toProduceable(Person person) {
    return StreamableEntity.of(topic, person.getStringId(), person, null);
  }

  private Person newPerson() {
    return Person.of("Lucas", randomAge());
  }

  private Integer randomAge() {
    return current().nextInt(18, 65);
  }
}
