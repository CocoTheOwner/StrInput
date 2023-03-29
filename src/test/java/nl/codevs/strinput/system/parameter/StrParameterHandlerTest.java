/*
 * This file is part of the StrInput distribution.
 * (https://github.com/CocoTheOwner/StrInput)
 * Copyright (c) 2021 Sjoerd van de Goor.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package nl.codevs.strinput.system.parameter;

import environment.TestCenter;
import nl.codevs.strinput.system.Context;
import nl.codevs.strinput.system.StrCenter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Handler test cases.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrParameterHandlerTest {

    @BeforeAll
    public static void setup() {
        if (!Context.registered()) {
            Context.touch(new TestCenter());
        }
    }

    @Test
    public void testHandlerBoolean() {
        Boolean value = null;
        try {
            value = (Boolean) StrCenter.ParameterHandling.getHandler(Boolean.class).parseSafe("true");
        } catch (StrCenter.ParameterHandling.StrNoParameterHandlerException | StrParameterHandler.StrWhichException | StrParameterHandler.StrParseException e) {
            e.printStackTrace();
        }
        assert value != null;
        assertEquals(true, value);
    }

    @Test
    public void testHandlerDouble() {
        Double value = null;
        try {
            value = (Double) StrCenter.ParameterHandling.getHandler(Double.class).parseSafe("0.1");
        } catch (StrCenter.ParameterHandling.StrNoParameterHandlerException | StrParameterHandler.StrWhichException | StrParameterHandler.StrParseException e) {
            e.printStackTrace();
        }
        assert value != null;
        assertEquals(0.1, value);
    }

    @Test
    public void testHandlerByte() {
        Byte value = null;
        try {
            value = (Byte) StrCenter.ParameterHandling.getHandler(Byte.class).parseSafe("10");
        } catch (StrCenter.ParameterHandling.StrNoParameterHandlerException | StrParameterHandler.StrWhichException | StrParameterHandler.StrParseException e) {
            e.printStackTrace();
        }
        assert value != null;
        assertEquals((byte) 10, value);
    }
}
