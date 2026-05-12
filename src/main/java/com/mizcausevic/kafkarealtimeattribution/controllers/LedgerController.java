package com.mizcausevic.kafkarealtimeattribution.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.AttributionJourney;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.AttributionPayload;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.ChannelSummary;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.DashboardSummary;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.JourneySummary;
import com.mizcausevic.kafkarealtimeattribution.services.AttributionService;

@RestController
@RequestMapping("/api")
public class LedgerController {
    private final AttributionService attributionService;

    public LedgerController(AttributionService attributionService) {
        this.attributionService = attributionService;
    }

    @GetMapping("/dashboard/summary")
    public DashboardSummary summary() {
        return attributionService.dashboardSummary();
    }

    @GetMapping("/events")
    public List<AttributionJourney> events() {
        return attributionService.journeys();
    }

    @GetMapping("/journeys")
    public List<JourneySummary> journeys() {
        return attributionService.journeySummaries();
    }

    @GetMapping("/channels")
    public List<ChannelSummary> channels() {
        return attributionService.channelSummaries();
    }

    @GetMapping("/topology")
    public Map<String, String> topology() {
        return attributionService.topologySummary();
    }

    @GetMapping("/sample")
    public AttributionPayload sample() {
        return attributionService.samplePayload();
    }
}
