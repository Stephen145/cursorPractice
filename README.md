# practiceCursor

Java Maven project with a word autocomplete engine and a CLI to try it.

## Prerequisites

- Java 25 (or compatible JDK)
- [Maven](https://maven.apache.org/)

## Build

```bash
mvn compile
```

Run tests:

```bash
mvn test
```

## Autocomplete CLI

The CLI reads a prefix from stdin and prints the top word suggestions (by frequency). It uses the `WordAutocomplete` engine.

### Run with Maven

```bash
mvn exec:java
```

Pass arguments with `-Dexec.args`:

```bash
mvn exec:java -Dexec.args="--words words.txt"
mvn exec:java -Dexec.args="--words words.txt --k 5"
```

### Run with the JAR

Build the JAR, then run it:

```bash
mvn package
java -jar target/practiceCursor-1.0-SNAPSHOT.jar --words words.txt --k 5
```

### Options

| Option | Description |
|--------|-------------|
| `--words <path>` | Load initial vocabulary from a file. Optional; if omitted, the dictionary starts empty. |
| `--k <number>` | Maximum number of suggestions to return per prefix (default: 10). |

### Word file format

One entry per line. Either:

- `word` — word is added with frequency 1
- `word frequency` — word with the given frequency (integer)

Example `words.txt`:

```
apple 2
ape 5
app 3
banana 1
```

### Usage

1. Start the CLI (with or without `--words`).
2. Type a prefix and press Enter.
3. Suggestions are printed, one per line.
4. Type another prefix, or press Enter on an empty line (or Ctrl-D) to exit.

Example with a word list:

```bash
$ mvn -q exec:java -Dexec.args="--words words.txt --k 3"
ap
ape
app
apple
ba
banana

```
