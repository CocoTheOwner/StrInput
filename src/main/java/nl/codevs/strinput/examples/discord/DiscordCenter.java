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
package nl.codevs.strinput.examples.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import nl.codevs.strinput.examples.discord.extensions.DiscordChannelContext;
import nl.codevs.strinput.examples.discord.extensions.DiscordMemberHandler;
import nl.codevs.strinput.examples.discord.extensions.DiscordTextChannelHandler;
import nl.codevs.strinput.examples.discord.extensions.DiscordUserContext;
import nl.codevs.strinput.examples.discord.extensions.DiscordUserHandler;
import nl.codevs.strinput.system.StrCategory;
import nl.codevs.strinput.system.StrCenter;
import nl.codevs.strinput.system.StrUser;
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
     * Create a new command center.
     * @param settingsFile the directory where the settings should be stored
     * @param categories vararg root categories
     */
    public DiscordCenter(
            @NotNull final File settingsFile,
            @NotNull final StrCategory... categories
    ) {
        this(
                settingsFile,
                DEFAULT_CONSOLE_USER,
                new StrParameterHandler<?>[0],
                new StrContextHandler<?>[0],
                categories
        );
    }

    /**
     * Create a new command center.
     * @param settingsFile the directory where the settings should be stored
     * @param extraParameterHandlers extra parameter handlers on top of the default discord and java ones
     * @param extraContextHandlers extra context handlers on top of the default discord and java ones
     * @param categories vararg root categories
     */
    public DiscordCenter(
            @NotNull final File settingsFile,
            @NotNull final StrParameterHandler<?>[] extraParameterHandlers,
            @NotNull final StrContextHandler<?>[] extraContextHandlers,
            @NotNull final StrCategory... categories
    ) {
        this(
                settingsFile,
                DEFAULT_CONSOLE_USER,
                extraParameterHandlers,
                extraContextHandlers,
                categories
        );
        ParameterHandling.register(
                new DiscordTextChannelHandler(),
                new DiscordMemberHandler(),
                new DiscordUserHandler()
        );
        ContextHandling.register(
                new DiscordChannelContext(),
                new DiscordUserContext()
        );
    }

    /**
     * Create a new command center.
     * @param settingsFile the directory where the settings should be stored
     * @param extraParameterHandlers extra parameter handlers on top of the default discord and java ones
     * @param extraContextHandlers extra context handlers on top of the default discord and java ones
     * @param categories vararg root categories
     */
    public DiscordCenter(
            @NotNull final File settingsFile,
            @NotNull final StrUser console,
            @NotNull final StrParameterHandler<?>[] extraParameterHandlers,
            @NotNull final StrContextHandler<?>[] extraContextHandlers,
            @NotNull final StrCategory... categories
    ) {
        super(
                settingsFile,
                console,
                extraParameterHandlers,
                extraContextHandlers,
                categories
        );
        ParameterHandling.register(
                new DiscordTextChannelHandler(),
                new DiscordMemberHandler(),
                new DiscordUserHandler()
        );
        ContextHandling.register(
                new DiscordChannelContext(),
                new DiscordUserContext()
        );
    }

    /**
     * Run commands.<br>
     * Precondition is that the event's raw content starts with the prefix.
     * @param event the event
     * @param prefix command prefix that should be removed
     */
    public void onCommand(
            final @NotNull MessageReceivedEvent event,
            final @NotNull String prefix
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
