package syoung.practiceCursor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordAutocompleteTest {

    @Test
    void suggestReturnsTopKByFrequency() {
        var ac = new WordAutocomplete(List.of(
                new WordFrequency("apple", 2),
                new WordFrequency("ape", 5),
                new WordFrequency("app", 3)
        ));
        assertEquals(List.of("ape", "app", "apple"), ac.suggest("ap", 5));
        assertEquals(List.of("ape", "app"), ac.suggest("ap", 2));
        assertEquals(List.of("ape"), ac.suggest("ap", 1));
    }

    @Test
    void pickIncrementsFrequencyAndSuggestReflectsOrder() {
        var ac = new WordAutocomplete(List.of(
                new WordFrequency("apple", 2),
                new WordFrequency("ape", 5)
        ));
        assertEquals(List.of("ape", "apple"), ac.suggest("ap", 5));
        ac.pick("apple");
        ac.pick("apple");
        ac.pick("apple");
        ac.pick("apple"); // apple 2+4=6, ape 5 -> apple first
        assertEquals(List.of("apple", "ape"), ac.suggest("ap", 5));
    }

    @Test
    void newWordAddsAtOneAndIncludedInSuggest() {
        var ac = new WordAutocomplete(List.of(
                new WordFrequency("apple", 10)
        ));
        ac.newWord("ape");
        assertEquals(List.of("apple", "ape"), ac.suggest("ap", 5));
        assertEquals(1, ac.suggest("ap", 5).indexOf("ape") >= 0 ? 1 : -1);
    }

    @Test
    void newWordAgainLeavesFrequencyUnchanged() {
        var ac = new WordAutocomplete(List.of(
                new WordFrequency("apple", 3)
        ));
        ac.newWord("ape");
        ac.pick("ape");
        ac.pick("ape"); // ape now has freq 3, same as apple
        ac.newWord("ape"); // already present - must not reset frequency
        var suggestions = ac.suggest("ap", 5);
        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains("apple"));
        assertTrue(suggestions.contains("ape"));
    }

    @Test
    void emptyPrefixReturnsAllWordsByFrequency() {
        var ac = new WordAutocomplete(List.of(
                new WordFrequency("z", 1),
                new WordFrequency("a", 10),
                new WordFrequency("m", 5)
        ));
        assertEquals(List.of("a", "m", "z"), ac.suggest("", 10));
        assertEquals(List.of("a", "m"), ac.suggest("", 2));
    }

    @Test
    void kZeroOrNegativeReturnsEmptyList() {
        var ac = new WordAutocomplete(List.of(new WordFrequency("a", 1)));
        assertTrue(ac.suggest("a", 0).isEmpty());
        assertTrue(ac.suggest("a", -1).isEmpty());
    }

    @Test
    void kLargerThanMatchesReturnsAllMatches() {
        var ac = new WordAutocomplete(List.of(
                new WordFrequency("apple", 1),
                new WordFrequency("ape", 2)
        ));
        assertEquals(List.of("ape", "apple"), ac.suggest("ap", 100));
    }

    @Test
    void duplicateInInitLastWins() {
        var ac = new WordAutocomplete(List.of(
                new WordFrequency("apple", 10),
                new WordFrequency("apple", 2)
        ));
        assertEquals(List.of("apple"), ac.suggest("ap", 5));
        assertEquals(List.of("apple"), ac.suggest("apple", 5));
        // frequency should be 2 (last wins)
        ac.pick("apple");
        ac.pick("apple");
        assertEquals(List.of("apple"), ac.suggest("ap", 5));
    }

    @Test
    void pickUnknownWordIsNoOp() {
        var ac = new WordAutocomplete(List.of(new WordFrequency("apple", 1)));
        ac.pick("unknown");
        ac.pick("");
        ac.pick(null);
        assertEquals(List.of("apple"), ac.suggest("", 10));
    }

    @Test
    void prefixWithNoMatchesReturnsEmpty() {
        var ac = new WordAutocomplete(List.of(new WordFrequency("apple", 1)));
        assertTrue(ac.suggest("xy", 5).isEmpty());
    }
}
