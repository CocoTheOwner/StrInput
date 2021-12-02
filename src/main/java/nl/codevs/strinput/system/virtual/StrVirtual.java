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
import nl.codevs.strinput.system.api.StrUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
     * @param arguments the remaining arguments
     * @param user the user that sent the command
     * @param center the command system
     * @return true if this virtual ran successfully
     */
    boolean run(List<String> arguments, StrUser user, StrCenter center);

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

    /**
     * Get whether this virtual matches an input string and user.
     * @param input the input string to match
     * @return true if the user and input string match this virtual
     */
    default boolean doesMatch(String input, StrUser user) {
        return doesMatchString(input) && doesMatchUser(user);
    }

    /**
     * Get whether this virtual matches an input string.
     * @param input the input string to match with
     * @return true if it matches the string
     */
    default boolean doesMatchString(String input) {
        // TODO: Implement matching using Lucene Apache {@link https://lucene.apache.org/core/8_11_0/demo/overview-summary.html}
        return true;
    }

    /**
     * Get whether this virtual matches an input user.
     * @param user the user to match with
     * @return true if it matches the user
     */
    default boolean doesMatchUser(StrUser user) {
        // TODO: Implement permissions check (abstract upstream of StrUser)
        // TODO: Perhaps something with sender origin (non-console sender in the usecase of spigot)
        return true;
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
