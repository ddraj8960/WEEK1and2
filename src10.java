import java.util.*;

class VideoData {
    String id;
    String content; // Simulated video data

    public VideoData(String id, String content) {
        this.id = id;
        this.content = content;
    }
}

public class src10 {
    private final int L1_SIZE = 2; // Small size for demonstration
    private final int L2_SIZE = 5;
    private final int PROMOTION_THRESHOLD = 2;

    // L1: In-memory LRU Cache
    private final LinkedHashMap<String, VideoData> l1Cache = new LinkedHashMap<>(L1_SIZE, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
            return size() > L1_SIZE;
        }
    };

    // L2: SSD-backed LRU Cache (Simulated with paths)
    private final LinkedHashMap<String, String> l2Cache = new LinkedHashMap<>(L2_SIZE, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > L2_SIZE;
        }
    };

    private final Map<String, Integer> accessCounts = new HashMap<>();

    // Stats
    private int l1Hits = 0, l2Hits = 0, l3Hits = 0, totalRequests = 0;

    public static void main(String[] args) {
        src10 netflixCache = new src10();

        System.out.println("--- Video Streaming Cache Simulation ---");
        netflixCache.getVideo("video_123"); // L3 Hit -> Add to L2
        netflixCache.getVideo("video_123"); // L2 Hit -> Count=2
        netflixCache.getVideo("video_123"); // L2 Hit -> Promotion to L1
        netflixCache.getVideo("video_123"); // L1 Hit

        netflixCache.getStatistics();
    }

    public void getVideo(String videoId) {
        totalRequests++;
        long startTime = System.nanoTime();

        // 1. Check L1
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            logResult("L1 HIT", startTime);
            return;
        }

        // 2. Check L2
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            updateAccessCount(videoId);
            logResult("L2 HIT", startTime);
            return;
        }

        // 3. Check L3 (Database)
        l3Hits++;
        fetchFromL3(videoId);
        logResult("L3 HIT (Added to L2)", startTime);
    }

    private void updateAccessCount(String videoId) {
        int count = accessCounts.getOrDefault(videoId, 0) + 1;
        accessCounts.put(videoId, count);

        // Promote L2 -> L1
        if (count >= PROMOTION_THRESHOLD) {
            System.out.println("[Promotion] " + videoId + " moved L2 -> L1");
            l1Cache.put(videoId, new VideoData(videoId, "HighQualityStream"));
            l2Cache.remove(videoId);
        }
    }

    private void fetchFromL3(String videoId) {
        // Simulate DB Latency
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        l2Cache.put(videoId, "/ssd/storage/" + videoId + ".mp4");
        accessCounts.put(videoId, 1);
    }

    private void logResult(String level, long start) {
        double ms = (System.nanoTime() - start) / 1_000_000.0;
        System.out.printf("→ %s (%.2fms)%n", level, ms);
    }

    public void getStatistics() {
        System.out.println("\n--- Cache Statistics ---");
        System.out.println("L1 Hit Rate: " + (l1Hits * 100 / totalRequests) + "%");
        System.out.println("L2 Hit Rate: " + (l2Hits * 100 / totalRequests) + "%");
        System.out.println("L3 Hit Rate: " + (l3Hits * 100 / totalRequests) + "%");
    }
}