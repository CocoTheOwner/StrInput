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

package nl.codevs.strinput.system.parameters;

import nl.codevs.strinput.system.exceptions.StrParseException;
import nl.codevs.strinput.system.exceptions.StrWhichException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * TODO:Description
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class BooleanHandler implements StrParameterHandler<Boolean> {
    /**
     * Get all possible values for this type.
     *
     * @return a list of possibilities
     */
    @Override
    public List<Boolean> getPossibilities() {
        return Arrays.asList(true, false);
    }

    /**
     * Whether this handler supports the type or not.
     *
     * @param type a type
     *
     * @return true if it supports the type
     */
    @Override
    public boolean supports(@NotNull Class<?> type) {
        return type.equals(Boolean.class) || type.equals(boolean.class);
    }

    /**
     * Parse a string to this type.
     *
     * @param text  the string to parse
     *
     * @return an instance of this type parsed from the string
     */
    @Override
    public @NotNull Boolean parse(@NotNull String text) throws StrParseException, StrWhichException {
        text = text.toLowerCase(Locale.ROOT);
        for (String fls : FALSES) {
            if (text.equals(fls)) {
                return false;
            }
        }
        for (String fls : TRUES) {
            if (text.equals(fls)) {
                return true;
            }
        }
        throw new StrParseException(Boolean.class, text, "Cannot map to true or false");
    }

    public static final String[] TRUES = new String[]{
            "true",
            "t",
            "yes",
            "+"
    };

    /**
     * False options
     */
    public static final String[] FALSES = new String[]{
            "false",
            "f",
            "no",
            "-"
    };

    /**
     * Get a random default value.
     *
     * @return the random default
     */
    @Override
    public @NotNull String getRandomDefault() {
        return new Random().nextBoolean() ? "true" : "false";
    }
}
