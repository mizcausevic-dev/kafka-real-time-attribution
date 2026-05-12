package com.mizcausevic.kafkarealtimeattribution.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mizcausevic.kafkarealtimeattribution.data.SampleAttributionData;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.AttributionPayload;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.AttributionJourney;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.ChannelSummary;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.DashboardSummary;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.JourneySummary;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.TouchEvent;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.WeightedTouch;
import com.mizcausevic.kafkarealtimeattribution.streams.KafkaTopologyFactory;

@Service
public class AttributionService {
    public List<AttributionJourney> journeys() {
        return SampleAttributionData.journeys();
    }

    public List<JourneySummary> journeySummaries() {
        return journeys().stream()
                .map(this::summarizeJourney)
                .toList();
    }

    public List<ChannelSummary> channelSummaries() {
        Map<String, Double> revenueByChannel = new LinkedHashMap<>();
        Map<String, Double> weightByChannel = new LinkedHashMap<>();
        Map<String, Integer> influencedByChannel = new LinkedHashMap<>();

        for (JourneySummary journey : journeySummaries()) {
            journey.weightedTouches().forEach(touch -> {
                revenueByChannel.merge(touch.channel(), touch.attributedRevenue(), Double::sum);
                weightByChannel.merge(touch.channel(), touch.weight(), Double::sum);
                influencedByChannel.merge(touch.channel(), 1, Integer::sum);
            });
        }

        return revenueByChannel.entrySet().stream()
                .map(entry -> new ChannelSummary(
                        entry.getKey(),
                        round(weightByChannel.get(entry.getKey())),
                        round(entry.getValue()),
                        influencedByChannel.get(entry.getKey()),
                        leadInsight(entry.getKey(), entry.getValue())))
                .sorted(Comparator.comparingDouble(ChannelSummary::attributedRevenue).reversed())
                .toList();
    }

    public DashboardSummary dashboardSummary() {
        List<JourneySummary> journeys = journeySummaries();
        List<ChannelSummary> channels = channelSummaries();
        double revenue = journeys.stream().mapToDouble(JourneySummary::amount).sum();
        int touchCount = journeys().stream().mapToInt(journey -> journey.session().touches().size()).sum();
        ChannelSummary topChannel = channels.getFirst();
        return new DashboardSummary(
                touchCount,
                journeys.size(),
                journeys.size(),
                round(revenue),
                topChannel.channel(),
                round(topChannel.attributedRevenue()),
                "Keep partner, webinar, and brand-search intent inside the same ledger so conversion credit stays trustworthy in executive reporting.");
    }

    public AttributionPayload samplePayload() {
        return new AttributionPayload(
                dashboardSummary(),
                journeySummaries(),
                channelSummaries(),
                KafkaTopologyFactory.summary(),
                "The local demo replays attribution math in-memory while the repo also ships a Kafka Streams topology and Redpanda compose stack.");
    }

    public Map<String, String> topologySummary() {
        return KafkaTopologyFactory.summary();
    }

    private JourneySummary summarizeJourney(AttributionJourney journey) {
        List<TouchEvent> touches = journey.session().touches();
        List<Double> weights = attributionWeights(touches.size());
        List<WeightedTouch> weightedTouches = new ArrayList<>();

        for (int index = 0; index < touches.size(); index++) {
            TouchEvent touch = touches.get(index);
            double weight = weights.get(index);
            weightedTouches.add(new WeightedTouch(
                    touch.channel(),
                    touch.campaign(),
                    round(weight),
                    round(journey.conversion().amount() * weight),
                    touch.stage()));
        }

        return new JourneySummary(
                journey.journeyId(),
                journey.session().accountName(),
                journey.session().segment(),
                journey.session().region(),
                journey.conversion().conversionType(),
                round(journey.conversion().amount()),
                weightedTouches,
                recommendationFor(journey.session().touches()));
    }

    private List<Double> attributionWeights(int touchCount) {
        if (touchCount <= 1) {
            return List.of(1.0);
        }
        if (touchCount == 2) {
            return List.of(0.4, 0.6);
        }

        double first = 0.2;
        double last = 0.45;
        double middlePool = 0.35;
        int middleCount = touchCount - 2;
        double middleWeight = middlePool / middleCount;

        List<Double> weights = new ArrayList<>();
        weights.add(first);
        for (int index = 0; index < middleCount; index++) {
            weights.add(middleWeight);
        }
        weights.add(last);
        return weights;
    }

    private String recommendationFor(List<TouchEvent> touches) {
        String channels = touches.stream().map(TouchEvent::channel).collect(Collectors.joining(", "));
        return "Keep " + channels + " in the same ordered event stream so replay and deduplication do not corrupt attribution credit.";
    }

    private String leadInsight(String channel, double revenue) {
        if (channel.equals("Direct") || channel.equals("Brand Search")) {
            return "High-intent channels are closing late-stage demand and should stay linked to earlier discovery touches.";
        }
        if (channel.equals("Partner") || channel.equals("Webinar")) {
            return "Mid-funnel education and partner influence are moving meaningful commercial value.";
        }
        return "This channel is shaping conversion paths but should be compared against spend and holdout tests.";
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
