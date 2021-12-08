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
package nl.codevs.strinput.system.api;

import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.BooleanHandler;
import nl.codevs.strinput.system.parameter.ByteHandler;
import nl.codevs.strinput.system.parameter.DoubleHandler;
import nl.codevs.strinput.system.parameter.FloatHandler;
import nl.codevs.strinput.system.parameter.IntegerHandler;
import nl.codevs.strinput.system.parameter.LongHandler;
import nl.codevs.strinput.system.parameter.ShortHandler;
import nl.codevs.strinput.system.parameter.StringHandler;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.virtual.StrVirtualCategory;
import org.apache.commons.lang.time.StopWatch;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Input center. The main class for interacting with Strinput.<br>
 * Make sure to point command calls to {@link #onCommand(List, StrUser)}

 * @see nl.codevs.strinput.examples.spigotmc
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public abstract class StrCenter {

    /**
     * Settings for this command system.
     */
    private StrSettings settings;

    /**
     * The console which receives messages (such as from {@link #debug(Str)}).
     */
    private final StrUser console;

    /**
     * Roots instance containing root command categories.
     */
    private final Roots roots;

    /**
     * Get console sender.
     * @return the console sender
     */
    public StrUser getConsole() {
        return console;
    }

    /**
     * Get the settings.
     * @return the settings
     */
    public StrSettings getSettings() {
        return settings;
    }

    /**
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     * @param settingsFolder the settings folder for this system
     *                      (settings file stored as {@code strconfig.json})
     * @param consoleUser the console ({@link StrUser})
     * @param extraParameterHandlers additional parameter handlers
     * @param extraContextHandlers additional context handlers
     * @param rootCommands array of root commands
     *                    (usually only 1, your main command)
     *
     * @throws InvalidParameterException
     * when the specified {@code settingsFolder} is not a directory
     */
    public StrCenter(
            @NotNull final File settingsFolder,
            @NotNull final StrUser consoleUser,
            @NotNull final StrParameterHandler<?>[] extraParameterHandlers,
            @NotNull final StrContextHandler<?>[] extraContextHandlers,
            @NotNull final StrCategory... rootCommands
    ) throws InvalidParameterException {

        if (!settingsFolder.isDirectory()) {
            throw new InvalidParameterException(
                    "File specified: '"
                            + settingsFolder.getAbsolutePath()
                            + "' is not a directory"
            );
        }

        // Create settings and sender
        settings = StrSettings.fromConfigJson(
                new File(settingsFolder.getAbsolutePath()
                        + "/strsettings.json"),
                consoleUser
        );
        console = consoleUser;

        // Handlers
        ParameterHandling.register(extraParameterHandlers);
        ParameterHandling.register(
                new BooleanHandler(),
                new ByteHandler(),
                new DoubleHandler(),
                new FloatHandler(),
                new IntegerHandler(),
                new LongHandler(),
                new ShortHandler(),
                new StringHandler()
        );
        ContextHandling.register(extraContextHandlers);

        // Command map (roots)
        roots = new Roots(rootCommands, this);
    }

    /**
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     * @param settingsFolder the settings folder for this system
     *                      (settings file stored as {@code strconfig.json})
     * @param consoleUser the console ({@link StrUser})
     * @param rootCommands array of root commands
     *                    (usually only 1, your main command)
     *
     * @throws InvalidParameterException
     * when the specified {@code settingsFolder} is not a directory
     */
    public StrCenter(
            @NotNull final File settingsFolder,
            @NotNull final StrUser consoleUser,
            @NotNull final StrCategory... rootCommands
    ) {
        this(
                settingsFolder,
                consoleUser,
                new StrParameterHandler<?>[0],
                new StrContextHandler<?>[0],
                rootCommands
        );
    }

    /**
     * Command receiver.
     * @param command the command to parse
     * @param user the user that sent the command
     */
    public void onCommand(
            @NotNull final List<String> command,
            @NotNull final StrUser user
    ) {

        Runnable cmd = () -> {

            // Store user in context for command invocation
            Env.touch(user);
            Env.touch(this);

            // Timing
            StopWatch s = new StopWatch();
            s.start();

            // Hot-load settings
            settings = settings.hotload(user);

            // Remove empty arguments (spaces)
            List<String> arguments = command.stream().filter(
                    c -> !c.isBlank()
            ).collect(Collectors.toList());

            // Get main category
            String mainCommand = arguments.remove(0);
            StrVirtualCategory root = roots.get(mainCommand);

            // Run
            if (root == null) {
                user.sendMessage(new Str(C.R)
                        .a("Could not find root command for: ")
                        .a(C.B)
                        .a(mainCommand)
                );
                user.playSound(StrUser.StrSoundEffect.FAILED_COMMAND);
            } else if (!root.run(arguments)) {
                user.sendMessage(new Str(C.R)
                        .a("Failed to run your command!"));
                user.playSound(StrUser.StrSoundEffect.FAILED_COMMAND);
            } else {
                user.sendMessage(new Str(C.G)
                        .a("Successfully ran your command!"));
                user.playSound(StrUser.StrSoundEffect.SUCCESSFUL_COMMAND);
            }

            s.stop();
            if (settings.debugTime) {
                debug(new Str(C.G).a("Command sent by ")
                        .a(C.B).a(user.getName())
                        .a(C.G).a(" took ")
                        .a(C.B).a(String.valueOf(s.getTime()))
                );
            }

        };

        if (settings.async) {
            new Thread(cmd, "StrInput command by " + user.getName()).start();
        } else {
            cmd.run();
        }
    }

    /**
     * Send a debug message.
     * @param message the debug message(s)
     */
    public void debug(final Str message) {
        console.sendMessage(settings.debugPrefix.copy().a(message));
    }

    /**
     * Send a debug message.
     * @param messages the debug message(s)
     */
    public void debug(final @NotNull List<Str> messages) {
        for (Str message : messages) {
            debug(message);
        }
    }

    /**
     * Debug a string.
     * @param message the debug string
     */
    public void debug(final String message) {
        debug(new Str(message));
    }

    /**
     * Run a function sync (on the main thread, when needed).
     *
     * @param runnable the runnable to run
     */
    public abstract void runSync(@NotNull Runnable runnable);

    /**
     * List this command system including
     * all listed root command categories, commands and parameters.
     * @param spacing the space to append to the prefix
     *               for subsequent sub-virtuals
     * @param exampleInput the input example to show matching scores
     * @return a list of strings representing this command system
     */
    public List<String> getListing(
            @NotNull final String spacing,
            @NotNull final List<String> exampleInput
    ) {
        List<String> result = new ArrayList<>();
        List<StrVirtualCategory> loadedCategories = new ArrayList<>();

        for (StrVirtualCategory value : roots.values()) {
            if (!loadedCategories.contains(value)) {
                loadedCategories.add(value);
            }
        }

        result.add(getClass().getSimpleName()
                + " command system with "
                + loadedCategories.size()
                + " loaded roots with input: "
                + String.join(" ", exampleInput)
        );
        for (StrVirtualCategory loadedCategory : loadedCategories) {
            loadedCategory.getListing(spacing, spacing, result, exampleInput);
        }
        return result;
    }

    /**
     * Command roots mapping.<br>
     * Functions just as a normal
     * {@link ConcurrentHashMap} but setup is built-in.
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class Roots extends
            ConcurrentHashMap<String, StrVirtualCategory> {

        /**
         * Create command roots.
         * @param categories array of categories
         * @param center the command center controlling these roots
         */
        public Roots(
                @NotNull final StrCategory[] categories,
                @NotNull final StrCenter center
        ) {

            // Debug
            List<StrCategory> rootInstancesFailed = new ArrayList<>();
            List<StrCategory> rootInstancesSuccess = new ArrayList<>();
            List<String> registeredRootNames = new ArrayList<>();

            // Roots
            List<StrCategory> roots = new ArrayList<>(List.of(categories));
            if (center.getSettings().settingsCommands) {
                roots.add(center.getSettings());
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
                StrInput input = r.getClass()
                        .getDeclaredAnnotation(StrInput.class);

                // Instance names
                List<String> names = new ArrayList<>();
                names.add(input.name());
                names.addAll(Arrays.asList(input.aliases()));

                // Actual virtual category (root)
                StrVirtualCategory root = new StrVirtualCategory(null, r);

                // Add names to root map
                names.forEach(n -> {
                    registeredRootNames.add(n);
                    put(n, root);
                });
            });

            // Debug startup
            if (center.getSettings().debugTime) {
                if (rootInstancesSuccess.isEmpty()) {
                    center.debug(new Str(C.R).a(
                            "No successful root instances registered."
                            + " Did you register all commands in the creator?"
                            + " Are they all annotated?")
                    );
                } else {
                    Str r = new Str(C.G).a("Loaded root category classes: ");
                    rootInstancesSuccess.forEach(
                            c -> r.a(C.B).a(c.getClass().getSimpleName())
                                    .a(C.G).a(", ")
                    );
                    center.debug(r);
                }

                if (rootInstancesFailed.size() > 0) {
                    Str r = new Str(C.R);
                    center.debug(r.a("Failed root instances: ").a(C.B));
                    rootInstancesFailed.forEach(
                            c -> r.a(C.R).a(", ")
                                    .a(C.B).a(c.getClass().getSimpleName())
                    );
                }

                if (registeredRootNames.isEmpty()) {
                    center.debug(new Str(C.R).a("No root commands registered!"
                            + " Did you register all commands in the creator?"
                            + " Are they @StrInput annotated?")
                    );
                } else {
                    Str r = new Str(C.G).a("Loaded root commands: ");
                    registeredRootNames.forEach(
                            c -> r.a(C.B).a(c).a(C.G).a(", ")
                    );
                    center.debug(r);
                }
            }
        }
    }

    /**
     * Str parameter handling.
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class ParameterHandling {

        /**
         * Parameter handlers available.
         */
        private static final List<StrParameterHandler<?>>
                PARAMETER_HANDLERS = new ArrayList<>();

        /**
         * Get handler for a type.
         * @param type the type to get the handler for
         * @return the parameter handler for the type
         * @throws StrNoParameterHandlerException
         * if no parameter handler could be found
         */
        public static @NotNull StrParameterHandler<?> getHandler(
                @NotNull final Class<?> type
        ) throws StrNoParameterHandlerException {
            for (StrParameterHandler<?> parameterHandler : PARAMETER_HANDLERS) {
                if (parameterHandler.supports(type)) {
                    return parameterHandler;
                }
            }
            throw new StrNoParameterHandlerException(type);
        }

        /**
         * Register new parameter handlers.
         * @param handlers the parameter handlers
         */
        public static void register(
                @NotNull final StrParameterHandler<?>... handlers
        ) {
            PARAMETER_HANDLERS.addAll(List.of(handlers));
        }

        /**
         * Exception thrown when no parameter handler could be found.
         *
         * @author Sjoerd van de Goor
         * @since v0.1
         */
        public static class StrNoParameterHandlerException extends Exception {

            /**
             * Create a new exception.
             * @param type the type for which no handler is available.
             */
            public StrNoParameterHandlerException(
                    @NotNull final Class<?> type
            ) {
                super("Could not find parameter handler for: "
                        + type.getSimpleName());
            }
        }
    }

    /**
     * Str context handling.
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class ContextHandling {

        /**
         * Context handler list.
         */
        private static final List<StrContextHandler<?>>
                CONTEXT_HANDLERS = new ArrayList<>();

        /**
         * Get context handler for a type.
         * @param type the type to get the context handler for
         * @return the context handler for the type
         * @throws StrNoContextHandlerException
         * if no context handler could be found
         */
        public static @NotNull StrContextHandler<?> getContextHandler(
                @NotNull final Class<?> type
        ) throws StrNoContextHandlerException {
            for (StrContextHandler<?> parameterHandler : CONTEXT_HANDLERS) {
                if (parameterHandler.supports(type)) {
                    return parameterHandler;
                }
            }
            throw new StrNoContextHandlerException(type);
        }

        /**
         * Register new context handlers.
         * @param handlers the context handlers
         */
        public static void register(
                @NotNull final StrContextHandler<?>... handlers
        ) {
            CONTEXT_HANDLERS.addAll(List.of(handlers));
        }

        /**
         * Exception for when no context handlers exist.
         *
         * @author Sjoerd van de Goor
         * @since v0.1
         */
        public static class StrNoContextHandlerException extends Exception {

            /**
             * Create a new exception.
             * @param type the type for which no context handler is available
             */
            public StrNoContextHandlerException(
                    @NotNull final Class<?> type
            ) {
                super("Could not find parameter handler for: "
                        + type.getSimpleName());
            }
        }
    }

}
