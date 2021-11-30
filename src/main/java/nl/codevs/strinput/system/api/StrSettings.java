package nl.codevs.strinput.system.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@StrInput(description = "StrInput settings", aliases = "stri", name = "strinput", permission = "strinput")
public class StrSettings implements StrCategory {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static long lastChanged;
    private static File file;

    @StrInput(description = "When entering arguments, should people be allowed to enter 'null'?")
    public void allowNullInput(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle"
            )
                    Boolean enable
    ){
        StrCenter.settings.allowNullInput = enable == null ? !StrCenter.settings.allowNullInput : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.GOLD).a("allow null input ").a(C.G).a("to: ").a(C.GOLD).a(String.valueOf(StrCenter.settings.allowNullInput)));
    }
    public boolean allowNullInput = false;

    @StrInput(description = "Whether to send debug messages or not")
    public void debug(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle"
            )
                    Boolean enable
    ){
        StrCenter.settings.debug = enable == null ? !StrCenter.settings.debug : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.GOLD).a("debug ").a(C.G).a("to: ").a(C.GOLD).a(String.valueOf(StrCenter.settings.debug)));
    }
    public boolean debug = false;

    @StrInput(description = "Whether to send debug messages on startup or not")
    public void debugStartup(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle"
            )
                    Boolean enable
    ){
        StrCenter.settings.debugStartup = enable == null ? !StrCenter.settings.debugStartup : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.GOLD).a("debugRuntime ").a(C.G).a("to: ").a(String.valueOf(StrCenter.settings.debugStartup)));
    }
    public boolean debugStartup;

    @StrInput(description = "Whether to debug matching or not. This is also ran on tab completion.")
    public void debugMatching(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle"
            )
                    Boolean enable
    ){
        StrCenter.settings.debugMatching = enable == null ? !StrCenter.settings.debugMatching : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.GOLD).a("debug matching ").a(C.G).a("to: ").a(String.valueOf(StrCenter.settings.debugMatching)));
    }
    public boolean debugMatching = true;

    @StrInput(description = "On argument parsing fail, pass 'null' instead. Can break argument parsing, best to leave 'false'", permission = "settings")
    public void nullOnFailure(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle"
            )
                    Boolean enable
    ){
        StrCenter.settings.nullOnFailure = enable == null ? !StrCenter.settings.nullOnFailure : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.GOLD).a("null on failure ").a(C.G).a("to: ").a(String.valueOf(StrCenter.settings.nullOnFailure)));
    }
    public boolean nullOnFailure = false;

    @StrInput(description = "Auto-pick the first option when multiple exist?")
    public void pickFirstOnMultiple(
            @Param(
                    description = "Whether to set this setting to true or false",
                    defaultValue = "toggle"
            )
                    Boolean enable
    ){
        StrCenter.settings.pickFirstOnMultiple = enable == null ? !StrCenter.settings.pickFirstOnMultiple : enable;
        user().sendMessage(new Str(C.G).a("Set ").a(C.GOLD).a("pick first on multiple ").a(C.G).a("to: ").a(String.valueOf(StrCenter.settings.pickFirstOnMultiple)));
    }
    public boolean pickFirstOnMultiple = false;

    @StrInput(description = "Command prefix")
    public void decreePrefix(
            @Param(
                    description = "The prefix to have Decree debug with",
                    defaultValue = "§c[§aDecree§c]§r"
            )
                    String prefix
    ){
        StrCenter.settings.prefix = new Str(prefix);
        user().sendMessage(new Str(C.G).a("Set ").a(C.GOLD).a("decree prefix ").a(C.G).a("to: ").a(String.valueOf(StrCenter.settings.prefix)));
    }
    public Str prefix = new Str(C.R).a("[").a(C.G).a("Decree").a(C.R).a("]").a(C.RESET);

    /**
     * Load a new decree Decrees file from json
     * @param file the file to read json from
     * @param console the console sender
     * @return the new {@link StrSettings}
     */
    public static StrSettings fromConfigJson(File file, StrUser console) {
        StrSettings.file = file;
        lastChanged = file.lastModified();
        try {
            if (!file.exists() || file.length() == 0) {
                file.getParentFile().mkdirs();
                StrSettings new_ = new StrSettings();
                FileWriter f = new FileWriter(file);
                gson.toJson(new_, StrSettings.class, f);
                f.close();
                console.sendMessage(new Str(C.G).a("Made new Decree config (").a(C.Y).a(file.getParent().replace("\\", "/")  + "/" + file.getName()).a(C.G).a(")"));
                return new_;
            }
            console.sendMessage(new Str(C.G).a("Loaded existing Decree config (").a(C.Y).a(file.getParent().replace("\\", "/") + "/" + file.getName()).a(C.G).a(")"));
            return new Gson().fromJson(new FileReader(file), StrSettings.class);
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
            gson.toJson(this, StrSettings.class, f);
            f.close();
            console.sendMessage(new Str(C.G + "Saved Decree Decrees"));
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
    public StrSettings hotload(StrUser console) {

        // Load file
        StrSettings fileSettings = fromConfigJson(file, console);
        assert fileSettings != null;

        // File is newer
        if (lastChanged != file.lastModified()) {
            lastChanged = file.lastModified();
            console.sendMessage(new Str(C.G).a("Hotloaded Decree Settings"));
            return fileSettings;
        }

        // In-memory settings are newer
        if (!fileSettings.equals(this)) {
            saveToConfig(file, console);
        }
        return this;
    }
}
