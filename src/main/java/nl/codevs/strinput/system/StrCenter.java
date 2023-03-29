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
package nl.codevs.strinput.system;

import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.*;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.virtual.StrVirtualCategory;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Input center. The main class for interacting with StrInput.<br>
 * Make sure to point command calls to {@link #onCommand(List, StrUser)}

 * @see nl.codevs.strinput.examples.spigotmc
 * @author Sjoerd van de Goor
 * @since v0.1
 */
@SuppressWarnings("SpellCheckingInspection")
public class StrCenter {

    /**
     * Settings for this command system.
     */
    private StrSettings settings;

    /**
     * The settings file where the settings are stored.
     */
    private final File settingsFile;

    /**
     * The console which receives messages (such as from {@link #debug(String)}).
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
        return settings == null ? new StrSettings() : settings;
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
     */
    public StrCenter(
            @NotNull final File settingsFolder,
            @NotNull final StrUser consoleUser,
            @NotNull final StrParameterHandler<?>[] extraParameterHandlers,
            @NotNull final StrContextHandler<?>[] extraContextHandlers,
            @NotNull final StrCategory... rootCommands
    ) {
        Context.touch(this);
        Context.touch(consoleUser);

        settingsFile = new File(settingsFolder.getAbsolutePath()
                + "/strsettings.json");

        // Create settings and sender
        settings = StrSettings.fromConfigJson(settingsFile, this);
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
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     * @param settingsFolder the settings folder for this system
     *                      (settings file stored as {@code strconfig.json})
     * @param rootCommands array of root commands
     *                    (usually only 1, your main command)
     */
    public StrCenter(
            @NotNull final File settingsFolder,
            @NotNull final StrCategory... rootCommands
    ) {
        this(
                settingsFolder,
                DEFAULT_CONSOLE_USER,
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
            Context.touch(user);
            Context.touch(this);

            // Timing
            StopWatch s = null;
            if (getSettings().isDebugTime()) {
                s = new StopWatch();
                s.start();
            }

            // Hot-load settings
            settings = getSettings().hotLoad(settingsFile, this);

            // Remove empty arguments (spaces)
            List<String> arguments = command.stream().filter(
                    c -> !c.isBlank()
            ).collect(Collectors.toList());

            // Get main category
            String mainCommand = arguments.remove(0);
            StrVirtualCategory root = roots.get(mainCommand);

            // Run
            if (root == null) {
                user.sendMessage(C.RED + "Could not find root command for: " + C.BLUE + mainCommand);
                user.playSound(StrUser.StrSoundEffect.FAILED_COMMAND);
            } else if (!root.run(arguments)) {
                user.sendMessage(C.RED + "Failed to run your command!");
                user.playSound(StrUser.StrSoundEffect.FAILED_COMMAND);
            } else {
                Context.center().debug(C.GREEN + "Successfully ran your command!");
                user.playSound(StrUser.StrSoundEffect.SUCCESSFUL_COMMAND);
            }

            if (s != null) {
                s.stop();
                debug(C.GREEN + "Command sent by " + C.BLUE + user.getName() + C.GREEN + " took " + C.BLUE + s.getTime() + "ms");
            }

        };

        if (getSettings().isAsync()) {
            new Thread(cmd, "StrInput command by " + user.getName()).start();
        } else {
            cmd.run();
        }
    }

    /**
     * Send a debug message.
     * @param message the debug message
     */
    public void debug(final String message) {
        if (getSettings().isDebug()) {
            console.sendMessage(getSettings().getDebugPrefix() + C.GREEN + "DEBG: " + message);
        }
    }

    /**
     * Send a warning message.
     * @param message the warning message
     */
    public void warn(final String message) {
        if (getSettings().isWarn()) {
            console.sendMessage(getSettings().getDebugPrefix() + C.YELLOW + "WARN: " + message);
        }
    }

    /**
     * Send an error message.
     * @param message the error message
     */
    public void error(final String message) {
        if (getSettings().isError()) {
            console.sendMessage(getSettings().getDebugPrefix() + C.RED + "ERRR: " + message);
        }
    }

    /**
     * Send an information message.
     * @param message the information message
     */
    public void info(final String message) {
        if (getSettings().isInfo()) {
            console.sendMessage(getSettings().getDebugPrefix() + C.BLUE + "INFO: " + message);
        }
    }

    /**
     * Run a function sync (on the main thread, when needed).
     *
     * @param runnable the runnable to run
     */
    public void runSync(@NotNull Runnable runnable) {
        Context.user().sendMessage(C.RED + "Running command synchronous without having the method overridden. Ask your admin");
        warn("Running command synchronous without having the StrCenter#runSync(Runnable) method overridden. " +
                "Please overwrite the method to allow running on the main thread explicitly.");
        runnable.run();
    }

    /**
     * List this command system including
     * all listed root command categories, commands and parameters.
     * @param spacing the space to append to the prefix for subsequent sub-virtual elements
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
     * Print out an exception.
     * @param e the exception to print
     */
    public void printException(Throwable e) {
        error("Exception reported: " + C.BLUE + e + C.RED + ". Stacktrace:");
        for (StackTraceElement el : e.getStackTrace()) {
            error(el.toString());
        }
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
            if (center.getSettings().isSettingsCommands()) {
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

                // Actual virtual category (root)
                StrVirtualCategory root = new StrVirtualCategory(null, r);

                // Add names to root map
                root.getNames().forEach(n -> {
                    registeredRootNames.add(n);
                    put(n, root);
                });
            });

            // Debug startup
            if (rootInstancesSuccess.isEmpty()) {
                center.warn(C.RED + 
                        "No successful root instances registered."
                        + " Did you register all commands in the creator?"
                        + " Are they all annotated?");
            } else {
                center.info(C.GREEN + "Loaded root category classes: "
                        + C.BLUE + rootInstancesSuccess.stream().map(ri -> ri.getClass().getSimpleName())
                                .collect(Collectors.joining(C.GREEN + ", " + C.BLUE))
                );
            }

            if (rootInstancesFailed.size() > 0) {
                center.error(C.RED + "Failed to load root category classes: "
                        + C.BLUE + rootInstancesFailed.stream().map(ri -> ri.getClass().getSimpleName())
                        .collect(Collectors.joining(C.RED + ", " + C.BLUE))
                );
            }

            if (registeredRootNames.isEmpty() && rootInstancesSuccess.isEmpty()) {
                center.warn(C.RED + "No root commands & root instances registered!"
                        + " Did you register all commands in the creator?"
                        + " Are they @StrInput annotated?");
            } else {
                center.info(C.GREEN + "Loaded root commands: " + C.BLUE
                        + registeredRootNames.stream().collect(Collectors.joining(C.GREEN + ", " + C.BLUE))
                );
            }
        }
    }

    /**
     * String parameter handling.
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
     * String context handling.
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
         * Exception thrown when no context handlers could be found.
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

    /**
     * A default console user without platform-specific properties.
     */
    public static final StrUser DEFAULT_CONSOLE_USER = new StrUser() {
        @Override
        public @NotNull String getName() {
            return "Console";
        }

        @Override
        public void sendMessage(@NotNull final String message) {
            System.out.println(message);
        }

        @Override
        public boolean supportsClickable() {
            return false;
        }

        @Override
        public void playSound(@NotNull final StrSoundEffect sfx) {

        }

        @Override
        public boolean supportsContext() {
            return false;
        }

        @Override
        public boolean hasPermission(@NotNull final String permission) {
            return true;
        }
    };
}
