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
package nl.codevs.strinput.system.virtual;

import nl.codevs.strinput.system.Param;
import nl.codevs.strinput.system.StrCategory;
import nl.codevs.strinput.system.StrInput;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Virtual command tests.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
@StrInput(name = "test", aliases = "t")
class StrVirtualCommandTest implements StrCategory {

    private static int x = 0;

    @StrInput(name = "test")
    public void testCommand(
            @Param(
                    name = "param1"
            )
            int param
    ) {
        x = param;
    }

    @Test
    public void testSetupParameters() throws NoSuchMethodException {
        assertEquals(1, new StrVirtualCommand(new StrVirtualCategory(null, this), this.getClass().getDeclaredMethod("testCommand", int.class)).getParameters().size());
    }

    @Test
    public void testInvocation() throws NoSuchMethodException {
        StrVirtualCommand test = new StrVirtualCommand(new StrVirtualCategory(null, this), this.getClass().getDeclaredMethod("testCommand", int.class));
        test.run(new ArrayList<>(List.of("1")));
        assertEquals(1, x);
    }

    @Test
    public void testGetDefaultName() throws NoSuchMethodException {
        assertEquals("testCommand", new StrVirtualCommand(null, this.getClass().getDeclaredMethod("testCommand", int.class)).getDefaultName());
    }
}