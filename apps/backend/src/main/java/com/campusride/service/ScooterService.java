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
    private final Map<Integer, Movement> movingScooters = new HashMap<>();

    // Helper class to track back-and-forth movement
    private static class Movement {
        double startLat;
        double startLng;
        double endLat;
        double endLng;
        boolean forward = true;
        double progress = 0.0;
    }

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
            scooter.setName("Scooter " + (i + 1));
            scooter.setLat(Double.parseDouble(anuLocations[i][1]));
            scooter.setLng(Double.parseDouble(anuLocations[i][2]));

            if (i < 3) {
                scooter.setStatus("Running");
                scooter.setSpeed(ThreadLocalRandom.current().nextInt(10, 26)); // 10–25 kph
                if (i == 0) scooter.setBattery(ThreadLocalRandom.current().nextInt(70, 81));
                if (i == 1) scooter.setBattery(ThreadLocalRandom.current().nextInt(50, 61));
                if (i == 2) scooter.setBattery(ThreadLocalRandom.current().nextInt(30, 41));
            } else if (i == 3) {
                scooter.setStatus("Locked");
                scooter.setSpeed(0);
                scooter.setBattery(56);
            } else {
                scooter.setStatus("Maintenance");
                scooter.setSpeed(0);
                scooter.setBattery(3);
            }

            scooters.add(scooter);
        }

        // Define paths for the first 3 running scooters
        movingScooters.put(1, createMovement(-35.27696367673257, 149.1198959596581, -35.2755447551961, 149.12112441125177));
        movingScooters.put(2, createMovement(-35.27898691078083, 149.12384417107916, -35.27678850261695, 149.12019636722022));
        movingScooters.put(3, createMovement(-35.27363530300725, 149.1179594045741, -35.2769592973909, 149.11506261907763));
    }

    private Movement createMovement(double lat1, double lng1, double lat2, double lng2) {
        Movement m = new Movement();
        m.startLat = lat1;
        m.startLng = lng1;
        m.endLat = lat2;
        m.endLng = lng2;
        return m;
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

    @Scheduled(fixedRate = 2000)
    public void broadcastUpdates() {
        simulateUpdates();
        updateStats();
        messagingTemplate.convertAndSend("/topic/scooter-locations", scooters);
        messagingTemplate.convertAndSend("/topic/scooter-stats", stats);
    }

    private void simulateUpdates() {
        for (Scooter scooter : scooters) {

            // Update battery within original range
            if (scooter.getId() == 1) scooter.setBattery(ThreadLocalRandom.current().nextInt(70, 81));
            if (scooter.getId() == 2) scooter.setBattery(ThreadLocalRandom.current().nextInt(50, 61));
            if (scooter.getId() == 3) scooter.setBattery(ThreadLocalRandom.current().nextInt(30, 41));

            // Update speed and move only if running
            if ("Running".equals(scooter.getStatus())) {
                scooter.setSpeed(ThreadLocalRandom.current().nextInt(10, 26)); // 10–25 kph

                Movement m = movingScooters.get(scooter.getId());
                if (m != null) {
                    // Move progress
                    m.progress += m.forward ? 0.01 : -0.01;

                    if (m.progress >= 1.0) {
                        m.progress = 1.0;
                        m.forward = false;
                    } else if (m.progress <= 0.0) {
                        m.progress = 0.0;
                        m.forward = true;
                    }

                    double newLat = m.startLat + (m.endLat - m.startLat) * m.progress;
                    double newLng = m.startLng + (m.endLng - m.startLng) * m.progress;
                    scooter.setLat(newLat);
                    scooter.setLng(newLng);
                }
            } else {
                scooter.setSpeed(0); // Locked or Maintenance scooters don't move
            }
        }
    }

    public List<Scooter> getAllScooters() {
        return scooters;
    }

    public Map<String, Object> getStats() {
        return stats;
    }
}
