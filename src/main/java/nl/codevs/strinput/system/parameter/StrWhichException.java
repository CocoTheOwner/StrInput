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
package nl.codevs.strinput.system.parameter;

import java.util.List;

/**
 * Thrown when more than one option is available for a singular mapping<br>
 * Like having a hashmap where one input maps to two outputs.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrWhichException extends Exception {

    private final List<?> options;

    public List<?> getOptions() {
        return options;
    }

    public StrWhichException(Class<?> type, String input, List<?> options) {
        super("Cannot parse \"" + input + "\" into type " + type.getSimpleName() + " because of multiple options");
        this.options = options;
    }
}
