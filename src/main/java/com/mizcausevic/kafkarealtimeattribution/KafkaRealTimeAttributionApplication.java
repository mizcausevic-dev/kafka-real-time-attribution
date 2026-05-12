package com.mizcausevic.kafkarealtimeattribution;

import java.net.BindException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaRealTimeAttributionApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(KafkaRealTimeAttributionApplication.class);
        try {
            app.run(args);
        } catch (Exception ex) {
            if (isPortIssue(ex)) {
                String port = System.getenv().getOrDefault("PORT", "4649");
                System.err.println("Kafka Real-Time Attribution could not start because port " + port + " is already in use.");
                System.err.println("Set a different port before running again, for example:");
                System.err.println("$env:PORT = \"4653\"");
                System.err.println(".\\mvnw.cmd spring-boot:run");
            }
            throw ex;
        }
    }

    private static boolean isPortIssue(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof BindException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
