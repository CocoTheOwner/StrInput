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
package tests;

import environment.TestCenter;
import environment.TestRoot;
import environment.TestUser;
import nl.codevs.strinput.system.StrInput;
import nl.codevs.strinput.system.util.C;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test {@link environment.TestRoot} commands.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class TestCommand {

    @Test
    public void testShowGraph() {
        for (String s : TestCenter.SUT.getListing(new ArrayList<>(List.of("test", "add", "string1=hey", "string2=there")))) {
            System.out.println(s);
        }
        assertTrue(true);
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
        TestCenter.SUT.onCommand(new ArrayList<>(List.of("test", "multi", "5", "6")), TestUser.SUT);
        assertEquals(30, TestRoot.multiplicationResult);
    }

    @Test
    public void testTypoMultiplication() {
        TestCenter.SUT.onCommand(new ArrayList<>(List.of("test", "multiple", "4", "6")), TestUser.SUT);
        assertEquals(24, TestRoot.multiplicationResult);
    }

    @Test
    public void testWrongCommand() {
        TestCenter.SUT.onCommand(new ArrayList<>(List.of("does-not-exist")), TestUser.SUT);
        assertEquals("Could not find root command for: does-not-exist",
                TestUser.SUT.messages.get(TestUser.SUT.messages.size() - 1));
    }

    @Test
    public void testWrongSubCommand() {
        TestCenter.SUT.onCommand(new ArrayList<>(List.of("test", "potato")), TestUser.SUT);
        assertTrue(TestUser.SUT.messages.get(TestUser.SUT.messages.size() - 1)
                .endsWith("test category had no command matching potato"));
    }

    @Test
    public void testGetHelp() {
        TestCenter.SUT.onCommand(new ArrayList<>(List.of("test")), TestUser.SUT);
        List<String> messages = TestUser.SUT.messages;
        int l = messages.size() - 4;
        assertEquals("===== test - Test category =====",
                C.removeC(messages.get(l++)));
        assertEquals("test add - Add two strings", messages.get(l++));
        assertEquals("test multiplications - Multiply two integers", messages.get(l++));
        assertEquals("test multiplication - Multiply two integers", messages.get(l));
    }
}
