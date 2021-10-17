package com.example.springproducer.api;

import com.example.springproducer.dto.MessageQueue;
import com.example.springproducer.service.AmqpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmqpApiController {

    @Autowired
    private AmqpService service;


    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/send")
    public void sendToConsumer(@RequestBody MessageQueue message) {
        service.sendToConsumer(message);
    }
}
