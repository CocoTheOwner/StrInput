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

package nl.codevs.strinput.system.api;

import nl.codevs.strinput.system.context.StrContext;
import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.*;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.virtual.StrVirtualCategory;
import org.jetbrains.annotations.NotNull;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * Command roots mapping. Functions just as a normal {@link ConcurrentHashMap} but setup is built-in.
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class StrRoots extends ConcurrentHashMap<String, StrVirtualCategory> {

        /**
         * Create command roots
         * @param enableSettingsCommands if set to true, enables commands for the system's settings
         * @param categories array of categories
         * @param center the controlling command center
         */
        public StrRoots(final boolean enableSettingsCommands, final StrCategory[] categories, final StrCenter center) {

            // Debug
            List<StrCategory> rootInstancesFailed = new ArrayList<>();
            List<StrCategory> rootInstancesSuccess = new ArrayList<>();
            List<String> registeredRootNames = new ArrayList<>();

            // Roots
            List<StrCategory> roots = new ArrayList<>(List.of(categories));
            if (enableSettingsCommands) {
                roots.add(new StrSettings());
            }

            // Setup each root
            roots.forEach(r -> {
                if (r.getClass().isAnnotationPresent(StrInput.class)) {
                    rootInstancesSuccess.add(r);
                } else {
                    rootInstancesFailed.add(r);
                    return;
                }

                // Get input annotation of the root instance
                StrInput input = r.getClass().getDeclaredAnnotation(StrInput.class);

                // Instance names
                List<String> names = new ArrayList<>();
                names.add(input.name());
                names.addAll(Arrays.asList(input.aliases()));

                // Actual virtual category (root)
                StrVirtualCategory root = new StrVirtualCategory(null, r, center);

                // Add names to root map
                names.forEach(n -> {
                    registeredRootNames.add(n);
                    put(n, root);
                });
            });

            // Debug startup
            if (StrCenter.settings.debugStartup) {
                if (rootInstancesSuccess.isEmpty()) {
                    center.debug(new Str(C.R).a("No successful root instances registered. Did you register all commands in the creator? Are they all annotated?"));
                } else {
                    Str r = new Str(C.G).a("Loaded root category classes: ");
                    rootInstancesSuccess.forEach(c -> r.a(C.Y).a(c.getClass().getSimpleName()).a(C.G).a(", "));
                    center.debug(r);
                }

                if (rootInstancesFailed.size() > 0) {
                    Str r = new Str(C.R);
                    rootInstancesFailed.forEach(c -> r.a(C.R).a(", ").a(C.Y).a(c.getClass().getSimpleName()));
                    center.debug(r.a("Failed root instances: ").a(C.Y));
                }

                if (registeredRootNames.isEmpty()) {
                    center.debug(new Str(C.R).a("No root commands registered! Did you register all commands in the creator? Are they @StrInput annotated?"));
                } else {
                    Str r = new Str(C.G).a("Loaded root commands: ");
                    registeredRootNames.forEach(c -> r.a(C.Y).a(c).a(C.G).a(", "));
                    center.debug(r);
                }
            }
        }
    }
}
