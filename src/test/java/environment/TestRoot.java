/*
 * This file is part of the Strinput distribution.
 * (https://github.com/CocoTheOwner/Strinput)
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
package environment;

import nl.codevs.strinput.system.api.Param;
import nl.codevs.strinput.system.api.StrCategory;
import nl.codevs.strinput.system.api.StrInput;

/**
 * StrCategory test implementation.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
@StrInput(name = "test", description = "Test category")
public class TestRoot implements StrCategory {

    public static String stringAddResult;
    public static int multResult;

//    public TestRoot r = new TestRoot();

    @StrInput(name = "add", description = "Add two strings")
    public void stringAddition(
            @Param(
                    description = "First string",
                    aliases = "s1",
                    name = "string1"
            ) String stringOne,
            @Param(
                    description = "Second string",
                    defaultValue = "Yeet",
                    aliases = "s2",
                    name = "string2"
            ) String stringTwo
    ) {
        stringAddResult = stringOne + stringTwo;
    }

    @StrInput(name = "mult", description = "Multiply two integers")
    public void multiplication(
            @Param(
                    description = "The first integer",
                    aliases = "i1",
                    name = "input1"
            ) Integer i1,
            @Param(
                    description = "The second integer",
                    aliases = "i2",
                    name = "input2"
            ) Integer i2
    ) {
        multResult = i1 * i2;
    }

    @StrInput(name = "multi", description = "Multiply two integers")
    public void multiplications(
            @Param(
                    description = "The first integer",
                    aliases = "i1",
                    name = "input1"
            ) Integer i1,
            @Param(
                    description = "The second integer",
                    aliases = "i2",
                    name = "input2"
            ) Integer i2
    ) {
        multResult = i1 * i2;
    }
}
