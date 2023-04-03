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
package nl.codevs.strinput.system.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Colors, C.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public enum C {
    /**
     * Red
     */
    RED('1'),
    /**
     * Green
     */
    GREEN('2'),
    /**
     * Blue
     */
    BLUE('3'),
    /**
     * Yellow
     */
    YELLOW('4'),
    /**
     * Reset
     */
    RESET('0');

    /**
     * To String conversion
     */
    private final String string;

    /**
     * Code
     */
    private final char code;

    /**
     * The special character which prefixes all chat colour codes. Use this if you
     * need to dynamically convert colour codes from your custom format.
     */
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static final char C_CHAR =  '\u00A7';

    /**
     * Construct a new C.
     * @param code the code of the C
     */
    C(final char code) {
        this.code = code;
        this.string = new String(new char[]{C_CHAR, code});
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
        return string;
    }

    /**
     * Convert a string to a blank output (C-less)
     * @param string the string to remove the C of
     * @return the string without C characters
     */
    public static @NotNull String removeC(@NotNull String string) {

        // Index to iterate through characters
        int i = 0;

        // Result builder
        StringBuilder result = new StringBuilder();

        while (i < string.length()) {
            if (string.charAt(i) != C_CHAR) {
                result.append(string.charAt(i)); // Append non-C character to output
            } else {
                i += 1; // Skip the next character, which is used to indicate the C.
            }
            i += 1; // Go to the next character
        }

        return result.toString();
    }

    /**
     * Convert a code character into a C
     * @param code the code character
     * @return the C
     */
    @Contract(pure = true)
    public static @Nullable C fromCode(final char code) {
        for (C value : C.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    /**
     * Split up a string into blocks of {@link C} and text.
     * Every element with an index that is even (e.g. the first, index 0) is a {@link C}.
     * The first element is guaranteed to be a {@link C}. If there is no {@link C} in the input there will be a {@link C#RESET}
     * @param string a string with the text to split by {@link C}.
     * @return A list of strings with a {@link C} followed by text repeatedly
     */
    public static @NotNull List<String> splitByC(@NotNull String string) {
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int i = 0;

        // Empty string
        if (string.length() == 0) {
            out.add(String.valueOf(RESET));
            out.add("");
            return out;
        }

        // Get first C
        if (string.charAt(0) != C_CHAR) {
            out.add(String.valueOf(RESET));
        } else {
            out.add(String.valueOf(C.fromCode(string.charAt(1))));
            i = 2; // Skip C character and code
        }
        // 'out' now has a C as first element, 'i' is on the index of the next character

        // Loop over rest of string
        while (i < string.length()) {

            // If there is a C char add the current
            if (string.charAt(i) == C_CHAR) {
                if (current.length() == 0) {
                    // Nothing new since last C so remove last C (duplicate C somewhere in text)
                    out.remove(out.size() - 1);
                } else {
                    // New text since last C needs to be added
                    out.add(current.toString());

                    // Reset current
                    current = new StringBuilder();
                }

                // Add new C
                out.add(String.valueOf(C.fromCode(string.charAt(i + 1))));

                // Skip an additional index due to C being 2 characters
                i++;
            } else {
                current.append(string.charAt(i));
            }
            i++;
        }

        out.add(current.toString());

        return out;
    }
}
