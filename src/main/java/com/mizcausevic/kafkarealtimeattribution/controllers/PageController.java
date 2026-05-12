package com.mizcausevic.kafkarealtimeattribution.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mizcausevic.kafkarealtimeattribution.services.AttributionService;
import com.mizcausevic.kafkarealtimeattribution.services.HtmlRenderer;

@RestController
public class PageController {
    private final AttributionService attributionService;

    public PageController(AttributionService attributionService) {
        this.attributionService = attributionService;
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return HtmlRenderer.renderOverview(attributionService.dashboardSummary(), attributionService.channelSummaries());
    }

    @GetMapping(value = "/journeys", produces = MediaType.TEXT_HTML_VALUE)
    public String journeys() {
        return HtmlRenderer.renderJourneys(attributionService.journeySummaries());
    }

    @GetMapping(value = "/verification", produces = MediaType.TEXT_HTML_VALUE)
    public String verification() {
        return HtmlRenderer.renderVerification(attributionService.samplePayload());
    }

    @GetMapping(value = "/topology", produces = MediaType.TEXT_HTML_VALUE)
    public String docs() {
        return HtmlRenderer.renderDocs(attributionService.topologySummary());
    }
}
