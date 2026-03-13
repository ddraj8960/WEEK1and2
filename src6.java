import java.util.*; // This fixes the 'cannot find symbol Map' error
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

class TokenBucket {
    private final long maxTokens;
    private final long refillRatePerSecond;
    private final AtomicLong currentTokens;
    private final AtomicLong lastRefillTimestamp;

    public TokenBucket(long maxTokens, long refillRatePerHour) {
        this.maxTokens = maxTokens;
        // Calculation: tokens per hour / 3600 seconds
        this.refillRatePerSecond = Math.max(1, refillRatePerHour / 3600);
        this.currentTokens = new AtomicLong(maxTokens);
        this.lastRefillTimestamp = new AtomicLong(System.currentTimeMillis());
    }

    public synchronized boolean allowRequest() {
        refill();
        if (currentTokens.get() > 0) {
            currentTokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long lastRefill = lastRefillTimestamp.get();
        long timePassedSeconds = (now - lastRefill) / 1000;

        if (timePassedSeconds > 0) {
            long newTokens = timePassedSeconds * refillRatePerSecond;
            if (newTokens > 0) {
                long total = Math.min(maxTokens, currentTokens.get() + newTokens);
                currentTokens.set(total);
                lastRefillTimestamp.set(now);
            }
        }
    }

    public long getRemaining() {
        refill();
        return currentTokens.get();
    }
}

public class src6 {
    // ConcurrentHashMap for thread-safe client tracking
    private final Map<String, TokenBucket> clientLimits = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        src6 gateway = new src6();
        String clientId = "abc123";

        System.out.println("--- Starting API Rate Limit Test ---");

        // Simulate rapid requests
        for (int i = 1; i <= 5; i++) {
            gateway.checkRateLimit(clientId);
        }

        System.out.println("\nFinal Status Check:");
        gateway.getRateLimitStatus(clientId);
    }

    public void checkRateLimit(String clientId) {
        // Initialize bucket if first time: 1000 tokens max, 1000/hr refill
        TokenBucket bucket = clientLimits.computeIfAbsent(clientId, k -> new TokenBucket(1000, 1000));

        if (bucket.allowRequest()) {
            System.out.println("Request from " + clientId + " -> ALLOWED (" + bucket.getRemaining() + " tokens left)");
        } else {
            System.out.println("Request from " + clientId + " -> DENIED (Limit Exceeded)");
        }
    }

    public void getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientLimits.get(clientId);
        if (bucket != null) {
            System.out.println("Client: " + clientId + " | Used: " + (1000 - bucket.getRemaining()) + "/1000");
        }
    }
}