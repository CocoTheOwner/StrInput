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

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import nl.codevs.strinput.system.Param;
import nl.codevs.strinput.system.StrInput;

/**
 * Example implementation of a category for Discord.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
@StrInput(name = "example", description = "example commands", aliases = "str")
public class DiscordCommands implements DiscordCommandCategory {

    /**
     * Ping the user.
     */
    @StrInput(name = "ping", description = "pong!")
    public void ping() {
        message().reply("Pong!").queue();
    }

    /**
     * Reply with a text channel.
     */
    @StrInput(name = "channel", description = "Reply with a channel")
    public void channel(
            @Param(
                    name = "channel",
                    description = "The channel to reply with",
                    contextual = true
            ) TextChannel channel
    ) {
        message().reply(channel.getAsMention()).queue();
    }

    /**
     * Reply with a user.
     */
    @StrInput(name = "user", description = "Reply with a user")
    public void user(
            @Param(
                    name = "user",
                    description = "The user to reply with",
                    contextual = true
            ) User user
    ) {
        message().reply(user.getAsMention()).queue();
    }
}
