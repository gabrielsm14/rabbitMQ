package com.example.spring.consumer.api;

import com.example.spring.consumer.service.RePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmqpApi {

    @Autowired
    private RePublishService rePublishService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/repost")
    public void sendToQueue() {
        rePublishService.repost();
    }

}
