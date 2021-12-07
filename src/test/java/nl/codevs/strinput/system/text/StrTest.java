package nl.codevs.strinput.system.text;

import environment.TestCenter;
import nl.codevs.strinput.system.api.StrCenter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class StrTest {

    @Test
    public void testStrSimple() {
        assertEquals("hey!", new Str("hey!").toHumanReadable());
    }

    @Test
    public void testStrAdditionText() {
        assertEquals("hey there!", new Str("hey ").a("there!").toHumanReadable());
    }

    @Test
    public void testTwoStrAddition() {
        assertEquals("hey there!", new Str("hey ").a(new Str("there!")).toHumanReadable());
    }

    @Test
    public void testStrColor() {
        assertEquals(C.B, new Str("hey", C.R).a(new Str("there!", C.B)).getMainColor());
    }

    @Test
    public void testIsGradient() {
        assertFalse(new Str("hey", C.R, C.B).a(new Str("there!", C.B)).isGradient());
    }

    @Test
    public void testIsParentGradient() {
        assertTrue(new Str("hey", C.R, C.B).a(new Str("there!", C.B)).getPrevious().isGradient());
    }

    @Test
    public void testClickable() {
        AtomicReference<Boolean> x = new AtomicReference<>(false);
        new Str("", () -> x.set(true)).getOnClick().run();
        assertTrue(x.get());
    }

    @Test
    public void testHoverable() {
        assertEquals("hoverable", new Str("text", new Str("hoverable")).getOnHover().getContent());
    }

    @Test
    public void testComplexCarryOver() {
        assertNull(new Str("text", () -> {}).resetClick().getOnClick());
    }

    @Test
    public void testComplexCarryOver2() {
        assertNull(new Str("text", () -> {}).a(new Str("text2", new Str("x"))).getPrevious().getOnHover());
    }

    @Test
    public void testComplexCarryOver3() {
        assertEquals("x", new Str("text", () -> {}).a(new Str("text2", new Str("x"))).getOnHover().getContent());
    }

    @Test
    public void testComplexModification() {
        assertEquals("test text 2", new Str("test ").a(new Str("text 2", new Str("x"))).toHumanReadable());
    }

    @Test
    public void testTextAdditionColorCarryOver() {
        assertEquals(C.B, new Str("", C.B).a("hey!").getMainColor());
    }

    @Test
    public void testStrAdditionColorNoCarryOver() {
        assertEquals(C.X, new Str("", C.B).a(new Str("hey!")).getMainColor());
    }

    @Test
    public void testComplexColorAddition() {
        assertEquals("the full text after", new Str(C.B).a("the full ").a(C.R).a("text after").toHumanReadable());
    }

    @Test
    public void testComplexColorAdditionStr() {
        assertEquals(C.B, new Str(C.B).a("the full ").a(new Str("text after", C.R)).getPrevious().getMainColor());
        assertEquals(C.R, new Str(C.B).a("the full ").a(new Str("text after", C.R)).getMainColor());
        TestCenter.SUT.debug(new Str(C.B).a("the full ").a(new Str("text after", C.R)));
    }
}