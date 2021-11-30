package tests;

import environment.TestCenter;
import nl.codevs.strinput.system.exception.StrNoParameterHandlerException;
import nl.codevs.strinput.system.exception.StrParseException;
import nl.codevs.strinput.system.exception.StrWhichException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHandlers {
    private static final TestCenter SUT = TestCenter.SUT;

    @Test
    public void testHandlerBoolean() {
        Boolean value = null;
        try {
            value = (Boolean) SUT.parameter.getHandler(boolean.class).parseSafe("true");
        } catch (StrNoParameterHandlerException | StrWhichException | StrParseException e) {
            e.printStackTrace();
        }
        assert value != null;
        assertEquals(true, value);
    }

    @Test
    public void testHandlerDouble() {
        Double value = null;
        try {
            value = (Double) SUT.parameter.getHandler(Double.class).parseSafe("0.1");
        } catch (StrNoParameterHandlerException | StrWhichException | StrParseException e) {
            e.printStackTrace();
        }
        assert value != null;
        assertEquals(0.1, value);
    }

    @Test
    public void testHandlerByte() {
        Byte value = null;
        try {
            value = (Byte) SUT.parameter.getHandler(Byte.class).parseSafe("10");
        } catch (StrNoParameterHandlerException | StrWhichException | StrParseException e) {
            e.printStackTrace();
        }
        assert value != null;
        assertEquals((byte) 10, value);
    }
}
