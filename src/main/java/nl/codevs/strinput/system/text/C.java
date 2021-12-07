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
 * Colors. Reason this is not an {@code enum} is so that it can be extended with additional colors.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class C {
    /**
     * Red color.
     */
    public static final C R = new C("red");
    /**
     * Green color.
     */
    public static final C G = new C("green");
    /**
     * Gold color.
     */
    public static final C GOLD = new C("gold");
    /**
     * Reset color.
     */
    public static final C X = new C("reset");

    /**
     * Name of the color.
     */
    private final String name;

    /**
     * Construct a new color.
     * @param colorName the name of the color
     */
    public C(String colorName) {
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
