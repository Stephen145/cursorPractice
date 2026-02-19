package syoung.practiceCursor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * CLI for WordAutocomplete: optional --words &lt;path&gt; and --k &lt;number&gt;,
 * then interactive loop: type prefix, get top-k suggestions; blank line or EOF exits.
 */
public class AutocompleteCli {

    private static final int DEFAULT_MAX_SUGGESTIONS = 10;
    private static final int MIN_SUGGESTIONS = 1;
    private static final int DEFAULT_WORD_FREQUENCY = 1;
    private static final int MIN_WORD_FREQUENCY = 1;

    public static void main(String[] args) {
        String wordsPath = null;
        int maxSuggestions = DEFAULT_MAX_SUGGESTIONS;

        for (int i = 0; i < args.length; i++) {
            if ("--words".equals(args[i]) && i + 1 < args.length) {
                wordsPath = args[++i];
            } else if ("--k".equals(args[i]) && i + 1 < args.length) {
                try {
                    maxSuggestions = Math.max(MIN_SUGGESTIONS, Integer.parseInt(args[++i]));
                } catch (NumberFormatException e) {
                    maxSuggestions = DEFAULT_MAX_SUGGESTIONS;
                    System.err.println("Invalid --k, using " + DEFAULT_MAX_SUGGESTIONS);
                }
            }
        }

        List<WordFrequency> initial = new ArrayList<>();
        if (wordsPath != null) {
            try {
                loadWords(Path.of(wordsPath), initial);
            } catch (IOException e) {
                System.err.println("Failed to load words: " + e.getMessage());
                System.exit(1);
            }
        }

        WordAutocomplete autocomplete = new WordAutocomplete(initial);
        System.out.println("Autocomplete initialized with " + initial.size() + " words");

        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    break;
                }
                for (String word : autocomplete.suggest(line, maxSuggestions)) {
                    System.out.println(word);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void loadWords(Path path, List<WordFrequency> out) throws IOException {
        for (String line : Files.readAllLines(path)) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+", 2);
            String word = parts[0];
            int frequency = DEFAULT_WORD_FREQUENCY;
            if (parts.length > 1) {
                try {
                    frequency = Math.max(MIN_WORD_FREQUENCY, Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException ignored) {
                }
            }
            out.add(new WordFrequency(word, frequency));
        }
    }
}
