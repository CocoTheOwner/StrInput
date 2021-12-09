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
package nl.codevs.strinput.examples.discord.extensions;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.codevs.strinput.examples.discord.DiscordUser;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.context.StrContextHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Context handler for {@link MessageChannel}s.
 * @author Sjoerd van de Goor
 * @since v0.2
 */
public class DiscordChannelContext implements StrContextHandler<MessageChannel> {
    /**
     * The type this context handler handles.
     *
     * @param type The type to check for support
     * @return the type
     */
    @Override
    public boolean supports(@NotNull Class<?> type) {
        return MessageChannel.class.isAssignableFrom(type);
    }

    /**
     * The handler for this context.<br>
     * Can use any data found in the user object for context derivation.<br>
     * More data can be added to the user by:
     * <ol>
     *     <li>Adding fields to the user implementation, and then</li>
     *     <li>Casting this {@code user} to the new type.</li>
     * </ol>
     *
     * @param user the user whose data may be used (can be casted)
     * @return the value in the assigned type
     */
    @Override
    public @Nullable TextChannel handle(@NotNull StrUser user) {
        return ((DiscordUser) user).channel();
    }
}
