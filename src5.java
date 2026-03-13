import java.util.*;
import java.util.concurrent.*;

public class src5 {
    // 1. Core Data Structures
    private final Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private final Map<String, Integer> trafficSources = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        src5 dashboard = new src5();

        // Simulating incoming stream
        dashboard.processEvent("/news/tech", "user_1", "Google");
        dashboard.processEvent("/news/tech", "user_2", "Facebook");
        dashboard.processEvent("/news/tech", "user_1", "Google"); // Duplicate user
        dashboard.processEvent("/sports/final", "user_3", "Direct");
        dashboard.processEvent("/sports/final", "user_4", "Google");

        // Display Dashboard
        dashboard.getDashboard();
    }

    public void processEvent(String url, String userId, String source) {
        // Update Total Views (O(1))
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // Update Unique Visitors (O(1) average)
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);

        // Update Traffic Source (O(1))
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    public void getDashboard() {
        System.out.println("======= REAL-TIME ANALYTICS DASHBOARD =======");

        // Use a PriorityQueue to find Top 10 (Efficiency: O(N log K))
        PriorityQueue<Map.Entry<String, Integer>> topPages = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue)
        );

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            topPages.offer(entry);
            if (topPages.size() > 10) {
                topPages.poll(); // Remove the page with the lowest views
            }
        }

        // Display Top Pages
        System.out.println("Top 10 Pages:");
        List<Map.Entry<String, Integer>> sortedTop = new ArrayList<>(topPages);
        sortedTop.sort((a, b) -> b.getValue().compareTo(a.getValue())); // Sort for display

        for (Map.Entry<String, Integer> entry : sortedTop) {
            String url = entry.getKey();
            int total = entry.getValue();
            int unique = uniqueVisitors.get(url).size();
            System.out.printf("- %s: %d views (%d unique)%n", url, total, unique);
        }

        System.out.println("\nTraffic Sources:");
        trafficSources.forEach((source, count) ->
                System.out.println("- " + source + ": " + count));
        System.out.println("=============================================");
    }
}