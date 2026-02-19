package syoung.practiceCursor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutocompleteCliTest {

    private final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    private final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private PrintStream originalErr;
    private java.io.InputStream originalIn;

    @BeforeEach
    void captureStreams() {
        originalOut = System.out;
        originalErr = System.err;
        originalIn = System.in;
        System.setOut(new PrintStream(stdout));
        System.setErr(new PrintStream(stderr));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
    }

    private void setStdin(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    private String stdout() {
        return stdout.toString(StandardCharsets.UTF_8);
    }

    private String stderr() {
        return stderr.toString(StandardCharsets.UTF_8);
    }

    @Test
    void noArgs_emptyStdin_exitsWithNoOutput() {
        setStdin("\n");
        AutocompleteCli.main(new String[0]);
        assertEquals("", stdout());
        assertEquals("", stderr());
    }

    @Test
    void noArgs_prefixWithEmptyDictionary_printsNothing() {
        setStdin("ap\n\n");
        AutocompleteCli.main(new String[0]);
        assertEquals("", stdout());
    }

    @Test
    void withWordsFile_printsSuggestionsForPrefix(@TempDir Path tempDir) throws IOException {
        Path wordsFile = tempDir.resolve("words.txt");
        Files.writeString(wordsFile, "apple 2\nape 5\napp 3\n");

        setStdin("ap\n\n");
        AutocompleteCli.main(new String[] { "--words", wordsFile.toString() });

        assertEquals("ape\napp\napple\n", stdout());
    }

    @Test
    void withK_limitsNumberOfSuggestions(@TempDir Path tempDir) throws IOException {
        Path wordsFile = tempDir.resolve("words.txt");
        Files.writeString(wordsFile, "apple 2\nape 5\napp 3\n");

        setStdin("ap\n\n");
        AutocompleteCli.main(new String[] { "--words", wordsFile.toString(), "--k", "2" });

        assertEquals("ape\napp\n", stdout());
    }

    @Test
    void invalidK_printsErrorAndUsesDefault(@TempDir Path tempDir) throws IOException {
        Path wordsFile = tempDir.resolve("words.txt");
        Files.writeString(wordsFile, "apple 2\nape 5\napp 3\n");

        setStdin("ap\n\n");
        AutocompleteCli.main(new String[] { "--words", wordsFile.toString(), "--k", "notanumber" });

        assertTrue(stderr().contains("Invalid --k, using 10"), "stderr: " + stderr());
        assertEquals("ape\napp\napple\n", stdout());
    }

    @Test
    void multiplePrefixes_eachGetsSuggestions(@TempDir Path tempDir) throws IOException {
        Path wordsFile = tempDir.resolve("words.txt");
        Files.writeString(wordsFile, "apple 2\nape 5\nbanana 1\n");

        setStdin("ap\nba\n\n");
        AutocompleteCli.main(new String[] { "--words", wordsFile.toString() });

        assertEquals("ape\napple\nbanana\n", stdout());
    }

    @Test
    void wordsFileWithDefaultFrequency_loadsWord(@TempDir Path tempDir) throws IOException {
        Path wordsFile = tempDir.resolve("words.txt");
        Files.writeString(wordsFile, "foo\n");

        setStdin("fo\n\n");
        AutocompleteCli.main(new String[] { "--words", wordsFile.toString() });

        assertEquals("foo\n", stdout());
    }
}
