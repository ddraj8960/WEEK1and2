import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    // Stores the top 10 queries that pass through this prefix
    List<String> topSuggestions = new ArrayList<>();
}

public class src7 {
    private final TrieNode root = new TrieNode();
    private final Map<String, Integer> frequencies = new HashMap<>();

    public static void main(String[] args) {
        src7 engine = new src7();

        // Initial Data
        engine.updateFrequency("java tutorial", 1234567);
        engine.updateFrequency("javascript", 987654);
        engine.updateFrequency("java download", 456789);
        engine.updateFrequency("java 21 features", 100);

        // Test Autocomplete
        System.out.println("Autocomplete for 'jav':");
        List<String> results = engine.search("jav");
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i) + " (" + engine.getQueryCount(results.get(i)) + " searches)");
        }
    }

    public void updateFrequency(String query, int newCount) {
        frequencies.put(query, newCount);

        // Update the Trie with this query
        TrieNode curr = root;
        for (char c : query.toCharArray()) {
            curr.children.putIfAbsent(c, new TrieNode());
            curr = curr.children.get(c);

            // Maintain top 10 at this node
            updateNodeSuggestions(curr, query);
        }
    }

    private void updateNodeSuggestions(TrieNode node, String query) {
        if (!node.topSuggestions.contains(query)) {
            node.topSuggestions.add(query);
        }

        // Sort based on frequency from the global map
        node.topSuggestions.sort((a, b) -> frequencies.get(b) - frequencies.get(a));

        // Keep only top 10
        if (node.topSuggestions.size() > 10) {
            node.topSuggestions.remove(10);
        }
    }

    public List<String> search(String prefix) {
        TrieNode curr = root;
        for (char c : prefix.toCharArray()) {
            if (!curr.children.containsKey(c)) {
                return Collections.emptyList();
            }
            curr = curr.children.get(c);
        }
        return curr.topSuggestions;
    }

    public int getQueryCount(String query) {
        return frequencies.getOrDefault(query, 0);
    }
}