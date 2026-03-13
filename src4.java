import java.util.*;

public class src4 {
    // Inverted Index: N-gram -> Set of Document IDs that contain it
    private final Map<String, Set<String>> ngramIndex = new HashMap<>();
    // Store total n-gram counts for each document to calculate percentage
    private final Map<String, Integer> documentTotalNGrams = new HashMap<>();

    public static void main(String[] args) {
        src4 detector = new src4();

        // 1. "Database" of existing essays
        detector.addDocument("essay_089.txt", "The quick brown fox jumps over the lazy dog in the forest");
        detector.addDocument("essay_092.txt", "Java is a high level class based object oriented programming language");

        // 2. New submission to analyze
        String newSubmission = "Java is a high level class based language that is very popular";
        detector.analyzeDocument("essay_123.txt", newSubmission);
    }

    public void addDocument(String docId, String content) {
        List<String> ngrams = extractNGrams(content, 3); // Using 3-grams for this small example
        documentTotalNGrams.put(docId, ngrams.size());

        for (String gram : ngrams) {
            ngramIndex.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    public void analyzeDocument(String docId, String content) {
        List<String> ngrams = extractNGrams(content, 3);
        int totalGrams = ngrams.size();
        System.out.println("analyzeDocument(\"" + docId + "\") -> Extracted " + totalGrams + " n-grams");

        // Map to track how many n-grams match with each existing document
        Map<String, Integer> matchCounts = new HashMap<>();

        for (String gram : ngrams) {
            if (ngramIndex.containsKey(gram)) {
                for (String existingDocId : ngramIndex.get(gram)) {
                    matchCounts.put(existingDocId, matchCounts.getOrDefault(existingDocId, 0) + 1);
                }
            }
        }

        // Calculate similarity for each matching document
        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            String existingId = entry.getKey();
            int matches = entry.getValue();
            // Similarity = (matching n-grams / total n-grams in NEW doc) * 100
            double similarity = (matches / (double) totalGrams) * 100;

            String status = similarity > 50 ? "(PLAGIARISM DETECTED)" : "(suspicious)";
            System.out.printf("→ Found %d matching n-grams with \"%s\"%n", matches, existingId);
            System.out.printf("→ Similarity: %.1f%% %s%n", similarity, status);
        }
    }

    private List<String> extractNGrams(String text, int n) {
        List<String> ngrams = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(j < n - 1 ? " " : "");
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }
}