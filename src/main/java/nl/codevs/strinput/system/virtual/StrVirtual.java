/*
 * This file is part of the Strinput distribution.
 * (https://github.com/CocoTheOwner/Strinput)
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

import nl.codevs.strinput.system.Env;
import nl.codevs.strinput.system.StrCenter;
import nl.codevs.strinput.system.StrInput;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * Get the default virtual name
     * (when the annotation was not given a specific name).
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
     * @return true if this virtual ran successfully
     */
    boolean run(List<String> arguments);

    /**
     * Send help for this virtual to a user.
     * @param user the user to send help to
     */
    void help(StrUser user);

    /**
     * Get category name.
     * @return the category name
     */
    @NotNull default String getName() {
        return capitalToLine(
                getAnnotation().name().trim().equals(StrInput.DEFAULT_NAME)
                        ? getDefaultName()
                        : getAnnotation().name()
        );
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
        return getParent() == null
                ? getName()
                : getParent().getPath() + " " + getName();
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
     * Get whether this virtual matches an input user.
     * @param user the user to match with
     * @return true if it matches the user
     */
    default boolean doesMatchUser(StrUser user) {
        if (Objects.equals(
                getAnnotation().permission(),
                StrInput.NO_PERMISSION)
        ) {
            return true;
        } else {
            return user.hasPermission(
                    getPath() + "." + getAnnotation().permission()
            );
        }
    }

    /**
     * Get the permission node for this virtual.<br>
     * All strings are passed through {@link #capitalToLine(String)}<br>
     * Built as: {@code cat1.cat2.cat3.command1}
     * @return the permission node
     */
    @NotNull default String getPermission() {
        if (getParent() != null) {
            return getParent().getPermission()
                    + "." + capitalToLine(getPermission());
        } else {
            return capitalToLine(getAnnotation().permission());
        }
    }

    /**
     * Replace all capital letters in
     * a string with a '-' and lowercase representation.
     * @param string The string to convert 'IMineDiamondsForFun'
     * @return The converted string 'i-mine-diamonds-for-fun'
     */
    @NotNull default String capitalToLine(@NotNull String string) {
        if (string.isBlank()) {
            return string;
        }
        char[] chars = string.toCharArray();
        StringBuilder name = new StringBuilder();
        name.append(Character.toLowerCase(chars[0]));
        if (chars.length == 1) {
            return name.toString();
        }
        for (int i = 1; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                name.append("-").append(Character.toLowerCase(chars[i]));
            } else {
                name.append(chars[i]);
            }
        }
        String result = name.toString();
        return result.startsWith("-") ? result.substring(1) : result;
    }

    /**
     * Send a debug message with additional information
     * about the node in its prefix.
     * @param str the {@link Str} message to send
     */
    default void debug(@NotNull Str str) {
        if (Env.settings().isDebugMatching()) {
            center().debug(new Str(getName(), C.B)
                    .a(new Str(": ", C.G))
                    .a(str.copy())
            );
        }
    }

    /**
     * @return The sender of the command, the user.
     */
    @NotNull default StrUser user() {
        return Env.UserContext.get();
    }

    /**
     * @return The command center running the system.
     */
    @NotNull default StrCenter center() {
        return Env.CenterContext.get();
    }

}
