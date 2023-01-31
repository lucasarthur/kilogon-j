package com.kilogon.adapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventListener(ApplicationReadyEvent.class)
public @interface AdapterExecution {
}
