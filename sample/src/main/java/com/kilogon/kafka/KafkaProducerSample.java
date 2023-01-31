package com.kilogon.kafka;

import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kilogon.model.Person;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaProducerSample {
  private static final String PERSON_TOPIC = "PersonTopic";
  private static final Person PERSON = Person.of("Lucas", 21);

  private final ReactiveKafkaProducer<String, Person> stringKeyProducer;
  private final ReactiveKafkaProducer<Long, Person> longKeyProducer;

  @EventListener(ApplicationReadyEvent.class)
  public void execution() {
    stringKeyProducer.with(new StringSerializer()).produceOne(PERSON_TOPIC, PERSON.getUuid(), PERSON).subscribe();
    longKeyProducer.with(new LongSerializer()).produceOne(PERSON_TOPIC, PERSON.getId(), PERSON).subscribe();
  }
}
