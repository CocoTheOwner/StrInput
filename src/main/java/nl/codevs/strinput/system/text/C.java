/*
 * This file is part of the StrInput distribution.
 * (https://github.com/CocoTheOwner/StrInput)
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
    RED('1'),
    /**
     * Green color.
     */
    GREEN('2'),
    /**
     * Blue color.
     */
    BLUE('3'),
    /**
     * Yellow color.
     */
    YELLOW('4'),
    /**
     * Reset color.
     */
    RESET('0');

    /**
     * To String conversion
     */
    private final String toString;

    /**
     * The special character which prefixes all chat colour codes. Use this if you
     * need to dynamically convert colour codes from your custom format.
     */
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static final char COLOR_CHAR =  '\u00A7';

    /**
     * Construct a new color.
     * @param code the code of the color
     */
    C(final char code) {
        toString = new String(new char[]{COLOR_CHAR, code});
    }

    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum class should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return toString;
    }

    /**
     * Convert a string to a blank output (colorless)
     * @param string the string to remove the color of
     * @return the string without color characters
     */
    public static String removeColor(String string) {

        // Index to iterate through characters
        int i = 0;

        // Result builder
        StringBuilder result = new StringBuilder();

        while (i < string.length()) {
            if (string.charAt(i) != COLOR_CHAR) {
                result.append(string.charAt(i)); // Append non-color character to output
            } else {
                i += 1; // Skip the next character, which is used to indicate the color.
            }
            i += 1; // Go to the next character
        }

        return result.toString();
    }
}
