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

package nl.codevs.strinput.system;

import nl.codevs.strinput.system.contexts.StrContextHandler;
import nl.codevs.strinput.system.parameters.*;
import nl.codevs.strinput.system.text.Str;

import java.util.ArrayList;
import java.util.List;

/**
 * Input center. The main class for interacting with Strinput.

 * @see nl.codevs.strinput.examples
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public abstract class StrCenter {
    final String prefix;
    final StrCommand[] commands;
    final StrUser console;

    /**
     * Create a new command center.
     * @param systemPrefix command prefix that can be passed with your commands (and should be ignored)
     * @param rootCommands array of root commands (usually only 1, your main command)
     * @param consoleUser the console ({@link StrUser})
     */
    public StrCenter(
            final String systemPrefix,
            final StrCommand[] rootCommands,
            final StrUser consoleUser
    ) {
        prefix = systemPrefix;
        commands = rootCommands;
        console = consoleUser;
    }

    /**
     * Parameter handlers registry.
     */
    public static final List<StrParameterHandler<?>> PARAMETER_HANDLERS = new ArrayList<>();

    /**
     * Add a parameter handler.
     * @param handler the handler to add
     */
    public static void addParameterHandler(StrParameterHandler<?> handler) {
        PARAMETER_HANDLERS.add(handler);
    }

    static {
        addParameterHandler(new BooleanHandler());
        addParameterHandler(new ByteHandler());
        addParameterHandler(new DoubleHandler());
        addParameterHandler(new FloatHandler());
        addParameterHandler(new IntegerHandler());
        addParameterHandler(new LongHandler());
        addParameterHandler(new ShortHandler());
        addParameterHandler(new StringHandler());
    }

    /**
     * StrUserContext handlers registry
     */
    public static final List<StrContextHandler<?>> CONTEXT_HANDLERS = new ArrayList<>();

    /**
     * Add a parameter handler.
     * @param handler the handler to add
     */
    public static void addContextHandler(StrContextHandler<?> handler) {
        CONTEXT_HANDLERS.add(handler);
    }

    /**
     * Command receiver.
     * @param command the command to parse
     * @param user the user that sent the command
     */
    public void onCommand(String command, StrUser user) {
        user.sendMessage(new Str("You sent command: ", command));
    }

    /**
     * Send a debug message.
     * @param messages the debug message(s)
     */
    public void debug(Str... messages) {
        if (messages.length == 1) {
            console.sendMessage(messages[0]);
        } else {
            console.sendMessage(messages);
        }
    }

}
