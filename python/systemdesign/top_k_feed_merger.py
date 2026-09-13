"""
Merge k user timelines, each already sorted by time descending, into
a single feed ordered most-recent-first using a heap-based k-way
merge instead of concatenating and re-sorting everything.

status - completed
"""

import heapq

from base_logger.logging_event import create_logger


class TopKFeedMerger:

    log = create_logger(__name__)

    def merge_feeds(self, timelines, limit=None):
        heap = []
        for feed_index, timeline in enumerate(timelines):
            if timeline:
                post = timeline[0]
                heapq.heappush(heap, (-post["timestamp"], feed_index, 0, post))

        merged = []
        while heap and (limit is None or len(merged) < limit):
            neg_ts, feed_index, item_index, post = heapq.heappop(heap)
            merged.append(post)

            next_index = item_index + 1
            timeline = timelines[feed_index]
            if next_index < len(timeline):
                next_post = timeline[next_index]
                heapq.heappush(heap, (-next_post["timestamp"], feed_index, next_index, next_post))

        return merged


if __name__ == "__main__":
    log = create_logger("top_k_feed_merger_demo")

    alice_timeline = [
        {"user": "alice", "timestamp": 100, "text": "alice post 3"},
        {"user": "alice", "timestamp": 60, "text": "alice post 2"},
        {"user": "alice", "timestamp": 10, "text": "alice post 1"},
    ]
    bob_timeline = [
        {"user": "bob", "timestamp": 90, "text": "bob post 2"},
        {"user": "bob", "timestamp": 20, "text": "bob post 1"},
    ]
    carol_timeline = [
        {"user": "carol", "timestamp": 150, "text": "carol post 1"},
    ]

    merger = TopKFeedMerger()
    merged_feed = merger.merge_feeds([alice_timeline, bob_timeline, carol_timeline])

    timestamps = [post["timestamp"] for post in merged_feed]
    log.info("merged feed timestamps: %s", timestamps)
    log.info("sorted descending: %s", timestamps == sorted(timestamps, reverse=True))

    top_3 = merger.merge_feeds([alice_timeline, bob_timeline, carol_timeline], limit=3)
    log.info("top 3 most recent posts: %s", [(p["user"], p["timestamp"]) for p in top_3])
