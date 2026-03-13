import java.util.*;
import java.util.concurrent.*;

// 1. Entry class to store domain data and timing
class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        // Current time + TTL converted to milliseconds
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class src3 {
    private final int MAX_CAPACITY = 3; // Small capacity to demonstrate LRU eviction
    private int hits = 0;
    private int misses = 0;

    // 2. LinkedHashMap with 'accessOrder = true' implements LRU automatically
    private final LinkedHashMap<String, DNSEntry> cache = new LinkedHashMap<>(MAX_CAPACITY, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
            return size() > MAX_CAPACITY; // Automatically evicts Least Recently Used
        }
    };

    public static void main(String[] args) throws InterruptedException {
        src3 dnsSystem = new src3();
        String domain = "google.com";

        // FIRST RESOLVE (MISS)
        System.out.println("Step 1: " + dnsSystem.resolve(domain));

        // SECOND RESOLVE (HIT) - Should be near 0ms
        System.out.println("Step 2: " + dnsSystem.resolve(domain));

        // WAIT FOR EXPIRATION (TTL is set to 3 seconds in this demo)
        System.out.println("\n--- Waiting 4 seconds for TTL to expire ---");
        Thread.sleep(4000);

        // THIRD RESOLVE (EXPIRED -> MISS)
        System.out.println("Step 3: " + dnsSystem.resolve(domain));

        // SHOW STATS
        dnsSystem.getCacheStats();
    }

    public synchronized String resolve(String domain) {
        long startTime = System.nanoTime();
        DNSEntry entry = cache.get(domain);

        // Check if it exists and is NOT expired
        if (entry != null && !entry.isExpired()) {
            hits++;
            double duration = (System.nanoTime() - startTime) / 1_000_000.0;
            return "resolve(\"" + domain + "\") -> Cache HIT -> " + entry.ipAddress + " (retrieved in " + String.format("%.3f", duration) + "ms)";
        }

        // Cache MISS (either not found or expired)
        misses++;
        if (entry != null) {
            System.out.print("[Expired] ");
            cache.remove(domain);
        }

        // Simulate querying upstream (takes time)
        String ip = queryUpstream(domain);
        cache.put(domain, new DNSEntry(domain, ip, 3)); // 3 second TTL for testing

        double duration = (System.nanoTime() - startTime) / 1_000_000.0;
        return "resolve(\"" + domain + "\") -> Cache MISS -> Query upstream -> " + ip + " (Time: " + String.format("%.2f", duration) + "ms)";
    }

    private String queryUpstream(String domain) {
        try { Thread.sleep(100); } catch (InterruptedException e) { } // Simulate 100ms latency
        return "172.217.14." + (new Random().nextInt(255));
    }

    public void getCacheStats() {
        double total = hits + misses;
        double hitRate = (total == 0) ? 0 : (hits / total) * 100;
        System.out.println("\n--- DNS Cache Statistics ---");
        System.out.println("Hit Rate: " + String.format("%.1f", hitRate) + "%");
        System.out.println("Total Requests: " + (int)total);
    }
}