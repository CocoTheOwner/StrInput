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
package nl.codevs.strinput.examples.discord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Example implementation of a Discord user.
 * @param user the underlying Discord user
 * @param channel the channel the message was sent in. Null if not in a guild.
 * @param message the message that the user sent
 * @param guild the guild in which the message was sent. Null if not in a guild.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public record DiscordUser(
        @NotNull User user,
        @Nullable TextChannel channel,
        @NotNull Message message,
        @Nullable Guild guild
) implements StrUser {

    /**
     * Get a Discord user from an event.
     * @param event the event
     * @return the Discord User
     */
    @Contract("_ -> new")
    public static @NotNull DiscordUser of(final @NotNull MessageReceivedEvent event) {
        return new DiscordUser(
                event.getAuthor(),
                event.getTextChannel(),
                event.getMessage(),
                event.getGuild()
        );
    }

    /**
     * Get the underlying Discord user.
     * @return the underlying Discord user
     */
    public User getUser() {
        return user;
    }

    /**
     * The name of the user (something to identify them by).
     *
     * @return the name of the user
     */
    @Override
    public @NotNull String getName() {
        return user.getName();
    }

    /**
     * Send a message to the user.
     *
     * @param msg the message to send
     */
    @Override
    public void sendMessage(@NotNull final String msg) {
        assert channel() != null;
        String output = C.removeColor(msg);
        while (output.length() > 1500) {
            channel().sendMessage(output.substring(0, 1500)).queue();
            output = output.substring(1501);
        }
        channel().sendMessage(output).queue();
    }

    /**
     * @return whether this user supports clickable {@link Str}s.
     */
    @Override
    public boolean supportsClickables() {
        return false;
    }

    /**
     * Play a sound effect.
     *
     * @param sfx the sound effect type
     */
    @Override
    public void playSound(@NotNull final StrSoundEffect sfx) {

    }

    /**
     * If this sender supports context,
     * i.e. has values it stores for getting data automatically
     * (instead of specifying it in commands).
     *
     * @return true if the user supports context
     */
    @Override
    public boolean supportsContext() {
        return true;
    }

    /**
     * Whether this user has permission for a certain node or not.
     *
     * @param permission the permissions node
     * @return true if permitted.
     */
    @Override
    public boolean hasPermission(@NotNull final String permission) {
        return true;
    }
}
