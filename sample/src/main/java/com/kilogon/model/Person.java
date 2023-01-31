package com.kilogon.model;

import static java.util.UUID.randomUUID;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Person {
  private String name;
  private Integer age;
  @Builder.Default private String stringId = randomUUID().toString();
  @Builder.Default private Long longId = current().nextLong();
  @Builder.Default private Boolean alive = true;
  @Builder.Default private LocalDateTime createdAt = now();

  public static Person of(String name, Integer age) {
    return builder().name(name).age(age).build();
  }
}
