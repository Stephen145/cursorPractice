package syoung.practiceCursor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Word autocomplete: init with words and frequencies, suggest top k by frequency,
 * pick (increment frequency), newWord (insert at frequency 1).
 */
public class WordAutocomplete {

    private final TrieNode root = new TrieNode();
    private final Map<String, Integer> frequencies = new HashMap<>();

    public WordAutocomplete(List<WordFrequency> initial) {
        if (initial != null) {
            for (WordFrequency wf : initial) {
                if (wf == null || wf.word() == null || wf.word().isEmpty()) continue;
                // last wins for duplicates
                frequencies.put(wf.word(), wf.frequency());
                addWordToTrie(wf.word());
            }
        }
    }

    /**
     * Returns the top k words that start with prefix, ordered by frequency descending.
     * Empty prefix = all words. k <= 0 returns empty list.
     */
    public List<String> suggest(String prefix, int k) {
        if (k <= 0) return List.of();
        TrieNode node = prefix == null || prefix.isEmpty() ? root : navigate(prefix);
        if (node == null) return List.of();
        Set<String> words = node.words;
        if (words.isEmpty()) return List.of();
        return words.stream()
                .sorted((a, b) -> Integer.compare(frequencies.getOrDefault(b, 0), frequencies.getOrDefault(a, 0)))
                .limit(k)
                .toList();
    }

    /** Increments the frequency of word by 1. No-op if word is not in the system. */
    public void pick(String word) {
        if (word == null || word.isEmpty() || !frequencies.containsKey(word)) return;
        frequencies.merge(word, 1, Integer::sum);
    }

    /** Inserts word at frequency 1 if not already present. If present, frequency is unchanged. */
    public void newWord(String word) {
        if (word == null || word.isEmpty()) return;
        if (frequencies.containsKey(word)) return;
        frequencies.put(word, 1);
        addWordToTrie(word);
    }

    private TrieNode navigate(String prefix) {
        TrieNode cur = root;
        for (int i = 0; i < prefix.length(); i++) {
            cur = cur.children.get(prefix.charAt(i));
            if (cur == null) return null;
        }
        return cur;
    }

    private void addWordToTrie(String word) {
        TrieNode cur = root;
        cur.words.add(word);
        for (int i = 0; i < word.length(); i++) {
            cur = cur.children.computeIfAbsent(word.charAt(i), c -> new TrieNode());
            cur.words.add(word);
        }
    }

    private static class TrieNode {
        final Map<Character, TrieNode> children = new HashMap<>();
        final Set<String> words = new TreeSet<>();
    }
}
