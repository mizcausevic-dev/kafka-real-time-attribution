package com.mizcausevic.kafkarealtimeattribution.data;

import java.util.List;

import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.AttributionJourney;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.ConversionEvent;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.SessionEvent;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.TouchEvent;

public final class SampleAttributionData {
    private SampleAttributionData() {
    }

    public static List<AttributionJourney> journeys() {
        return List.of(
                new AttributionJourney(
                        "journey-4102",
                        new SessionEvent(
                                "sess-4102",
                                "Northstar Cloud",
                                "Enterprise",
                                "NA",
                                List.of(
                                        new TouchEvent("touch-1", "2026-05-01T10:12:00Z", "Paid Search", "brand-defense-q2", "ad_click", "discovery"),
                                        new TouchEvent("touch-2", "2026-05-03T16:40:00Z", "Webinar", "governance-briefing", "content", "consideration"),
                                        new TouchEvent("touch-3", "2026-05-07T09:05:00Z", "Direct", "pricing-page", "site_visit", "decision"))),
                        new ConversionEvent("conv-4102", "Northstar Cloud", "trial_to_paid", 62000, "USD", "2026-05-08T12:00:00Z")),
                new AttributionJourney(
                        "journey-4108",
                        new SessionEvent(
                                "sess-4108",
                                "Harborline Finance",
                                "Mid-Market",
                                "EMEA",
                                List.of(
                                        new TouchEvent("touch-4", "2026-05-02T13:15:00Z", "LinkedIn", "cfo-risk-brief", "ad_click", "discovery"),
                                        new TouchEvent("touch-5", "2026-05-05T11:10:00Z", "Email", "product-tour-followup", "lifecycle", "consideration"),
                                        new TouchEvent("touch-6", "2026-05-09T08:35:00Z", "Brand Search", "comparison-intent", "search", "decision"))),
                        new ConversionEvent("conv-4108", "Harborline Finance", "demo_booked", 28000, "USD", "2026-05-10T09:15:00Z")),
                new AttributionJourney(
                        "journey-4116",
                        new SessionEvent(
                                "sess-4116",
                                "Blueharbor Capital",
                                "Enterprise",
                                "LATAM",
                                List.of(
                                        new TouchEvent("touch-7", "2026-05-01T07:45:00Z", "Partner", "reseller-qbr", "partner", "discovery"),
                                        new TouchEvent("touch-8", "2026-05-04T15:25:00Z", "Paid Social", "fraud-playbook", "ad_click", "consideration"),
                                        new TouchEvent("touch-9", "2026-05-11T17:05:00Z", "Direct", "security-docs", "site_visit", "decision"))),
                        new ConversionEvent("conv-4116", "Blueharbor Capital", "opportunity_created", 91000, "USD", "2026-05-11T18:00:00Z")));
    }
}
