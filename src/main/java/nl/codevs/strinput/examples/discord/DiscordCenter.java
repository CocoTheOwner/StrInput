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

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nl.codevs.strinput.system.StrCenter;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class DiscordCenter extends StrCenter {
    /**
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     */
    public DiscordCenter() {
        super(
                new File("settings"),
                DEFAULT_CONSOLE_USER,
                new StrParameterHandler[]{

                },
                new StrContextHandler[]{

                },
                new DiscordCommands()
        );
    }

    /**
     * Run a function sync (on the main thread, when needed).
     *
     * @param runnable the runnable to run
     */
    @Override
    public void runSync(@NotNull Runnable runnable) {
        runnable.run();
    }

    /**
     * Run commands.
     * @param event the event.
     */
    public void onCommand(MessageReceivedEvent event) {
        DiscordUser user = DiscordUser.of(event);
        user.channel().sendTyping().queue();
        super.onCommand(List.of(event.getMessage().getContentRaw().split(" ")), user);
    }
}
