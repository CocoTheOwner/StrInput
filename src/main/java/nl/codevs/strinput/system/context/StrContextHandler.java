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

package nl.codevs.strinput.system.context;

import nl.codevs.strinput.system.api.StrUser;

/**
 * StrUserContext handler.
 *
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
    boolean supports(Class<?> type);

    /**
     * The handler for this context. Can use any data found in the user object for context derivation.
     * @param user the user whose data may be used
     * @return the value in the assigned type
     */
    T handle(StrUser user);

    /**
     * Convert this context to a string.
     *
     * @param string result of {@link #handle(StrUser)} which should be parsed to a string
     *
     * @return result of conversion
     */
    default String toString(T string) {
        return string.toString();
    }

}