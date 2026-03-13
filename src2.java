import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class src2 {

    // 1. Instant stock lookup: ProductID -> StockCount
    private final ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // 2. Waiting list: ProductID -> Queue of UserIDs
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> waitingLists = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // Create an instance of our manager
        src2 manager = new src2();

        // Setup: Initialize a product with limited stock (e.g., 2 units for testing)
        String item = "IPHONE15_256GB";
        manager.addProduct(item, 2);

        System.out.println("--- Flash Sale Started ---");

        // Test Case 1: Check initial stock
        System.out.println(manager.checkStock(item));

        // Test Case 2: Successful purchases
        System.out.println(manager.purchaseItem(item, 12345)); // Success, 1 left
        System.out.println(manager.purchaseItem(item, 67890)); // Success, 0 left

        // Test Case 3: Stock runs out, add to waiting list
        System.out.println(manager.purchaseItem(item, 99999)); // Waiting list #1
        System.out.println(manager.purchaseItem(item, 88888)); // Waiting list #2

        System.out.println("--- Simulation Complete ---");
    }

    // Initialize a product
    public void addProduct(String productId, int initialStock) {
        inventory.put(productId, new AtomicInteger(initialStock));
        waitingLists.put(productId, new ConcurrentLinkedQueue<>());
    }

    // O(1) Stock Check
    public String checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return (stock != null) ? stock.get() + " units available" : "Product not found";
    }

    // Atomic Purchase Operation
    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null) return "Product not found";

        // Thread-safe atomic decrement
        int previousValue = stock.getAndUpdate(current -> current > 0 ? current - 1 : 0);

        if (previousValue > 0) {
            return "purchaseItem(\"" + productId + "\", userId=" + userId + ") -> Success, " + (previousValue - 1) + " units remaining";
        } else {
            ConcurrentLinkedQueue<Integer> queue = waitingLists.get(productId);
            queue.add(userId);
            return "purchaseItem(\"" + productId + "\", userId=" + userId + ") -> Added to waiting list, position #" + queue.size();
        }
    }
}