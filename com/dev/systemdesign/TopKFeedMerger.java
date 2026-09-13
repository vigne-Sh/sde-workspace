/*
Merge k user timelines (each already sorted by time) into one feed ordered most
recent first, using a heap of iterators for efficiency, common system design /
merge-k-sorted-lists interview question.

status - completed
 */

package com.dev.systemdesign;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import com.dev.logger.basePrinter;

class TopKFeedMerger extends basePrinter{

    static class Post {
        String user;
        long timestamp;
        String content;

        Post(String user, long timestamp, String content){
            this.user = user;
            this.timestamp = timestamp;
            this.content = content;
        }

        public String toString(){
            return "[t=" + timestamp + " @" + user + "] " + content;
        }
    }

    private static class TimelineCursor {
        Iterator<Post> iterator;
        Post current;

        TimelineCursor(List<Post> timeline){
            this.iterator = timeline.iterator();
            advance();
        }

        void advance(){
            current = iterator.hasNext() ? iterator.next() : null;
        }
    }

    // each input timeline must already be sorted most-recent-first
    static List<Post> mergeTimelines(List<List<Post>> timelines){
        PriorityQueue<TimelineCursor> heap = new PriorityQueue<>(
                (a, b) -> Long.compare(b.current.timestamp, a.current.timestamp));

        for (List<Post> timeline : timelines){
            if (!timeline.isEmpty()){
                heap.add(new TimelineCursor(timeline));
            }
        }

        List<Post> merged = new ArrayList<>();
        while (!heap.isEmpty()){
            TimelineCursor cursor = heap.poll();
            merged.add(cursor.current);
            cursor.advance();
            if (cursor.current != null){
                heap.add(cursor);
            }
        }
        return merged;
    }

    static List<Post> topK(List<List<Post>> timelines, int k){
        List<Post> merged = mergeTimelines(timelines);
        return merged.subList(0, Math.min(k, merged.size()));
    }

    public static void main(String[] args) {
        List<Post> timelineAlice = List.of(
                new Post("alice", 100, "alice post at 100"),
                new Post("alice", 70, "alice post at 70"),
                new Post("alice", 20, "alice post at 20"));

        List<Post> timelineBob = List.of(
                new Post("bob", 90, "bob post at 90"),
                new Post("bob", 50, "bob post at 50"));

        List<Post> timelineCara = List.of(
                new Post("cara", 110, "cara post at 110"),
                new Post("cara", 60, "cara post at 60"),
                new Post("cara", 10, "cara post at 10"));

        List<List<Post>> allTimelines = List.of(timelineAlice, timelineBob, timelineCara);

        List<Post> fullFeed = mergeTimelines(allTimelines);
        logp("full merged feed, most recent first:");
        for (Post post : fullFeed){
            logp(post.toString());
        }

        logp("top 3 posts across all timelines:");
        for (Post post : topK(allTimelines, 3)){
            logp(post.toString());
        }
    }
}
