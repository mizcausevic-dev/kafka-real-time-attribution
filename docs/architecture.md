# Kafka Real-Time Attribution Architecture

## Intent

This repo demonstrates a production-minded attribution workflow:

- click and campaign touches arrive as ordered events
- sessions group those touches into a commercial journey
- conversions close the journey and trigger weighted channel credit
- the same event story can be replayed without corrupting revenue attribution

## Core Pieces

- `AttributionService`
  - replays seeded journeys in-memory for local proof
- `KafkaTopologyFactory`
  - describes how a Kafka Streams topology would flow source topics into attributed outputs
- `PageController`
  - operator-facing HTML proof surfaces
- `LedgerController`
  - machine-readable APIs for dashboards and downstream tools
- `docker-compose.yml`
  - Redpanda stack for local Kafka infrastructure

## Routes

- `/`
  - overview of channel revenue and attribution posture
- `/journeys`
  - journey-by-journey weighted credit board
- `/verification`
  - local proof payload
- `/topology`
  - stream contract and topic flow summary
- `/api/dashboard/summary`
  - high-level metrics
- `/api/events`
  - raw seeded attribution journeys
- `/api/journeys`
  - weighted journey summaries
- `/api/channels`
  - aggregated channel credit
- `/api/topology`
  - topic flow summary
- `/api/sample`
  - compact verification payload

## Why This Matters

Attribution systems often fail because they either:

- flatten everything into last-click bias
- lose replay safety
- or become too opaque for finance and executive reporting

This repo focuses on keeping the channel story explainable while preserving
ordered event semantics.
