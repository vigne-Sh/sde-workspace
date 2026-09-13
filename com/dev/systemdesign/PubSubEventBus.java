/*
Simple publish/subscribe event bus supporting multiple topics and multiple
subscribers per topic, common system design building block interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import com.dev.logger.basePrinter;

class PubSubEventBus extends basePrinter{

    private final HashMap<String, List<Consumer<String>>> topicSubscribers;

    PubSubEventBus(){
        this.topicSubscribers = new HashMap<>();
    }

    void subscribe(String topic, Consumer<String> subscriber){
        topicSubscribers.computeIfAbsent(topic, k -> new ArrayList<>()).add(subscriber);
    }

    void unsubscribe(String topic, Consumer<String> subscriber){
        List<Consumer<String>> subscribers = topicSubscribers.get(topic);
        if (subscribers != null){
            subscribers.remove(subscriber);
        }
    }

    void publish(String topic, String message){
        List<Consumer<String>> subscribers = topicSubscribers.get(topic);
        if (subscribers == null){
            return;
        }
        // snapshot to avoid concurrent modification if a subscriber unsubscribes mid-publish
        for (Consumer<String> subscriber : new ArrayList<>(subscribers)){
            subscriber.accept(message);
        }
    }

    public static void main(String[] args) {
        PubSubEventBus bus = new PubSubEventBus();

        Consumer<String> newsSubscriberOne = msg -> logp("newsSubscriberOne got -> " + msg);
        Consumer<String> newsSubscriberTwo = msg -> logp("newsSubscriberTwo got -> " + msg);
        Consumer<String> sportsSubscriber = msg -> logp("sportsSubscriber got -> " + msg);

        bus.subscribe("news", newsSubscriberOne);
        bus.subscribe("news", newsSubscriberTwo);
        bus.subscribe("sports", sportsSubscriber);

        logp("publishing to news topic...");
        bus.publish("news", "breaking: system design interview practice repo grows");

        logp("publishing to sports topic...");
        bus.publish("sports", "local team wins championship");

        bus.unsubscribe("news", newsSubscriberTwo);
        logp("newsSubscriberTwo unsubscribed, publishing to news again...");
        bus.publish("news", "second news update");

        logp("publishing to topic with no subscribers (no-op)...");
        bus.publish("weather", "sunny today");
    }
}
