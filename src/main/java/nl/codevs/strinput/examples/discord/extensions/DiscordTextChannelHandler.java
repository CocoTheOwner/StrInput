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
package nl.codevs.strinput.examples.discord.extensions;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.codevs.strinput.examples.discord.DiscordUser;
import nl.codevs.strinput.system.Env;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Parameter handler for {@link TextChannel}s.
 *
 * @author Sjoerd van de Goor
 * @since v0.2
 */
public class DiscordTextChannelHandler
        implements StrParameterHandler<TextChannel> {
    /**
     * Get all possible values for this type.<br>
     * Do not specify lists of very high length (10^6)
     *
     * @return a list of possibilities
     */
    @Override
    public List<TextChannel> getPossibilities() {
        Guild guild = ((DiscordUser) Env.user()).guild();
        if (guild == null) {
            return null;
        }
        return guild.getTextChannels();
    }

    /**
     * Whether this handler supports the type or not.
     *
     * @param type a type
     * @return true if it supports the type
     */
    @Override
    public boolean supports(@NotNull final Class<?> type) {
        return type.isAssignableFrom(TextChannel.class);
    }

    /**
     * Parse a string to this type.<br>
     * You can throw:
     * <ul>
     *     <li>{@link StrWhichException}
     *     to indicate multiple options (ambiguity)</li>
     *     <li>{@link StrParseException}
     *     to indicate parsing problems</li>
     * </ul>
     *
     * @param text the string to parse
     * @return an instance of this type parsed from the string
     * @throws Throwable when something else fails.
     *                   (Exceptions don't have to be caught in the parser)
     */
    @Override
    public @NotNull TextChannel parse(
            @NotNull final String text
    ) throws Throwable {
        List<TextChannel> options = getPossibilities(text);

        if (options.size() == 0) {
            throw new StrParseException(
                    DiscordTextChannelHandler.class,
                    text,
                    "No options for input"
            );
        }

        if (options.size() > 1) {
            List<TextChannel> filteredOptions = options.stream().filter(
                    o -> o.getName().equals(text)
            ).toList();
            if (filteredOptions.size() == 1) {
                return filteredOptions.get(0);
            } else if (filteredOptions.size() > 1) {
                throw new StrWhichException(
                        DiscordTextChannelHandler.class,
                        text,
                        filteredOptions
                );
            } else {
                throw new StrWhichException(
                        DiscordTextChannelHandler.class,
                        text,
                        options
                );
            }
        }

        return options.get(0);
    }

    /**
     * Get a random default value.
     *
     * @return the random default
     */
    @Override
    public @NotNull String getRandomDefault() {
        return getPossibilities().get(
                new Random().nextInt(getPossibilities().size() - 1)
        ).getAsMention();
    }

    /**
     * Get all possible values for this type,
     * filtered with some input string.<br>
     *
     * @param input the input string to filter by
     * @return a list of possibilities
     */
    @Override
    public @NotNull List<TextChannel> getPossibilities(
            @NotNull final String input
    ) {
        return getPossibilities().stream().filter(
                c -> c.getName().contains(input)
        ).collect(Collectors.toList());
    }
}
