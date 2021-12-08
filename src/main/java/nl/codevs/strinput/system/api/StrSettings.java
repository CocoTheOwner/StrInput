package nl.codevs.strinput.system.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
public class StrSettings implements StrCategory {
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
    private static File file;

    /**
     * Debug message prefix.
     * Cannot be modified by commands.
     */
    public static Str debugPrefix = new Str(C.R).a("[")
            .a(C.G).a("StrInput")
            .a(C.R).a("] ")
            .a(C.X);

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

    public double matchThreshold = 0.1;

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

    public boolean settingsCommands = true;

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

    public boolean pickFirstOnMultiple = false;

    /**
     * Load a new StrInput file from json
     *
     * @param file    the file to read json from
     * @return the new {@link StrSettings}
     */
    public static StrSettings fromConfigJson(File file, StrUser console) {
        StrSettings.file = file;
        lastChanged = file.lastModified();
        try {
            if (!file.exists() || file.length() == 0) {
                file.getParentFile().mkdirs();
                StrSettings newSettings = new StrSettings();
                FileWriter f = new FileWriter(file);
                GSON.toJson(newSettings, StrSettings.class, f);
                f.close();
                console.sendMessage(new Str(C.G).a("Made new StrInput config (").a(C.B).a(file.getParent().replace("\\", "/") + "/" + file.getName()).a(C.G).a(")"));
                return newSettings;
            }
            console.sendMessage(new Str(C.G).a("Loaded existing StrInput config (").a(C.B).a(file.getParent().replace("\\", "/") + "/" + file.getName()).a(C.G).a(")"));
            return new Gson().fromJson(new FileReader(file), StrSettings.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Save the config to a file.
     *
     * @param file the file path
     * @param console the console to send debug messages to
     */
    public void saveToConfig(File file, StrUser console) {
        try {
            FileWriter f = new FileWriter(file);
            GSON.toJson(this, StrSettings.class, f);
            f.close();
            console.sendMessage(new Str(C.G).a("Saved StrInput Settings"));
            lastChanged = file.lastModified();
        } catch (IOException e) {
            console.sendMessage(new Str("Failed to save config: \n" + GSON.toJson(this)));
            e.printStackTrace();
        }
    }

    /**
     * Hotload settings from file
     *
     * @return the new settings
     */
    public StrSettings hotload(StrUser console) {

        // Load file
        StrSettings fileSettings = fromConfigJson(file, console);
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
