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
package nl.codevs.strinput.system.context;

import nl.codevs.strinput.system.api.StrUser;
import org.jetbrains.annotations.NotNull;

/**
 * StrUserContext handler.<br>
 * These handlers make use of a provided {@link StrUser} to deduct a value.
 *
 * @param <T> the type to handle in context.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public interface StrContextHandler<T> {

    /**
     * The type this context handler handles.
     *
     * @param type The type to check for support
     *
     * @return the type
     */
    boolean supports(@NotNull final Class<?> type);

    /**
     * The handler for this context.<br>
     * Can use any data found in the user object for context derivation.<br>
     * More data can be added to the user by:
     * <ol>
     *     <li>Adding fields to the user implementation, and then</li>
     *     <li>Casting this {@code user} to the new type.</li>
     * </ol>
     * @param user the user whose data may be used (can be casted)
     * @return the value in the assigned type
     */
    @NotNull T handle(@NotNull final StrUser user);

    /**
     * Convert this context to a string.
     *
     * @param string result of {@link #handle(StrUser)},
     *              which should be parsed to a string
     *
     * @return result of conversion
     */
    @NotNull default String toString(@NotNull final T string) {
        return string.toString();
    }
}
