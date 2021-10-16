package com.example.springproducer.service;

import com.example.springproducer.dto.Message;

public interface AmqpService {
    void sendToConsumer(Message message);
}
