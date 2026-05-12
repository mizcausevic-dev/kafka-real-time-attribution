package com.mizcausevic.kafkarealtimeattribution.models;

import java.util.List;
import java.util.Map;

public final class AttributionModels {
    private AttributionModels() {
    }

    public record TouchEvent(
            String touchId,
            String timestamp,
            String channel,
            String campaign,
            String sourceType,
            String stage) {
    }

    public record SessionEvent(
            String sessionId,
            String accountName,
            String segment,
            String region,
            List<TouchEvent> touches) {
    }

    public record ConversionEvent(
            String conversionId,
            String accountName,
            String conversionType,
            double amount,
            String currency,
            String timestamp) {
    }

    public record AttributionJourney(
            String journeyId,
            SessionEvent session,
            ConversionEvent conversion) {
    }

    public record WeightedTouch(
            String channel,
            String campaign,
            double weight,
            double attributedRevenue,
            String stage) {
    }

    public record JourneySummary(
            String journeyId,
            String accountName,
            String segment,
            String region,
            String conversionType,
            double amount,
            List<WeightedTouch> weightedTouches,
            String leadRecommendation) {
    }

    public record ChannelSummary(
            String channel,
            double weightedCredit,
            double attributedRevenue,
            int influencedConversions,
            String leadInsight) {
    }

    public record DashboardSummary(
            int touchCount,
            int sessionCount,
            int conversionCount,
            double totalRevenue,
            String topChannel,
            double topChannelRevenue,
            String leadRecommendation) {
    }

    public record AttributionPayload(
            DashboardSummary dashboard,
            List<JourneySummary> journeys,
            List<ChannelSummary> channels,
            Map<String, String> topology,
            String verificationNote) {
    }
}
