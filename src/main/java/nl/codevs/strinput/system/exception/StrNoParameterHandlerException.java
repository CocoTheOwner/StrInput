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

package nl.codevs.strinput.system.exception;

/**
 * Exception thrown when no parameter handler could be found.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrNoParameterHandlerException extends Exception {
    public StrNoParameterHandlerException(Class<?> type) {
        super("Could not find parameter handler for: " + type.getSimpleName());
    }
}
