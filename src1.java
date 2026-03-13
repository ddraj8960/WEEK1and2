import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class src1{

    // username -> userId
    private ConcurrentHashMap<String, Integer> usernameMap = new ConcurrentHashMap<>();

    // username -> attempt frequency
    private ConcurrentHashMap<String, AtomicInteger> attemptFrequency = new ConcurrentHashMap<>();

    // Check username availability
    public boolean checkAvailability(String username) {

        // Track attempt frequency
        attemptFrequency.putIfAbsent(username, new AtomicInteger(0));
        attemptFrequency.get(username).incrementAndGet();

        // O(1) lookup
        return !usernameMap.containsKey(username);
    }

    // Register username
    public boolean registerUser(String username, int userId) {
        if (checkAvailability(username)) {
            usernameMap.put(username, userId);
            return true;
        }
        return false;
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // Strategy 1: append numbers
        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernameMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // Strategy 2: replace underscore with dot
        if (username.contains("_")) {
            String suggestion = username.replace("_", ".");
            if (!usernameMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, AtomicInteger> entry : attemptFrequency.entrySet()) {
            if (entry.getValue().get() > maxAttempts) {
                maxAttempts = entry.getValue().get();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + maxAttempts + " attempts)";
    }

    public static void main(String[] args) {

        src1 checker = new src1();

        checker.registerUser("john_doe", 1);
        checker.registerUser("admin", 2);

        System.out.println(checker.checkAvailability("john_doe"));   // false
        System.out.println(checker.checkAvailability("jane_smith")); // true

        System.out.println(checker.suggestAlternatives("john_doe"));

        // simulate attempts
        for(int i=0;i<10;i++) checker.checkAvailability("admin");

        System.out.println(checker.getMostAttempted());
    }
}