/*
 * This file is part of the Strinput distribution (https://github.com/CocoTheOwner/Strinput).
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
package tests;

import environment.TestCenter;
import environment.TestRoot;
import environment.TestUser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test {@link environment.TestRoot} commands.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class TestCommand {

    @Test
    public void testCommandInvocation() {
        TestCenter.SUT.onCommand(new ArrayList<>(List.of("test")), TestUser.SUT);
        assertEquals("You sent command: [test]", TestUser.SUT.messages.get(0));
    }

    @Test
    public void testSimpleStringAddition() {
        TestCenter.SUT.onCommand(new ArrayList<>(List.of("test", "add", "apple", "Pear")), TestUser.SUT);
        assertEquals("applePear", TestRoot.stringAddResult);
    }

    @Test
    public void testSimpleStringAdditionDefault() {
        TestCenter.SUT.onCommand(new ArrayList<>(List.of("test", "add", "apple")), TestUser.SUT);
        assertEquals("appleYeet", TestRoot.stringAddResult);
    }

    @Test
    public void testSimpleMultiplication() {
        TestCenter.SUT.onCommand(new ArrayList<>(List.of("test", "mult", "5", "6")), TestUser.SUT);
        assertEquals(30, TestRoot.multResult);
    }
}
