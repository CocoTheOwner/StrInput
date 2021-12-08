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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
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
     * The last time the settings file was modified
     * (to check for re-saving).
     */
    private static long lastChanged;

    /**
     * The file the settings are stored in.
     */
    private transient final File settingsFile;

    /**
     * Debug message prefix.
     * Cannot be modified by commands.
     */
    public Str debugPrefix = new Str(C.R).a("[")
            .a(C.G).a("StrInput")
            .a(C.R).a("] ")
            .a(C.X);

    /**
     * Create a new configuration.
     * @param file the settings file
     */
    public StrSettings(@NotNull final File file) {
        this.settingsFile = file;
    }

    @StrInput(description = "How many times should a user re-try picking an option?")
    public void setPickingAmount(
            @Param(
                    description = "The amount of times the user can pick an option",
                    defaultValue = "3",
                    name = "times"
            )
                    int times
    ) {
        pickingAmount = times;
        user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("option picking amount ").a(C.G).a("to: ").a(C.B).a(String.valueOf(pickingAmount)));
    }
    /**
     * The amount of time (in seconds) the user has to pick an option.
     */
    public int pickingAmount;

    @StrInput(description = "How much time (in seconds) should user have for picking an option?")
    public void setPickingTimeout(
            @Param(
                    description = "The picking timeout time in seconds",
                    defaultValue = "15",
                    name = "time"
            )
                    int time
    ) {
        pickingTimeout = time;
        user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("option picking time ").a(C.G).a("to: ").a(C.B).a(String.valueOf(pickingTimeout)));
    }
    /**
     * The amount of time (in seconds) the user has to pick an option.
     */
    public long pickingTimeout;

    @StrInput(description = "Which threshold should be met for command matching using our improved N-Gram search algorithm?")
    public void setMatchThreshold(
            @Param(
                    description = "The match threshold",
                    defaultValue = "0.1",
                    name = "threshold"
            )
                    Double threshold
    ) {
        matchThreshold = threshold;
        user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("matching threshold ").a(C.G).a("to: ").a(C.B).a(String.valueOf(matchThreshold)));
    }

    /**
     * The threshold that should be met when matching using
     * {@link nl.codevs.strinput.system.util.NGram#ngramMatching(String, List)}.
     */
    public double matchThreshold = 0.1;

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Should users with the 'strinput' permission be able to use commands to change settings?")
    public void setSettingsCommands(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    Boolean enable
    ) {
        settingsCommands = enable == null ? !settingsCommands : enable;
        user().sendMessage(new Str(C.G).a("After a restart, ").a(C.B).a("settings commands ").a(C.G).a("will be: ").a(C.B).a(String.valueOf(settingsCommands)));
    }

    /**
     * Whether to allow users to send commands to change settings.
     */
    public boolean settingsCommands = true;

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Should commands be ran in async or sync (does not overwrite the 'sync' setting in individual StrInputs)")
    public void setAsync(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    Boolean enable
    ) {
        async = enable == null ? !async : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("async ").a(C.G).a("to: ").a(C.B).a(String.valueOf(async)));
    }

    public boolean async;

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "When entering arguments, should people be allowed to enter 'null'?")
    public void allowNullInput(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    Boolean enable
    ) {
        allowNullInput = enable == null ? !allowNullInput : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("allow null input ").a(C.G).a("to: ").a(C.B).a(String.valueOf(allowNullInput)));
    }

    public boolean allowNullInput = false;

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Whether to send debug messages or not")
    public void debug(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    Boolean enable
    ) {
        debug = enable == null ? !debug : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("debug ").a(C.G).a("to: ").a(C.B).a(String.valueOf(debug)));
    }

    public boolean debug = false;

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Whether to send debug messages on the time command running took")
    public void setDebugTime(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    Boolean enable
    ) {
        debugTime = enable == null ? !debugTime : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("debugTime ").a(C.G).a("to: ").a(String.valueOf(debugTime)));
    }

    public boolean debugTime;

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Whether to debug matching or not. This is also ran on tab completion.")
    public void debugMatching(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    Boolean enable
    ) {
        debugMatching = enable == null ? !debugMatching : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("debug matching ").a(C.G).a("to: ").a(String.valueOf(debugMatching)));
    }


    public boolean debugMatching = true;

    /**
     * Setting command.
     * @param enable whether to enable it or not
     */
    @StrInput(description = "Auto-pick the first option when multiple exist?")
    public void pickFirstOnMultiple(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle",
                    name = "enable"
            )
                    Boolean enable
    ) {
        pickFirstOnMultiple = enable == null ? !pickFirstOnMultiple : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.B).a("pick first on multiple ").a(C.G).a("to: ").a(String.valueOf(pickFirstOnMultiple)));
    }

    /**
     * Whether to pick the first option when multiple are available.
     * Effectively not giving the user the option to pick.
     */
    public boolean pickFirstOnMultiple = false;

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
     * @param file the file to read json from
     * @param console the console to send debug to
     * @return the new {@link StrSettings}
     */
    public static StrSettings fromConfigJson(
            @NotNull final File file,
            @NotNull final StrUser console
    ) {
        try {
            if (!file.exists() || file.length() == 0) {
                file.getParentFile().mkdirs();
                StrSettings newSettings = new StrSettings(file);
                FileWriter f = new FileWriter(file);
                GSON.toJson(newSettings, StrSettings.class, f);
                f.close();
                lastChanged = file.lastModified();
                console.sendMessage(new Str(C.G).a("Made new StrInput config (")
                        .a(C.B).a(file.getParent().replace("\\", "/")
                                + "/" + file.getName())
                        .a(C.G).a(")"));
                return newSettings;
            }
            console.sendMessage(new Str(C.G)
                    .a("Loaded existing StrInput config (")
                    .a(C.B).a(file.getParent().replace("\\", "/")
                            + "/" + file.getName()).a(C.G).a(")"));
            return new Gson().fromJson(new FileReader(file), StrSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save the config to {@link #settingsFile}.
     *
     * @param console the console to send debug messages to
     */
    public void saveToConfig(
            @NotNull final StrUser console
    ) {
        try {
            FileWriter f = new FileWriter(settingsFile);
            GSON.toJson(this, StrSettings.class, f);
            f.close();
            console.sendMessage(new Str(C.G).a("Saved StrInput Settings"));
            lastChanged = settingsFile.lastModified();
        } catch (IOException e) {
            console.sendMessage(new Str("Failed to save config: \n"
                    + GSON.toJson(this)));
            e.printStackTrace();
        }
    }

    /**
     * Hotload settings from file.
     *
     * @param console the console user to send debug to
     *
     * @return the new settings
     */
    public StrSettings hotload(
            @NotNull final StrUser console
    ) {

        // Load file
        StrSettings fileSettings = fromConfigJson(settingsFile, console);
        assert fileSettings != null;

        // File is newer
        if (lastChanged != settingsFile.lastModified()) {
            lastChanged = settingsFile.lastModified();
            console.sendMessage(new Str(C.G).a("Hotloaded StrInput Settings"));
            return fileSettings;
        }

        // In-memory settings are newer
        if (!fileSettings.equals(this)) {
            saveToConfig(console);
        }
        return this;
    }
}
