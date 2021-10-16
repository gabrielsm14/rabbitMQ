package com.example.springproducer.amqp;

public interface AmqpProducer<T> {
    void producer(T t);
}
