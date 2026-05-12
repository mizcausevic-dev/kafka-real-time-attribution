package com.mizcausevic.kafkarealtimeattribution.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.AttributionPayload;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.ChannelSummary;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.DashboardSummary;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.JourneySummary;
import com.mizcausevic.kafkarealtimeattribution.models.AttributionModels.WeightedTouch;

public final class HtmlRenderer {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private HtmlRenderer() {
    }

    public static String renderOverview(DashboardSummary summary, List<ChannelSummary> channels) {
        String channelCards = channels.stream().limit(4)
                .map(channel -> """
                        <div class="card">
                          <div class="kicker">%s</div>
                          <h2>$%s attributed revenue</h2>
                          <p>%s</p>
                          <div class="note">Weighted credit: %s • Influenced conversions: %d</div>
                        </div>
                        """.formatted(
                        escape(channel.channel()),
                        money(channel.attributedRevenue()),
                        escape(channel.leadInsight()),
                        money(channel.weightedCredit()),
                        channel.influencedConversions()))
                .collect(Collectors.joining());

        return pageShell("Kafka Real-Time Attribution - Overview", "kafka real-time attribution", """
                <h1>Real-time attribution that keeps revenue credit attached to the touches that actually created it.</h1>
                <p class="lede">
                  Kafka Real-Time Attribution models click, session, and conversion events as an ordered stream so
                  SaaS teams can assign weighted channel credit without breaking replay safety or double-counting revenue.
                </p>
                <div class="pill-row">
                  <div class="pill">Kafka Streams topology</div>
                  <div class="pill">weighted multi-touch credit</div>
                  <div class="pill">Redpanda compose stack</div>
                  <div class="pill">operator reporting surface</div>
                </div>
                <div class="stats">
                  <div class="stat"><div class="label">Touches replayed</div><div class="value">%d</div><div class="copy">Ordered click and session records flowing into the attribution ledger.</div></div>
                  <div class="stat"><div class="label">Journeys linked</div><div class="value">%d</div><div class="copy">Conversion journeys stitched from discovery, consideration, and decision events.</div></div>
                  <div class="stat"><div class="label">Revenue analyzed</div><div class="value">$%s</div><div class="copy">Attributed contract value flowing through the weighted channel model.</div></div>
                  <div class="stat"><div class="label">Top channel</div><div class="value">%s</div><div class="copy">%s</div></div>
                </div>
                <div class="section"><div class="section-grid">%s</div></div>
                """.formatted(
                summary.touchCount(),
                summary.sessionCount(),
                money(summary.totalRevenue()),
                escape(summary.topChannel()),
                escape(summary.leadRecommendation()),
                channelCards));
    }

    public static String renderJourneys(List<JourneySummary> journeys) {
        String cards = journeys.stream()
                .map(journey -> """
                        <div class="card">
                          <div class="kicker">%s • %s</div>
                          <h2>%s</h2>
                          <p>Conversion type: %s • Revenue: $%s</p>
                          <ul>%s</ul>
                          <div class="note">%s</div>
                        </div>
                        """.formatted(
                        escape(journey.segment()),
                        escape(journey.region()),
                        escape(journey.accountName()),
                        escape(journey.conversionType()),
                        money(journey.amount()),
                        journey.weightedTouches().stream()
                                .map(HtmlRenderer::touchLine)
                                .collect(Collectors.joining()),
                        escape(journey.leadRecommendation())))
                .collect(Collectors.joining());

        return pageShell("Kafka Real-Time Attribution - Journeys", "journey board", """
                <h1>Each conversion path keeps the full revenue story instead of flattening it into last-click shortcuts.</h1>
                <p class="lede">
                  The journey view shows how weighted credit lands across discovery, nurture, and intent touches while still
                  preserving a replay-safe event trail for auditors and RevOps teams.
                </p>
                <div class="section"><div class="section-grid">%s</div></div>
                """.formatted(cards));
    }

    public static String renderVerification(AttributionPayload payload) {
        return pageShell("Kafka Real-Time Attribution - Verification", "verification lane", """
                <h1>The repo proves the attribution math locally even before Kafka is running.</h1>
                <p class="lede">
                  The local verification payload replays sample journeys in-memory and mirrors the structure the Kafka Streams
                  topology would emit after joining touches, sessions, and conversions in the live event bus.
                </p>
                <div class="json"><pre>%s</pre></div>
                """.formatted(escape(prettyJson(payload))));
    }

    public static String renderDocs(Map<String, String> topology) {
        String rows = topology.entrySet().stream()
                .map(entry -> "<tr><th>%s</th><td>%s</td></tr>".formatted(escape(entry.getKey()), escape(entry.getValue())))
                .collect(Collectors.joining());

        return pageShell("Kafka Real-Time Attribution - Docs", "topology docs", """
                <h1>The streaming contract is as important as the math.</h1>
                <p class="lede">
                  This repo ships both an operator-facing attribution proof layer and a Kafka-style stream topology for production-minded teams.
                </p>
                <div class="section">
                  <table class="queue">
                    <tbody>%s</tbody>
                  </table>
                </div>
                """.formatted(rows));
    }

    private static String touchLine(WeightedTouch touch) {
        return "<li>%s • %s • %s%% • $%s</li>".formatted(
                escape(touch.channel()),
                escape(touch.campaign()),
                percent(touch.weight()),
                money(touch.attributedRevenue()));
    }

    private static String pageShell(String title, String kicker, String body) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>%s</title>
                  <style>
                    :root {
                      --bg: #07111d;
                      --panel: #0d1a2b;
                      --panel-2: #12233a;
                      --line: #1d3655;
                      --text: #eef2ff;
                      --muted: #98a7c2;
                      --accent: #68b7ff;
                    }
                    * { box-sizing: border-box; }
                    body {
                      margin: 0;
                      font-family: "Segoe UI", Inter, sans-serif;
                      background: linear-gradient(180deg, #07111d 0%%, #091827 100%%);
                      color: var(--text);
                    }
                    .page {
                      width: 1440px;
                      margin: 0 auto;
                      padding: 48px 52px 64px;
                      background:
                        radial-gradient(circle at top right, rgba(104,183,255,0.16), transparent 30%%),
                        linear-gradient(180deg, rgba(11,25,41,0.95), rgba(6,14,24,0.98));
                      min-height: 920px;
                    }
                    .frame {
                      border: 1px solid var(--line);
                      border-radius: 34px;
                      padding: 28px 32px 36px;
                      background: rgba(11, 22, 37, 0.88);
                    }
                    .eyebrow {
                      color: var(--accent);
                      font-size: 15px;
                      letter-spacing: 0.34em;
                      text-transform: uppercase;
                      margin-bottom: 18px;
                      font-weight: 700;
                    }
                    h1 {
                      margin: 0;
                      font-size: 66px;
                      line-height: 0.98;
                      color: #f4f1e3;
                      font-family: Georgia, "Times New Roman", serif;
                      max-width: 1120px;
                    }
                    .lede {
                      margin-top: 18px;
                      max-width: 920px;
                      color: var(--muted);
                      font-size: 18px;
                      line-height: 1.6;
                    }
                    .pill-row {
                      display: flex;
                      gap: 12px;
                      flex-wrap: wrap;
                      margin-top: 24px;
                    }
                    .pill {
                      border-radius: 999px;
                      padding: 10px 16px;
                      background: #1a2f4d;
                      border: 1px solid #29486e;
                      color: #f5f8ff;
                      font-size: 15px;
                      font-weight: 600;
                    }
                    .stats {
                      display: grid;
                      grid-template-columns: repeat(4, 1fr);
                      gap: 18px;
                      margin-top: 28px;
                    }
                    .stat {
                      padding: 22px 22px 18px;
                      border-radius: 24px;
                      background: #12233a;
                      border: 1px solid #25415f;
                      min-height: 168px;
                    }
                    .label {
                      color: #a8b6cd;
                      text-transform: uppercase;
                      letter-spacing: 0.12em;
                      font-size: 13px;
                      margin-bottom: 14px;
                    }
                    .value {
                      color: #f4f1e3;
                      font-family: Georgia, "Times New Roman", serif;
                      font-size: 48px;
                      line-height: 0.95;
                      margin-bottom: 12px;
                    }
                    .copy {
                      color: #c1cadc;
                      font-size: 16px;
                      line-height: 1.5;
                    }
                    .section {
                      margin-top: 34px;
                      border-radius: 28px;
                      border: 1px solid #203654;
                      background: #0d1524;
                      padding: 28px;
                    }
                    .section-grid {
                      display: grid;
                      grid-template-columns: repeat(3, 1fr);
                      gap: 18px;
                    }
                    .card {
                      border-radius: 22px;
                      border: 1px solid #263d5f;
                      background: #131e32;
                      padding: 22px;
                      min-height: 250px;
                    }
                    .card .kicker {
                      color: var(--accent);
                      font-size: 13px;
                      text-transform: uppercase;
                      letter-spacing: 0.18em;
                      margin-bottom: 18px;
                      font-weight: 700;
                    }
                    .card h2 {
                      font-size: 24px;
                      line-height: 1.15;
                      margin: 0 0 14px;
                      color: #f4f1e3;
                      font-family: Georgia, "Times New Roman", serif;
                    }
                    .card p, .card li, .queue td, .queue th, .note {
                      color: #bdc7d9;
                      font-size: 16px;
                      line-height: 1.55;
                      margin: 0;
                    }
                    .card ul {
                      padding-left: 18px;
                      margin: 0;
                    }
                    .note {
                      margin-top: 16px;
                      color: #8fbfff;
                    }
                    .queue {
                      width: 100%%;
                      border-collapse: collapse;
                    }
                    .queue th, .queue td {
                      text-align: left;
                      padding: 14px 12px;
                      border-bottom: 1px solid #203654;
                      vertical-align: top;
                    }
                    .queue th {
                      width: 260px;
                      color: #8fbfff;
                      text-transform: uppercase;
                      letter-spacing: 0.12em;
                      font-size: 12px;
                    }
                    .json {
                      background: #07101b;
                      border: 1px solid #284462;
                      border-radius: 22px;
                      padding: 24px;
                      margin-top: 24px;
                    }
                    pre {
                      margin: 0;
                      white-space: pre-wrap;
                      word-break: break-word;
                      color: #d7f7da;
                      font-size: 15px;
                      line-height: 1.45;
                      font-family: Consolas, "SFMono-Regular", monospace;
                    }
                  </style>
                </head>
                <body>
                  <div class="page">
                    <div class="frame">
                      <div class="eyebrow">%s</div>
                      %s
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(escape(title), escape(kicker), body);
    }

    private static String prettyJson(Object value) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return "{\"error\":\"json-render-failed\"}";
        }
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String money(double value) {
        return String.format("%,.2f", value);
    }

    private static int percent(double weight) {
        return (int) Math.round(weight * 100);
    }
}
