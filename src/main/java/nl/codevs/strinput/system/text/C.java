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
package nl.codevs.strinput.system.text;

/**
 * Colors.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public enum C {
    /**
     * Red color.
     */
    R("red"),
    /**
     * Green color.
     */
    G("green"),
    /**
     * Blue color.
     */
    B("blue"),
    /**
     * Reset color.
     */
    X("reset");

    /**
     * Name of the color.
     */
    private final String name;

    /**
     * Construct a new color.
     * @param colorName the name of the color
     */
    C(String colorName) {
        name = colorName;
    }

    /**
     * Get the color name.
     * @return the color name
     */
    public String getName() {
        return name;
    }
}
