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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Message;
import nl.codevs.strinput.system.StrCategory;

/**
 * Discord command category with auxiliary commands.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public interface DiscordCategory extends StrCategory {

    /**
     * @return The player instance of the user (null if not a player).
     */
    default User discordUser() {
        return ((DiscordUser) user()).getUser();
    }

    /**
     * @return The channel of the message (null if not in a guild).
     */
    default MessageChannel channel() {
        if (((DiscordUser) user()).channel() == null) {
            return discordUser().openPrivateChannel().complete();
        } else {
            return ((DiscordUser) user()).channel();
        }
    }

    /**
     * @return The guild in which the message was sent (null if not in a guid).
     */
    default Guild guild() {
        return ((DiscordUser) user()).guild();
    }

    /**
     * @return The message that was sent.
     */
    default Message message() {
        return ((DiscordUser) user()).message();
    }
}
