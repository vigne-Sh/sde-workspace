/*
Simple publish/subscribe event bus supporting multiple topics, multiple
subscribers per topic, and unsubscribe handles - a lightweight in-process
message broker.

status - completed
*/

import { logp } from "../utils/logger";

type Handler<T> = (payload: T) => void;

class PubSubEventBus {
  private readonly subscribersByTopic: Map<string, Set<Handler<unknown>>> = new Map();

  subscribe<T>(topic: string, handler: Handler<T>): () => void {
    let subscribers = this.subscribersByTopic.get(topic);
    if (!subscribers) {
      subscribers = new Set();
      this.subscribersByTopic.set(topic, subscribers);
    }
    const wrapped = handler as Handler<unknown>;
    subscribers.add(wrapped);

    return () => {
      subscribers!.delete(wrapped);
    };
  }

  publish<T>(topic: string, payload: T): number {
    const subscribers = this.subscribersByTopic.get(topic);
    if (!subscribers || subscribers.size === 0) {
      return 0;
    }
    for (const handler of subscribers) {
      handler(payload);
    }
    return subscribers.size;
  }

  subscriberCount(topic: string): number {
    return this.subscribersByTopic.get(topic)?.size ?? 0;
  }
}

// usage scenarios

interface OrderEvent {
  orderId: number;
  status: string;
}

const bus = new PubSubEventBus();

const received: string[] = [];
const unsubscribeEmail = bus.subscribe<OrderEvent>("orders", (event) => {
  received.push(`email-service saw order ${event.orderId}: ${event.status}`);
});
bus.subscribe<OrderEvent>("orders", (event) => {
  received.push(`analytics-service saw order ${event.orderId}: ${event.status}`);
});
bus.subscribe<string>("logs", (line) => {
  received.push(`log-sink: ${line}`);
});

const deliveredCount = bus.publish<OrderEvent>("orders", { orderId: 42, status: "placed" });
logp(`published to "orders", delivered to ${deliveredCount} subscribers`);
received.forEach((line) => logp(line));
received.length = 0;

unsubscribeEmail();
const deliveredAfterUnsub = bus.publish<OrderEvent>("orders", { orderId: 42, status: "shipped" });
logp(`after unsubscribing email-service, delivered to ${deliveredAfterUnsub} subscriber(s)`);
received.forEach((line) => logp(line));

logp(`publishing to topic with no subscribers -> delivered to ${bus.publish("nobody-listening", "x")} subscribers`);
