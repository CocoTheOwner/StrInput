package nl.codevs.strinput.system.util;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CTest {

    @Test
    void removeC() {
        assertEquals("No Colors Here", C.removeC(C.GREEN + "No" + C.RED + " Colors " + C.YELLOW + "Here"));
        assertEquals("No Colors Here", C.removeC(C.GREEN.toString() + C.RED + "No Colors " + C.YELLOW + "Here"));
        assertEquals("No Colors Here", C.removeC(C.GREEN + "No" + C.RED + " Colors " + C.C_CHAR + " " + C.YELLOW + "Here"));
    }

    @Test
    void fromCode() {
        assertEquals(C.BLUE, C.fromCode('3'));
        assertEquals(C.RESET, C.fromCode('0'));
        assertEquals(C.RED, C.fromCode('1'));
    }

    @Test
    void splitByC() {
        assertStringListEqual(new ArrayList<>(List.of(String.valueOf(C.BLUE), "B", String.valueOf(C.RED), "R")),
                C.splitByC(C.BLUE + "B" + C.RED + "R"));
        assertStringListEqual(new ArrayList<>(List.of(String.valueOf(C.RESET), "")), C.splitByC(""));
        assertStringListEqual(new ArrayList<>(List.of(String.valueOf(C.RED), "ok")), C.splitByC(String.valueOf(C.BLUE) + C.BLUE + C.BLUE + C.RED + "ok"));
    }

    /**
     * Check whether two lists are equal.
     * @param expected the expected values
     * @param actual the actual values
     */
    void assertStringListEqual(@NotNull List<String> expected, @NotNull List<String> actual) {
        for (int i = 0; i < expected.size(); i++) {
            if (!actual.get(i).equals(expected.get(i))) {
                fail("index (" + i + ") expected: " + expected.get(i) + " but was: " + actual.get(i));
            }
        }
    }
}