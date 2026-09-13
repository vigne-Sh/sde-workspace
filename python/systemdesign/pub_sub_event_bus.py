"""
Publish/subscribe event bus supporting multiple topics, each with
multiple subscribers. Publishing a message on a topic fans it out to
every subscriber callback registered for that topic.

status - completed
"""

from collections import defaultdict

from base_logger.logging_event import create_logger


class PubSubEventBus:

    log = create_logger(__name__)

    def __init__(self):
        self.subscribers = defaultdict(list)
        self._next_subscription_id = 0
        self._subscription_index = {}

    def subscribe(self, topic, callback):
        subscription_id = self._next_subscription_id
        self._next_subscription_id += 1
        self.subscribers[topic].append((subscription_id, callback))
        self._subscription_index[subscription_id] = topic
        return subscription_id

    def unsubscribe(self, subscription_id):
        topic = self._subscription_index.pop(subscription_id, None)
        if topic is None:
            return False
        self.subscribers[topic] = [
            (sid, cb) for sid, cb in self.subscribers[topic] if sid != subscription_id
        ]
        return True

    def publish(self, topic, message):
        delivered = 0
        for _, callback in list(self.subscribers.get(topic, [])):
            callback(topic, message)
            delivered += 1
        return delivered


if __name__ == "__main__":
    log = create_logger("pub_sub_demo")

    bus = PubSubEventBus()
    received = {"orders": [], "payments": []}

    def order_handler_a(topic, message):
        received["orders"].append(("A", message))

    def order_handler_b(topic, message):
        received["orders"].append(("B", message))

    def payment_handler(topic, message):
        received["payments"].append(message)

    bus.subscribe("orders", order_handler_a)
    sub_b = bus.subscribe("orders", order_handler_b)
    bus.subscribe("payments", payment_handler)

    delivered = bus.publish("orders", "order-created:1001")
    log.info("published to 'orders', delivered to %d subscribers", delivered)
    log.info("received orders: %s", received["orders"])

    bus.publish("payments", "payment-captured:1001")
    log.info("received payments: %s", received["payments"])

    bus.unsubscribe(sub_b)
    received["orders"].clear()
    bus.publish("orders", "order-created:1002")
    log.info("after unsubscribing handler B, received orders: %s", received["orders"])
    log.info("expected only handler A's delivery")

    unrelated_delivered = bus.publish("unknown-topic", "noop")
    log.info("publish to topic with no subscribers -> delivered=%d", unrelated_delivered)
