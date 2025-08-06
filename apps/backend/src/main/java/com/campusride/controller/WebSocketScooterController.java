package com.campusride.controller;

import com.campusride.model.Scooter;
import com.campusride.service.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class WebSocketScooterController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ScooterService scooterService;

    // Broadcast live scooter locations every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void broadcastScooterLocations() {
        List<Scooter> scooters = scooterService.getAllScooters();
        messagingTemplate.convertAndSend("/topic/scooter-locations", scooters);
    }
}