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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.context.StrNoContextHandlerException;
import nl.codevs.strinput.system.parameter.*;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.virtual.StrVirtualCategory;
import org.apache.commons.lang.time.StopWatch;
import org.jetbrains.annotations.NotNull;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Enumeration;
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
    public static Settings settings;
    final StrUser console;
    final StrRoots roots;

    /**
     * Create a new command center.<br>
     * Make sure to point command calls to {@link #onCommand(List, StrUser)}
     * @param settingsFolder the settings folder for this system (settings file stored as {@code strconfig.json})
     * @param consoleUser the console ({@link StrUser})
     * @param extraParameterHandlers additional parameter handlers
     * @param extraContextHandlers additional context handlers
     * @param rootCommands array of root commands (usually only 1, your main command)
     *
     * @throws InstanceAlreadyExistsException when this command system is already running
     * @throws InvalidParameterException when the specified {@code settingsFolder} is not a directory
     */
    public StrCenter(
            @NotNull final File settingsFolder,
            @NotNull final StrUser consoleUser,
            @NotNull final StrParameterHandler<?>[] extraParameterHandlers,
            @NotNull final StrContextHandler<?>[] extraContextHandlers,
            @NotNull final StrCategory... rootCommands
    ) throws InstanceAlreadyExistsException, InvalidParameterException {
        if (settings != null) {
            throw new InstanceAlreadyExistsException();
        }

        if (!settingsFolder.isDirectory()) {
            throw new InvalidParameterException("File specified: '" + settingsFolder.getAbsolutePath() + "' is not a directory");
        }

        // Fields
        settings = Settings.fromConfigJson(new File(settingsFolder.getAbsolutePath() + "/strsettings.json"), consoleUser);
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
        roots = new StrRoots(settings.settingsCommands, rootCommands, this);
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
                rootCommands
        );
    }

    /**
     * Command receiver.
     * @param command the command to parse
     * @param user the user that sent the command
     */
    public void onCommand(List<String> command, StrUser user) {

        Runnable cmd = () -> {
            StopWatch s = new StopWatch();
            s.start();

            // Hot-load settings
            settings = settings.hotload(console);

            // Remove empty arguments (spaces)
            List<String> arguments = command.stream().filter(c -> !c.isBlank()).collect(Collectors.toList());

            // Get main category
            String mainCommand = arguments.remove(0);
            StrVirtualCategory root = roots.get(mainCommand);

            // Store user in context for command invocation
            UserContext.touch(user);

            // Run
            if (root == null) {
                user.sendMessage(new Str(C.R).a("Could not find root command for: ").a(C.B).a(mainCommand));
                user.playSound(StrUser.StrSoundEffect.FAILED_COMMAND);
            } else if (!root.run(arguments, user, this)) {
                user.sendMessage(new Str(C.R).a("Failed to run your command!"));
                user.playSound(StrUser.StrSoundEffect.FAILED_COMMAND);
            } else {
                user.sendMessage(new Str(C.G).a("Successfully ran your command!"));
                user.playSound(StrUser.StrSoundEffect.SUCCESSFUL_COMMAND);
            }

            s.stop();
            if (settings.debugTime) {
                debug(new Str(C.G).a("Command sent by ").a(C.B).a(user.getName()).a(C.G).a(" took ").a(C.B).a(String.valueOf(s.getTime())));
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
    public void debug(Str message) {
        console.sendMessage(message);
    }

    /**
     * Send a debug message.
     * @param messages the debug message(s)
     */
    public void debug(List<Str> messages) {
        for (Str message : messages) {
            debug(message);
        }
    }

    /**
     * Debug a string.
     * @param message the debug string
     */
    public void debug(String message) {
        debug(new Str(message));
    }

    /**
     * Get console sender.
     * @return the console sender
     */
    public StrUser getConsole() {
        return console;
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
                roots.add(new Settings());
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
            if (settings.debugTime) {
                if (rootInstancesSuccess.isEmpty()) {
                    center.debug(new Str(C.R).a("No successful root instances registered. Did you register all commands in the creator? Are they all annotated?"));
                } else {
                    Str r = new Str(C.G).a("Loaded root category classes: ");
                    rootInstancesSuccess.forEach(c -> r.a(C.B).a(c.getClass().getSimpleName()).a(C.G).a(", "));
                    center.debug(r);
                }

                if (rootInstancesFailed.size() > 0) {
                    Str r = new Str(C.R);
                    center.debug(r.a("Failed root instances: ").a(C.B));
                    rootInstancesFailed.forEach(c -> r.a(C.R).a(", ").a(C.B).a(c.getClass().getSimpleName()));
                }

                if (registeredRootNames.isEmpty()) {
                    center.debug(new Str(C.R).a("No root commands registered! Did you register all commands in the creator? Are they @StrInput annotated?"));
                } else {
                    Str r = new Str(C.G).a("Loaded root commands: ");
                    registeredRootNames.forEach(c -> r.a(C.B).a(c).a(C.G).a(", "));
                    center.debug(r);
                }
            }
        }
    }

    /**
     * List this command system including all listed root command categories, commands and parameters.
     * @param spacing the space to append to the prefix for subsequent sub-virtuals
     * @param exampleInput the input example to show matching scores
     */
    public List<String> getListing(String spacing, List<String> exampleInput) {
        List<String> result = new ArrayList<>();
        List<StrVirtualCategory> loadedCategories = new ArrayList<>();

        for (StrVirtualCategory value : roots.values()) {
            if (!loadedCategories.contains(value)) {
                loadedCategories.add(value);
            }
        }

        result.add(getClass().getSimpleName() + " command system with " + loadedCategories.size() + " loaded roots with input: " + String.join(" ", exampleInput));
        for (StrVirtualCategory loadedCategory : loadedCategories) {
            loadedCategory.getListing(spacing, spacing, result, exampleInput);
        }
        return result;
    }

    /**
     * Str parameter handling.
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class ParameterHandling {

        private static List<StrParameterHandler<?>> parameterHandlers = new ArrayList<>();

        /**
         * Get handler for a type.
         * @param type the type to get the handler for
         * @return the parameter handler for the type
         * @throws StrNoParameterHandlerException if no parameter handler could be found
         */
        public static StrParameterHandler<?> getHandler(Class<?> type) throws StrNoParameterHandlerException {
            for (StrParameterHandler<?> parameterHandler : parameterHandlers) {
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
        public static void register(StrParameterHandler<?>... handlers) {
            parameterHandlers.addAll(List.of(handlers));
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
        private static final List<StrContextHandler<?>> contextHandlers = new ArrayList<>();

        /**
         * Get context handler for a type.
         * @param type the type to get the context handler for
         * @return the context handler for the type
         * @throws StrNoContextHandlerException if no context handler could be found
         */
        public static StrContextHandler<?> getContextHandler(Class<?> type) throws StrNoContextHandlerException {
            for (StrContextHandler<?> parameterHandler : contextHandlers) {
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
        public static void register(StrContextHandler<?>... handlers) {
            contextHandlers.addAll(List.of(handlers));
        }
    }

    /**
     * Context handling (user handling).
     *
     * This system REQUIRES:
     * <ul>
     *     <li>each command to be be handled in a new thread</li>
     *     <li>a call to {@link #touch(StrUser)} asap after a command call</li>
     * </ul>
     *
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public abstract static class UserContext {

        private static final ConcurrentHashMap<Thread, StrUser> context = new ConcurrentHashMap<>();

        /**
         * Get the current user from the current thread's context
         *
         * @return the {@link StrUser} for this thread
         */
        public static StrUser get() {
            return context.get(Thread.currentThread());
        }

        /**
         * Add the {@link StrUser} to the context map & removes dead threads
         *
         * @param user the user
         */
        public static void touch(StrUser user) {
            synchronized (context) {
                context.put(Thread.currentThread(), user);

                Enumeration<Thread> contextKeys = context.keys();

                while (contextKeys.hasMoreElements()) {
                    Thread thread = contextKeys.nextElement();
                    if (!thread.isAlive()) {
                        context.remove(thread);
                    }
                }
            }
        }
    }

    /**
     * StrInput settings.
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    @StrInput(description = "StrInput settings", aliases = "stri", name = "strinput", permission = "strinput")
    public static class Settings implements StrCategory {
        private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        private static long lastChanged;
        private static File file;

        /**
         * Debug message prefix.
         * Cannot be modified by commands.
         */
        public static Str debugPrefix = new Str(C.R).a("[").a(C.G).a("StrInput").a(C.R).a("]").a(C.X);

        @StrInput(description = "Which threshold should be met for command matching using our improved N-Gram search algorithm?")
        public void setMatchThreshold(
                @Param(
                        description = "The match threshold",
                        defaultValue = "0.1",
                        name = "threshold"
                )
                        Double threshold
        ){
            settings.matchThreshold = threshold;
            user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("matching threshold ").a(C.G).a("to: ").a(C.B).a(String.valueOf(settings.matchThreshold)));
        }
        public double matchThreshold = 0.1;

        @StrInput(description = "Should users with the 'strinput' permission be able to use commands to change settings?")
        public void setSettingsCommands(
                @Param(
                        description = "Whether to set this setting to true or false",
                        defaultValue = "toggle",
                        name = "enable"
                )
                        Boolean enable
        ){
            settings.settingsCommands = enable == null ? !settings.settingsCommands : enable;
            user().sendMessage(new Str(C.G).a("After a restart, ").a(C.B).a("settings commands ").a(C.G).a("will be: ").a(C.B).a(String.valueOf(settings.settingsCommands)));
        }
        public boolean settingsCommands = true;

        @StrInput(description = "Should commands be ran in async or sync (does not overwrite the 'sync' setting in individual StrInputs)")
        public void setAsync(
                @Param(
                        description = "Whether to set this setting to true or false",
                        defaultValue = "toggle",
                        name = "enable"
                )
                        Boolean enable
        ){
            settings.async = enable == null ? !settings.async : enable;
            user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("async ").a(C.G).a("to: ").a(C.B).a(String.valueOf(settings.async)));
        }
        public boolean async;

        @StrInput(description = "When entering arguments, should people be allowed to enter 'null'?")
        public void allowNullInput(
                @Param(
                        description = "Whether to set this setting to true or false",
                        defaultValue = "toggle",
                        name = "enable"
                )
                        Boolean enable
        ){
            settings.allowNullInput = enable == null ? !settings.allowNullInput : enable;
            user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("allow null input ").a(C.G).a("to: ").a(C.B).a(String.valueOf(settings.allowNullInput)));
        }
        public boolean allowNullInput = false;

        @StrInput(description = "Whether to send debug messages or not")
        public void debug(
                @Param(
                        description = "Whether to set this setting to true or false",
                        defaultValue = "toggle",
                        name = "enable"
                )
                        Boolean enable
        ){
            settings.debug = enable == null ? !settings.debug : enable;
            user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("debug ").a(C.G).a("to: ").a(C.B).a(String.valueOf(settings.debug)));
        }
        public boolean debug = false;

        @StrInput(description = "Whether to send debug messages on the time command running took")
        public void setDebugTime(
                @Param(
                        description = "Whether to set this setting to true or false",
                        defaultValue = "toggle",
                        name = "enable"
                )
                        Boolean enable
        ){
            settings.debugTime = enable == null ? !settings.debugTime : enable;
            user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("debugTime ").a(C.G).a("to: ").a(String.valueOf(settings.debugTime)));
        }
        public boolean debugTime;

        @StrInput(description = "Whether to debug matching or not. This is also ran on tab completion.")
        public void debugMatching(
                @Param(
                        description = "Whether to set this setting to true or false",
                        defaultValue = "toggle",
                        name = "enable"
                )
                        Boolean enable
        ){
            settings.debugMatching = enable == null ? !settings.debugMatching : enable;
            user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("debug matching ").a(C.G).a("to: ").a(String.valueOf(settings.debugMatching)));
        }
        public boolean debugMatching = true;
        @StrInput(description = "Auto-pick the first option when multiple exist?")
        public void pickFirstOnMultiple(
                @Param(
                        description = "Whether to set this setting to true or false",
                        defaultValue = "toggle",
                        name = "enable"
                )
                        Boolean enable
        ){
            settings.pickFirstOnMultiple = enable == null ? !settings.pickFirstOnMultiple : enable;
            user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("pick first on multiple ").a(C.G).a("to: ").a(String.valueOf(settings.pickFirstOnMultiple)));
        }
        public boolean pickFirstOnMultiple = false;

        /**
         * Load a new StrInput file from json
         * @param file the file to read json from
         * @param console the console user
         * @return the new {@link Settings}
         */
        public static Settings fromConfigJson(File file, StrUser console) {
            Settings.file = file;
            lastChanged = file.lastModified();
            try {
                if (!file.exists() || file.length() == 0) {
                    file.getParentFile().mkdirs();
                    Settings new_ = new Settings();
                    FileWriter f = new FileWriter(file);
                    gson.toJson(new_, Settings.class, f);
                    f.close();
                    console.sendMessage(new Str(C.G).a("Made new StrInput config (").a(C.B).a(file.getParent().replace("\\", "/")  + "/" + file.getName()).a(C.G).a(")"));
                    return new_;
                }
                console.sendMessage(new Str(C.G).a("Loaded existing StrInput config (").a(C.B).a(file.getParent().replace("\\", "/") + "/" + file.getName()).a(C.G).a(")"));
                return new Gson().fromJson(new FileReader(file), Settings.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Save the config to
         * @param file a file (path)
         */
        public void saveToConfig(File file, StrUser console) {
            try {
                FileWriter f = new FileWriter(file);
                gson.toJson(this, Settings.class, f);
                f.close();
                console.sendMessage(new Str(C.G).a("Saved StrInput Settings"));
                lastChanged = file.lastModified();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Hotload settings from file
         *
         * @return the new settings
         */
        public Settings hotload(StrUser console) {

            // Load file
            Settings fileSettings = fromConfigJson(file, console);
            assert fileSettings != null;

            // File is newer
            if (lastChanged != file.lastModified()) {
                lastChanged = file.lastModified();
                console.sendMessage(new Str(C.G).a("Hotloaded StrInput Settings"));
                return fileSettings;
            }

            // In-memory settings are newer
            if (!fileSettings.equals(this)) {
                saveToConfig(file, console);
            }
            return this;
        }
    }
}
