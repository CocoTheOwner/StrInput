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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nl.codevs.strinput.examples.discord.extensions.DiscordChannelContext;
import nl.codevs.strinput.examples.discord.extensions.DiscordMemberHandler;
import nl.codevs.strinput.examples.discord.extensions.DiscordTextChannelHandler;
import nl.codevs.strinput.examples.discord.extensions.DiscordUserContext;
import nl.codevs.strinput.examples.discord.extensions.DiscordUserHandler;
import nl.codevs.strinput.system.StrCenter;
import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link StrCenter} for Discord.
 *
 * @author Sjoerd van de Goor
 * @since v0.2
 */
public class DiscordCenter extends StrCenter {

    /**
     * JDA.
     */
    private final JDA jda;


    /**
     * Create a new command center.
     * @param discordJDA the JDA created for this center
     */
    public DiscordCenter(final JDA discordJDA) {
        super(
                new File("settings"),
                DEFAULT_CONSOLE_USER,
                new StrParameterHandler[]{
                        new DiscordTextChannelHandler(),
                        new DiscordMemberHandler(),
                        new DiscordUserHandler()
                },
                new StrContextHandler[]{
                        new DiscordChannelContext(),
                        new DiscordUserContext()
                },
                new DiscordCommands()
        );
        this.jda = discordJDA;
    }

    /**
     * Run a function sync (on the main thread, when needed).
     *
     * @param runnable the runnable to run
     */
    @Override
    public void runSync(@NotNull final Runnable runnable) {
        runnable.run();
    }

    /**
     * Run commands.<br>
     * Precondition is that the event's raw content starts with the prefix.
     * @param event the event
     * @param prefix command prefix that should be removed
     */
    public void onCommand(
            final MessageReceivedEvent event,
            final String prefix
    ) {
        DiscordUser user = DiscordUser.of(event);
        assert user.channel() != null;
        user.channel().sendTyping().complete();
        ArrayList<String> args = new ArrayList<>(List.of(
                event.getMessage().getContentRaw().split(" ")
        ));
        args.set(0, args.get(0).substring(prefix.length()));
        super.onCommand(args, user);
    }
}
