package nl.codevs.strinput.system.util;

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
        assertEquals(new ArrayList<>(List.of("" + C.BLUE, "B", "" + C.RED, "R")),
                C.splitByC("" + C.BLUE + "B" + C.RED + "R"));
    }
}