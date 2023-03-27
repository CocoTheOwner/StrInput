package nl.codevs.strinput.system.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CTest {

    @Test
    void removeColor() {
        assertEquals("No Colors Here", C.removeColor(C.GREEN + "No" + C.RED + " Colors " + C.YELLOW + "Here"));
        assertEquals("No Colors Here", C.removeColor(C.GREEN.toString() + C.RED + "No Colors " + C.YELLOW + "Here"));
        assertEquals("No Colors Here", C.removeColor(C.GREEN + "No" + C.RED + " Colors " + C.COLOR_CHAR + " " + C.YELLOW + "Here"));
    }
}