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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.codevs.strinput.system.text.C;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * StrInput settings.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
@StrInput(
        description = "StrInput settings",
        aliases = "stri",
        name = "strinput",
        permission = "strinput"
)
public final class StrSettings implements StrCategory {
    /**
     * Gson for string-to-class and class-to-string conversion.
     */
    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    /**
     * The default amount of seconds users get to pick an option.
     */
    private static final int DEFAULT_PICKING_AMOUNT = 15;

    /**
     * The default threshold matching should achieve,
     * before a match is allowed.
     */
    private static final double DEFAULT_MATCH_THRESHOLD = 0.1;

    /**
     * The last time the settings file was modified
     * (to check for re-saving).
     */
    private transient long lastChanged;

    /**
     * Debug message prefix.
     * Cannot be modified by commands.
     */
    private final String debugPrefix = C.RED + "[" + C.GREEN + "StrInput" + C.RED + "] " + C.RESET;

    /**
     * @return the debug message prefix.
     */
    public String getDebugPrefix() {
        return debugPrefix;
    }

    /**
     * Create a new configuration.
     */
    public StrSettings() {

    }

    /**
     * Set the amount of times a user can re-try picking an option.
     * @param times the amount of times a user can re-try picking an option
     */
    @StrInput(description =
            "How many times should a user re-try picking an option?")
    public void setPickingAmount(
            @Param(
                    description =
                            "The amount of times the user can pick an option",
                    defaultValue = "1",
                    name = "times"
            )
                    final int times
    ) {
        pickingAmount = times;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "option picking amount " + C.GREEN + "to: " + C.BLUE + pickingAmount);
    }
    /**
     * The amount of times the user can try to pick an option.
     */
    private int pickingAmount = 1;

    /**
     * @return the amount of times the user can try to pick an option.
     */
    public int getPickingAmount() {
        return pickingAmount;
    }

    /**
     * Set the amount of time a user has to pick an option.
     * @param time the amount of time a user has to pick an option
     */
    @StrInput(description = "How much time (in seconds)"
            + " should user have for picking an option?")
    public void setPickingTimeout(
            @Param(
                    description = "The picking timeout time in seconds",
                    defaultValue = "15",
                    name = "time"
            )
                    final int time
    ) {
        pickingTimeout = time;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "option picking time " + C.GREEN + "to: " + C.BLUE + pickingTimeout);
    }
    /**
     * The amount of time (in seconds) the user has to pick an option.
     */
    private long pickingTimeout = DEFAULT_PICKING_AMOUNT;

    /**
     * @return the amount of time (in seconds) the user has to pick an option.
     */
    public long getPickingTimeout() {
        return pickingTimeout;
    }

    /**
     * Set the matching threshold.
     * @param threshold the matching threshold
     */
    @StrInput(description = "Which threshold should be met for command matching"
            + " using our improved N-Gram search algorithm?")
    public void setMatchThreshold(
            @Param(
                    description = "The match threshold",
                    defaultValue = "0.1",
                    name = "threshold"
            )
                    final Double threshold
    ) {
        matchThreshold = threshold;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "matching threshold " + C.GREEN + "to: " + C.BLUE + matchThreshold);
    }

    /**
     * The threshold that should be met when matching using
     * {@link nl.codevs.strinput.system.util.NGram#ngramMatching(String, List)}.
     */
    private double matchThreshold = DEFAULT_MATCH_THRESHOLD;

    /**
     * @return the threshold that should be met when matching using
     * {@link nl.codevs.strinput.system.util.NGram#ngramMatching(String, List)}.
     */
    public double getMatchThreshold() {
        return matchThreshold;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Should users with the 'strinput'"
            + " permission be able to use commands to change settings?")
    public void setSettingsCommands(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    final Boolean enable
    ) {
        settingsCommands = enable == null ? !settingsCommands : enable;
        user().sendMessage(C.GREEN + "After a restart, " + C.BLUE + "settings commands " + C.GREEN + "will be: " + C.BLUE + settingsCommands);
    }

    /**
     * Whether to allow users to send commands to change settings.
     */
    private boolean settingsCommands = false;

    /**
     * @return true if users are allowed to send commands to change settings
     */
    public boolean isSettingsCommands() {
        return settingsCommands;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Should commands be ran in async or sync"
            + " (does not overwrite the 'sync'"
            + " setting in individual StrInputs)")
    public void setAsync(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    final Boolean enable
    ) {
        async = enable == null ? !async : enable;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "async " + C.GREEN + "to: " + C.BLUE + async);
    }

    /**
     * Whether asynchronous command running should be done or not.
     */
    private boolean async = true;

    /**
     * @return true if asynchronous command running should be done
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "When entering arguments,"
            + " should people be allowed to enter 'null'?")
    public void allowNullInput(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    final Boolean enable
    ) {
        allowNullInput = enable == null ? !allowNullInput : enable;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "allow null input " + C.GREEN + "to: " + C.BLUE + allowNullInput);
    }

    /**
     * If true, 'null' is allowed as input.
     */
    private boolean allowNullInput = false;

    /**
     * @return true if 'null' as input is allowed.
     */
    public boolean isAllowNullInput() {
        return allowNullInput;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Whether to send debug messages or not")
    public void debug(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    final Boolean enable
    ) {
        debug = enable == null ? !debug : enable;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "debug " + C.GREEN + "to: " + C.BLUE + debug);
    }

    /**
     * Whether debug messages are enabled or not.
     */
    private boolean debug = false;

    /**
     * @return whether debug messages are enabled or not
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Whether to send warning messages or not")
    public void warn(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
            final Boolean enable
    ) {
        warn = enable == null ? !warn : enable;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "warnings " + C.GREEN + "to: " + C.BLUE + warn);
    }

    /**
     * Whether warning messages are enabled or not.
     */
    private boolean warn = true;

    /**
     * @return whether warning messages are enabled or not
     */
    public boolean isWarn() {
        return warn;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Whether to send error messages or not")
    public void error(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
            final Boolean enable
    ) {
        error = enable == null ? !error : enable;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "errors " + C.GREEN + "to: " + C.BLUE + error);
    }

    /**
     * Whether error messages are enabled or not.
     */
    private boolean error = true;

    /**
     * @return whether information messages are enabled or not
     */
    public boolean isInfo() {
        return info;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Whether to send info messages or not")
    public void info(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
            final Boolean enable
    ) {
        info = enable == null ? !info : enable;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "information " + C.GREEN + "to: " + C.BLUE + info);
    }

    /**
     * Whether information messages are enabled or not.
     */
    private boolean info = true;

    /**
     * @return whether error messages are enabled or not
     */
    public boolean isError() {
        return error;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Whether to send debug messages"
            + " about the time command running took")
    public void setDebugTime(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    final Boolean enable
    ) {
        debugTime = enable == null ? !debugTime : enable;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "debugTime " + C.GREEN + "to: " + debugTime);
    }

    /**
     * Whether to debug command runtime or not.
     */
    private boolean debugTime = true;

    /**
     * @return true if debugging command runtime is enabled
     */
    public boolean isDebugTime() {
        return debugTime;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Whether to debug matching or not."
            + " This is also ran on tab completion.")
    public void debugMatching(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    final Boolean enable
    ) {
        debugMatching = enable == null ? !debugMatching : enable;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "debug matching " + C.GREEN + "to: " + debugMatching);
    }

    /**
     * Whether matching should be debugged or not.
     */
    private boolean debugMatching = true;

    /**
     * @return true if matching should be debugged
     */
    public boolean isDebugMatching() {
        return debugMatching;
    }

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Auto-pick the first option when multiple exist?")
    public void pickFirstOnMultiple(
            @Param(
                    description = "Whether to set this setting"
                            + " to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    final Boolean enable
    ) {
        pickFirstOnMultiple = enable == null ? !pickFirstOnMultiple : enable;
        user().sendMessage(C.GREEN + "Set " + C.BLUE + "pick first on multiple " + C.GREEN + "to: " + pickFirstOnMultiple);
    }

    /**
     * Whether to pick the first option when multiple are available.
     * Effectively not giving the user the option to pick.
     */
    private boolean pickFirstOnMultiple = false;

    /**
     * @return Whether to pick the first option when multiple are available.<br>
     * Effectively not giving the user the option to pick.
     */
    public boolean isPickFirstOnMultiple() {
        return pickFirstOnMultiple;
    }

    /**
     * Load a new StrInput file from json.
     *
     * @param settingsFile the file to read json from
     * @param center the center to send debug to
     * @return the new {@link StrSettings}
     */
    public static StrSettings fromConfigJson(
            @NotNull final File settingsFile,
            @NotNull final StrCenter center
    ) {
        try {
            if (!settingsFile.exists() || settingsFile.length() == 0) {
                settingsFile.getParentFile().mkdirs();
                StrSettings newSettings = new StrSettings();
                FileWriter f = new FileWriter(settingsFile);
                GSON.toJson(newSettings, StrSettings.class, f);
                f.close();
                newSettings.lastChanged = settingsFile.lastModified();
                center.debug(C.GREEN + "Made new StrInput config (" + C.BLUE + settingsFile.getParent().replace("\\", "/")
                                + "/" + settingsFile.getName() + C.GREEN + ")");
                return newSettings;
            }
            return new Gson().fromJson(
                    new FileReader(settingsFile),
                    StrSettings.class
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save the config to the specified file.
     *
     * @param settingsFile the file to save the config to
     * @param center the center to send debug messages to
     */
    public void saveToConfig(
            @NotNull final File settingsFile,
            @NotNull final StrCenter center
    ) {
        try {
            FileWriter f = new FileWriter(settingsFile);
            GSON.toJson(this, StrSettings.class, f);
            f.close();
            center.debug(C.GREEN + "Saved StrInput Settings");
            lastChanged = settingsFile.lastModified();
        } catch (IOException e) {
            center.debug("Failed to save config: \n"
                    + GSON.toJson(this));
            e.printStackTrace();
        }
    }

    /**
     * Hot-load settings from file.
     *
     * @param settingsFile the settings file where to load from / save to
     * @param center the center to send debug messages to
     *
     * @return the new settings
     */
    public StrSettings hotLoad(
            @NotNull final File settingsFile,
            @NotNull final StrCenter center
    ) {

        // Load file
        StrSettings fileSettings = fromConfigJson(settingsFile, center);
        assert fileSettings != null;

        // File is newer
        if (lastChanged != settingsFile.lastModified()) {
            lastChanged = settingsFile.lastModified();
            center.debug(C.GREEN + "Hotloaded StrInput Settings");
            return fileSettings;
        }

        // In-memory settings are newer
        if (!fileSettings.equals(this)) {
            saveToConfig(settingsFile, center);
        }
        return this;
    }
}
