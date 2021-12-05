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

package nl.codevs.strinput.examples.spigotmc.extensions;

import nl.codevs.strinput.system.parameter.StrParseException;
import nl.codevs.strinput.system.parameter.StrWhichException;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Spigot user implementation
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class SpigotPlayerHandler implements StrParameterHandler<Player> {
    /**
     * Get all possible values for this type.<br>
     * Do not specify lists of very high length (10^6)
     *
     * @return a list of possibilities
     */
    @Override
    public List<Player> getPossibilities() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    /**
     * Whether this handler supports the type or not.
     *
     * @param type a type
     * @return true if it supports the type
     */
    @Override
    public boolean supports(@NotNull Class<?> type) {
        return type.equals(Player.class);
    }

    /**
     * Parse a string to this type.<br>
     * You can throw:
     * <ul>
     *     <li>{@link StrWhichException} to indicate multiple options (ambiguity)</li>
     *     <li>{@link StrParseException} to indicate parsing problems</li>
     * </ul>
     *
     * @param text the string to parse
     * @return an instance of this type parsed from the string
     * @throws Throwable when something else fails. (Exceptions don't have to be caught in the parser)
     */
    @Override
    public @NotNull Player parse(@NotNull String text) throws Throwable {
        List<Player> options = getPossibilities(text);
        if (options.size() == 0) {
            throw new StrParseException(Player.class, text, "No options found for input");
        } else if (options.size() == 1) {
            return options.get(0);
        } else {
            throw new StrWhichException(Player.class, text, options);
        }
    }

    /**
     * Get a random default value.
     *
     * @return the random default
     */
    @Override
    public @NotNull String getRandomDefault() {
        return getPossibilities().get(RANDOM.nextInt(getPossibilities().size())).getName();
    }

    /**
     * Get all possible values for this type, filtered with some input string.<br>
     *
     * @param input the input string to filter by
     * @return a list of possibilities
     */
    @Override
    public List<Player> getPossibilities(String input) {
        final String i = input.toLowerCase(Locale.ROOT);
        return getPossibilities().stream().filter(player -> player.getName().toLowerCase(Locale.ROOT).startsWith(i)).collect(Collectors.toList());
    }
}
