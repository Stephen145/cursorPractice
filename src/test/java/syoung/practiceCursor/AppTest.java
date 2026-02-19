package syoung.practiceCursor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void getMessageReturnsExpected() {
        assertEquals("Hello from practiceCursor", App.getMessage());
    }
}
