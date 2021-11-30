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
package nl.codevs.strinput.system.virtual;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Utility functions for StrVirtual classes.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public interface StrVirtual {

    /**
     * Get the parent virtual.
     * @return the parent virtual
     */
    @Nullable StrVirtual getParent();

    /**
     * Get the primary name of the virtual.
     * @return the primary name
     */
    @NotNull String getName();

    /**
     * Get aliases.
     * @return the aliases
     */
    @NotNull List<String> getAliases();

    /**
     * Get the command path to this virtual.
     * @return the command path
     */
    @NotNull default String getPath() {
        return getParent() == null ? getName() : getParent().getPath() + " " + getName();
    }

    /**
     * Replace all capital letters in a string with a '-' and lowercase representation
     * @param string The string to convert 'IMineDiamondsForFun'
     * @return The converted string 'i-mine-diamonds-for-fun'
     */
    @NotNull default String capitalToLine(String string) {
        char[] chars = string.toCharArray();
        StringBuilder name = new StringBuilder();
        for (char aChar : chars) {
            if (Character.isUpperCase(aChar)) {
                name.append("-").append(Character.toLowerCase(aChar));
            } else {
                name.append(aChar);
            }
        }
        String result = name.toString();
        return result.startsWith("-") ? result.substring(1) : result;
    }
}
