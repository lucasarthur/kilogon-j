package com.kilogon.kafka;

import static com.kilogon.kafka.util.KafkaUtils.longSerializer;
import static com.kilogon.kafka.util.KafkaUtils.stringSerializer;
import static java.time.Duration.ofMillis;
import static java.util.concurrent.ThreadLocalRandom.current;
import static reactor.core.publisher.Flux.interval;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kilogon.kafka.entity.StreamableEntity;
import com.kilogon.model.Person;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaProducerSample {
  @Value("${spring.kafka.string-key-person-topic}") private String stringKeyTopic;
  @Value("${spring.kafka.long-key-person-topic}") private String longKeyTopic;

  private final ReactiveKafkaProducer<String, Person> stringKeyProducer;
  private final ReactiveKafkaProducer<Long, Person> longKeyProducer;
  private final Person person = newPerson();

  @EventListener(ApplicationReadyEvent.class)
  public void execution() {
    // with string keys
    stringKeyProducer.with(stringSerializer())
      .produceOne(stringKeyTopic, person.getStringId(), person).subscribe();

    // with long keys
    longKeyProducer.with(longSerializer())
      .produceOne(longKeyTopic, person.getLongId(), person).subscribe();

    // batch producing
    stringKeyProducer.with(stringSerializer())
      .produceMany(interval(ofMillis(50L))
        .map(l -> newPerson())
        .map(this::toProduceable)
        .take(10L))
      .subscribe();
  }

  private StreamableEntity<String, Person> toProduceable(Person person) {
    return StreamableEntity.of(stringKeyTopic, person.getStringId(), person, null);
  }

  private Person newPerson() {
    return Person.of("Lucas", randomAge());
  }

  private Integer randomAge() {
    return current().nextInt(18, 65);
  }
}
