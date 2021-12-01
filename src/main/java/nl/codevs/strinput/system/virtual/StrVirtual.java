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

import nl.codevs.strinput.system.api.StrCenter;
import nl.codevs.strinput.system.api.StrInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
     * Get the default virtual name (when the annotation was not given a specific name)
     * @return the name
     */
    @NotNull String getDefaultName();

    /**
     * Get the annotation on the class/method.
     * @return the annotation
     */
    @NotNull StrInput getAnnotation();

    /**
     * Run the virtual.
     * @param arguments the remaining arguments.
     * @param center the command center running this.
     * @return true if successfully ran
     */
    boolean run(List<String> arguments, StrCenter center);

    /**
     * Get category name.
     * @return the category name
     */
    @NotNull default String getName() {
        return capitalToLine(getAnnotation().name().trim().equals(StrInput.DEFAULT_NAME) ? getDefaultName() : getAnnotation().name());
    }

    /**
     * Get aliases.
     * @return the aliases
     */
    @NotNull default List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        for (String alias : getAnnotation().aliases()) {
            if (!alias.isBlank()) {
                aliases.add(alias);
            }
        }
        return aliases;
    }

    /**
     * Get the command path to this virtual.
     * @return the command path
     */
    @NotNull default String getPath() {
        return getParent() == null ? getName() : getParent().getPath() + " " + getName();
    }

    /**
     * Get names (including aliases).
     * @return the category names
     */
    @NotNull default List<String> getNames() {
        List<String> names = new ArrayList<>();
        names.add(getName());
        names.addAll(getAliases());
        return names;
    }

    // TODO: Implement matching using Lucene Apache {@link https://lucene.apache.org/core/8_11_0/demo/overview-summary.html}

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
