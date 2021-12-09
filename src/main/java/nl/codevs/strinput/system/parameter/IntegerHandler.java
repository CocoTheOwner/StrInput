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
package nl.codevs.strinput.system.parameter;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Integer parameter handler.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public final class IntegerHandler implements StrParameterHandler<Integer> {
    /**
     * Get all possible values for this type.<br>
     * Do not specify lists of very high length (10^6)
     *
     * @return a list of possibilities
     */
    @Override
    public List<Integer> getPossibilities() {
        return null;
    }

    /**
     * Whether this handler supports the type or not.
     *
     * @param type a type
     *
     * @return true if it supports the type
     */
    @Override
    public boolean supports(@NotNull final Class<?> type) {
        return type.equals(Integer.class) || type.equals(int.class);
    }

    /**
     * Parse a string to this type.<br>
     * You can throw:
     * <ul>
     *     <li>{@link StrWhichException}
     *     to indicate multiple options (ambiguity)</li>
     *     <li>{@link StrParseException}
     *     to indicate parsing problems</li>
     * </ul>
     * @param text the string to parse
     * @return an instance of this type parsed from the string
     * @throws Throwable when something else fails.
     * (Exceptions don't have to be caught in the parser)
     */
    @Override
    public @NotNull Integer parse(@NotNull final String text) throws Throwable {
        AtomicReference<String> r = new AtomicReference<>(text);
        return (int) Integer.valueOf(r.get()).doubleValue() * getMultiplier(r);
    }


    /**
     * Parse an instance of this type to a string.
     *
     * @param input the input string
     *
     * @return the string representation of an instance of this type
     */
    @Override
    public @NotNull String toString(@NotNull final Integer input) {
        return input.toString();
    }

    /**
     * Defaults.
     */
    private static final Integer[] DEFAULTS = new Integer[]{
            1,
            10,
            42,
            69,
            420
    };

    /**
     * Get a random default value.
     *
     * @return the random default.
     */
    @Override
    public @NotNull String getRandomDefault() {
        return DEFAULTS[RANDOM.nextInt(DEFAULTS.length)].toString();
    }
}
