import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    long timestamp; // epoch time in seconds
    String account;

    public Transaction(int id, int amount, String merchant, long timestamp, String account) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.timestamp = timestamp;
        this.account = account;
    }

    @Override
    public String toString() { return "ID:" + id + " ($" + amount + ")"; }
}

public class src9 {
    private List<Transaction> transactions = new ArrayList<>();

    public static void main(String[] args) {
        src9 system = new src9();

        // Setup sample data
        system.transactions.add(new Transaction(1, 500, "Store A", 1000, "Acc1"));
        system.transactions.add(new Transaction(2, 300, "Store B", 2000, "Acc2"));
        system.transactions.add(new Transaction(3, 200, "Store C", 3000, "Acc3"));
        system.transactions.add(new Transaction(4, 500, "Store A", 1500, "Acc4")); // Duplicate of ID 1

        System.out.println("Two-Sum (Target 500): " + system.findTwoSum(500));
        System.out.println("Duplicate Detection: " + system.detectDuplicates());
    }

    // Classic Two-Sum: O(n) time using Hash Map
    public List<String> findTwoSum(int target) {
        Map<Integer, Transaction> complements = new HashMap<>();
        List<String> pairs = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (complements.containsKey(complement)) {
                pairs.add("(" + complements.get(complement).id + ", " + t.id + ")");
            }
            complements.put(t.amount, t);
        }
        return pairs;
    }

    // Duplicate detection: Same amount + same merchant, different accounts
    public List<String> detectDuplicates() {
        // Key: amount + merchant name
        Map<String, List<Transaction>> recordMap = new HashMap<>();
        List<String> duplicates = new ArrayList<>();

        for (Transaction t : transactions) {
            String key = t.amount + "_" + t.merchant;
            if (recordMap.containsKey(key)) {
                for (Transaction existing : recordMap.get(key)) {
                    if (!existing.account.equals(t.account)) {
                        duplicates.add("Suspicious: ID " + existing.id + " and " + t.id + " (Same amount/merchant, different accounts)");
                    }
                }
            }
            recordMap.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }
        return duplicates;
    }

    // Two-Sum with time window (e.g., within 3600 seconds / 1 hour)
    public List<String> findTwoSumWithTime(int target, long windowSeconds) {
        Map<Integer, Transaction> complements = new HashMap<>();
        List<String> results = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (complements.containsKey(complement)) {
                Transaction other = complements.get(complement);
                if (Math.abs(t.timestamp - other.timestamp) <= windowSeconds) {
                    results.add("Time-Linked Pair: " + other.id + " & " + t.id);
                }
            }
            complements.put(t.amount, t);
        }
        return results;
    }
}