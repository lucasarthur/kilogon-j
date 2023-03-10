package com.kilogon.kafka;

import static com.kilogon.kafka.util.KafkaUtils.longDeserializer;
import static com.kilogon.kafka.util.KafkaUtils.stringDeserializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kilogon.adapter.AdapterExecution;
import com.kilogon.model.Person;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerSample {
  @Value("${spring.kafka.string-key-person-topic}") private String stringKeyTopic;
  @Value("${spring.kafka.long-key-person-topic}") private String longKeyTopic;

  private final ReactiveKafkaConsumer<String, Person> stringKeyConsumer;
  private final ReactiveKafkaConsumer<Long, Person> longKeyConsumer;
  private final ObjectMapper mapper;

  @AdapterExecution
  public void execution() {
    stringKeyConsumer.with(stringDeserializer(), stringKeyTopic).doOnEach(this::prettyPrint);
    longKeyConsumer.with(longDeserializer(), longKeyTopic).doOnEach(this::prettyPrint);
  }

  @SneakyThrows
  public <V> void prettyPrint(V value) {
    log.info("{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value));
  }
}
