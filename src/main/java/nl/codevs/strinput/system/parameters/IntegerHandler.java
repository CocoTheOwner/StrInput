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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Integer parameter handler.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class IntegerHandler implements StrParameterHandler<Integer> {
    /**
     * Get all possible values for this type.
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
    public boolean supports(@NotNull Class<?> type) {
        return type.equals(Integer.class) || type.equals(int.class);
    }

    /**
     * Parse a string to this type.
     *
     * @param text  the string to parse
     *
     * @return an instance of this type parsed from the string
     */
    @Override
    public @NotNull Integer parse(@NotNull String text) throws StrParseException, StrWhichException {
        try {
            AtomicReference<String> r = new AtomicReference<>(text);
            double m = getMultiplier(r);
            return (int) (Integer.valueOf(r.get()).doubleValue() * m);
        } catch (Throwable e) {
            throw new StrParseException(Integer.class, text, e);
        }
    }

    /**
     * Parse an instance of this type to a string.
     *
     * @param input the input string
     *
     * @return the string representation of an instance of this type
     */
    @Override
    public @NotNull String toString(@NotNull Integer input) {
        return input.toString();
    }

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
        return DEFAULTS[new Random().nextInt(0, DEFAULTS.length)].toString();
    }
}
