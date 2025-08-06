package com.campusride.service;

import com.campusride.model.Scooter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Service
public class ScooterService {

    private static final Logger logger = Logger.getLogger(ScooterService.class.getName());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final List<Scooter> scooters = new ArrayList<>();
    private final Map<String, Object> stats = new HashMap<>();

    public ScooterService() {
        initializeScooters();
        updateStats();
    }

    private void initializeScooters() {
        String[][] anuLocations = {
                {"Marie Reay Teaching Centre", "-35.27757954101514", "149.1208912314757"},
                {"Near Union Court", "-35.2780", "149.1205"},
                {"Student Plaza", "-35.2770", "149.1215"},
                {"Library Entrance", "-35.2785", "149.1200"},
                {"Engineering Precinct", "-35.2772", "149.1220"}
        };

        for (int i = 0; i < anuLocations.length; i++) {
            Scooter scooter = new Scooter();
            scooter.setId(i + 1);
            scooter.setName(anuLocations[i][0]);
            double lat = Double.parseDouble(anuLocations[i][1]);
            double lng = Double.parseDouble(anuLocations[i][2]);
            scooter.setLat(lat);
            scooter.setLng(lng);
            scooter.setBattery(ThreadLocalRandom.current().nextInt(40, 100));
            scooter.setStatus(getRandomStatus());
            scooters.add(scooter);
        }
    }

    private String getRandomStatus() {
        String[] statuses = {"Running", "Locked", "Maintenance"};
        return statuses[ThreadLocalRandom.current().nextInt(statuses.length)];
    }

    private void updateStats() {
        long running = scooters.stream().filter(s -> "Running".equals(s.getStatus())).count();
        long locked = scooters.stream().filter(s -> "Locked".equals(s.getStatus())).count();
        long maintenance = scooters.stream().filter(s -> "Maintenance".equals(s.getStatus())).count();
        int total = scooters.size();

        stats.put("running", running);
        stats.put("locked", locked);
        stats.put("maintenance", maintenance);
        stats.put("total", total);
        stats.put("timestamp", new Date());
    }

    @Scheduled(fixedRate = 5000)
    public void broadcastUpdates() {
        simulateUpdates();
        updateStats();
        messagingTemplate.convertAndSend("/topic/scooter-locations", scooters);
        messagingTemplate.convertAndSend("/topic/scooter-stats", stats);
        System.out.println("Broadcasting scooters: " + scooters);
    }

    private void simulateUpdates() {
        for (Scooter scooter : scooters) {
            // Update battery
            int battery = scooter.getBattery();
            int delta = ThreadLocalRandom.current().nextInt(-3, 2);
            scooter.setBattery(Math.max(0, Math.min(100, battery + delta)));

            // Occasionally change status
            if (ThreadLocalRandom.current().nextDouble() < 0.2) {
                scooter.setStatus(getRandomStatus());
            }

            // Move slightly
            double latOffset = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.0005;
            double lngOffset = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.0005;
            scooter.setLat(scooter.getLat() + latOffset);
            scooter.setLng(scooter.getLng() + lngOffset);
        }
    }

    public List<Scooter> getAllScooters() {
        return scooters;
    }

    public Map<String, Object> getStats() {
        return stats;
    }
}