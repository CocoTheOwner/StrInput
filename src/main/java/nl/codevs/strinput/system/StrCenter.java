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

import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.*;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import org.jetbrains.annotations.NotNull;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.util.List;

/**
 * Input center. The main class for interacting with Strinput.<br>
 * Make sure to point command calls to {@link #onCommand(List, StrUser)}

 * @see nl.codevs.strinput.examples.spigotmc
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public abstract class StrCenter {
    public static StrSettings settings;
    final StrUser console;
    final StrRoots commandMap;
    public final StrParameter parameter;
    public final StrContext context;

    /**
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     * @param settingsFolder the settings folder for this system (settings file stored as {@code strconfig.json})
     * @param consoleUser the console ({@link StrUser})
     * @param parameterHandlers additional parameter handlers
     * @param contextHandlers additional context handlers
     * @param enableSettingsCommands if set to true, enables commands for the system's settings
     * @param rootCommands array of root commands (usually only 1, your main command)
     *
     * @throws InstanceAlreadyExistsException when this command system is already running
     */
    public StrCenter(
            @NotNull final File settingsFolder,
            @NotNull final StrUser consoleUser,
            @NotNull final StrParameterHandler<?>[] parameterHandlers,
            @NotNull final StrContextHandler<?>[] contextHandlers,
                     final boolean enableSettingsCommands,
            @NotNull final StrCategory... rootCommands
    ) throws InstanceAlreadyExistsException {
        if (settings != null) {
            throw new InstanceAlreadyExistsException();
        }

        // Fields
        settings = StrSettings.fromConfigJson(new File(settingsFolder.getAbsolutePath() + "/strsettings.json"), consoleUser);
        console = consoleUser;

        // Handlers
        parameter = new StrParameter(parameterHandlers);
        parameter.addAll(List.of(
                new BooleanHandler(),
                new ByteHandler(),
                new DoubleHandler(),
                new FloatHandler(),
                new IntegerHandler(),
                new LongHandler(),
                new ShortHandler(),
                new StringHandler()
        ));
        context = new StrContext(contextHandlers);

        // Command map (roots)
        commandMap = new StrRoots(enableSettingsCommands, rootCommands, this);
    }

    /**
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     * @param settingsFolder the settings folder for this system (settings file stored as {@code strconfig.json})
     * @param consoleUser the console ({@link StrUser})
     * @param rootCommands array of root commands (usually only 1, your main command)
     *
     * @throws InstanceAlreadyExistsException when this command system is already running
     */
    public StrCenter(
            @NotNull final File settingsFolder,
            @NotNull final StrUser consoleUser,
            @NotNull final StrCategory... rootCommands
    ) throws InstanceAlreadyExistsException {
        this(
                settingsFolder,
                consoleUser,
                new StrParameterHandler<?>[0],
                new StrContextHandler<?>[0],
                true,
                rootCommands
        );
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
        user.sendMessage(new Str("And most likely category is: ").a(C.Y).a(commandMap.get(command.get(0)).getName()));
        return true;
    }

    /**
     * Send a debug message.
     * @param messages the debug message(s)
     */
    public void debug(Str... messages) {
        console.sendMessage(messages);
    }

    /**
     * Debug a string.
     * @param message the debug string
     */
    public void debug(String message) {
        debug(new Str(message));
    }

}
