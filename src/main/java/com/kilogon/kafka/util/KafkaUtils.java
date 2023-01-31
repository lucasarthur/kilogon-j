package com.kilogon.kafka.util;

import static java.util.Arrays.asList;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;

@Component
@RequiredArgsConstructor
@SuppressWarnings({ "unchecked", "rawtypes" })
public class KafkaUtils {
  private final ConfigurableListableBeanFactory cbf;

  public <K, V> KafkaReceiver<K, V> consumer(Deserializer<K> keyDeserializer, String... topics) {
    return this.<K, V>consumerType().cast(cbf.getBean("kafka-consumer", asList(topics), keyDeserializer));
  }

  public <K, V> KafkaSender<K, V> producer(Serializer<K> keySerializer) {
    return this.<K, V>producerType().cast(cbf.getBean("kafka-producer", keySerializer));
  }

  public <K, V> ReceiverRecord<K, V> ack(ReceiverRecord<K, V> record) {
    record.receiverOffset().acknowledge();
    return record;
  }

  public Class<Map<String, Object>> mapType() { return (Class<Map<String, Object>>) (Class) Map.class; }
  private <K, V> Class<KafkaReceiver<K, V>> consumerType() { return (Class<KafkaReceiver<K, V>>) (Class) KafkaReceiver.class; }
  private <K, V> Class<KafkaSender<K, V>> producerType() { return (Class<KafkaSender<K, V>>) (Class) KafkaSender.class; }

  public static Serializer<String> stringSerializer() { return new StringSerializer(); }
	public static Serializer<Long> longSerializer() { return new LongSerializer(); }
  public static Deserializer<String> stringDeserializer() { return new StringDeserializer(); }
	public static Deserializer<Long> longDeserializer() { return new LongDeserializer(); }
}
