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
import nl.codevs.strinput.system.exceptions.StrNoParameterHandlerException;
import nl.codevs.strinput.system.parameters.*;
import nl.codevs.strinput.system.text.Str;

import java.util.ArrayList;
import java.util.List;

/**
 * Input center. The main class for interacting with Strinput.<br>
 * Make sure to point command calls to {@link #onCommand(List, StrUser)}

 * @see nl.codevs.strinput.examples.spigotmc
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public abstract class StrCenter {
    final StrCategory[] commands;
    final StrUser console;

    /**
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     * @param consoleUser the console ({@link StrUser})
     * @param parameterHandlers additional parameter handlers
     * @param contextHandlers additional context handlers
     * @param rootCommands array of root commands (usually only 1, your main command)
     */
    public StrCenter(
            final StrUser consoleUser,
            final StrParameterHandler<?>[] parameterHandlers,
            final StrContextHandler<?>[] contextHandlers,
            final StrCategory... rootCommands
    ) {
        commands = rootCommands;
        console = consoleUser;

        for (StrParameterHandler<?> parameterHandler : parameterHandlers) {
            addParameterHandler(parameterHandler);
        }

        for (StrContextHandler<?> contextHandler : contextHandlers) {
            addContextHandler(contextHandler);
        }
    }

    /**
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     * @param rootCommands array of root commands (usually only 1, your main command)
     * @param consoleUser the console ({@link StrUser})
     */
    public StrCenter(
            final StrCategory[] rootCommands,
            final StrUser consoleUser
    ) {
        this(
                consoleUser,
                new StrParameterHandler<?>[0],
                new StrContextHandler<?>[0],
                rootCommands
        );
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

    /**
     * Get handler for a type.
     * @param type the type to get the handler for
     * @return the parameter handler for the type
     * @throws StrNoParameterHandlerException if no parameter handler could be found
     */
    public static StrParameterHandler<?> getHandler(Class<?> type) throws StrNoParameterHandlerException {
        for (StrParameterHandler<?> parameterHandler : PARAMETER_HANDLERS) {
            if (parameterHandler.supports(type)) {
                return parameterHandler;
            }
        }
        throw new StrNoParameterHandlerException(type);
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
     *
     * @return true if successful
     */
    public boolean onCommand(List<String> command, StrUser user) {
        user.sendMessage(new Str("You sent command: ", command.toString()));
        return true;
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

    /**
     * Debug a string.
     * @param message the debug string
     */
    public void debug(String message) {
        debug(new Str(message));
    }
}
