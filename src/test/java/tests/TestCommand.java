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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        tc("test", "add", "apple", "Pear");
        assertEquals("applePear", TestRoot.stringAddResult);
    }

    @Test
    public void testSimpleStringAdditionDefault() {
        tc("test", "add", "apple");
        assertEquals("appleYeet", TestRoot.stringAddResult);
    }

    @Test
    public void testSimpleMultiplication() {
        tc("test", "multi", "5", "6");
        assertEquals(30, TestRoot.multiplicationResult);
    }

    @Test
    public void testTypoMultiplication() {
        tc("test", "multiple", "4", "6");
        assertEquals(24, TestRoot.multiplicationResult);
    }

    @Test
    public void testWrongCommand() {
        tc("does-not-exist");
        assertEquals("Could not find root command for: does-not-exist",
                TestUser.SUT.messages.get(TestUser.SUT.messages.size() - 1));
    }

    @Test
    public void testWrongSubCommand() {
        tc("test", "potato");
        assertTrue(TestUser.SUT.messages.get(TestUser.SUT.messages.size() - 1)
                .endsWith("test category had no command matching potato"));
    }

    @Test
    public void testGetHelp() {
        tc("test");
        List.of(
                "===== test - Test category =====",
                "test add (Add two strings)",
                "test multiplications (Multiply two integers)",
                "test multiplication (Multiply two integers)"
        ).forEach(m -> {
            for (String message : TestUser.SUT.messages) {
                System.out.println("Comparing '" + m + "' with '" + message + "': " + message.startsWith(m));
                if (message.startsWith(m)) {
                    return;
                }
            }
            fail("Logs above are output, but this message was not found: " + m);
        });
    }

    @Test
    public void testComplicatedMultiplicationSimple() {
        tc("test", "compmut", "4");
        assertEquals(4, TestRoot.multiplicationResult);
    }

    @Test
    public void testComplicatedMultiplicationFull() {
        tc("test", "compmut", "3", "2", "2");
        assertEquals(3, TestRoot.multiplicationResult);
    }

    @Test
    public void testComplicatedMultiplicationFlags() {
        tc("test", "compmut", "3", "2", "2", "-1", "-2", "in3on=false");
        assertEquals(6, TestRoot.multiplicationResult);
    }

    /**
     * Test a command
     * @param input the input command
     */
    private void tc(String... input) {
        TestCenter.SUT.onCommand(new ArrayList<>(List.of(input)), TestUser.SUT);
    }
}
