package me.exejar.stathead.champstats.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.exejar.stathead.utils.ChatUtils;
import me.exejar.stathead.champstats.utils.Handler;
import me.exejar.stathead.utils.References;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.HashMap;

import static me.exejar.stathead.champstats.config.ModConfigNames.*;
import static me.exejar.stathead.utils.References.VERSION;


public class ModConfig {

    private String apiKey, statMode, statName;
    private static ModConfig instance;

    public static ModConfig getInstance() {
        if (instance == null) instance = new ModConfig();
        return instance;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String key) {
        this.apiKey = key;
    }

    public String getStatMode() { return this.statMode; }

    public String getStatName() { return this.statName; }

    public void setStatMode(String mode) { this.statMode = mode; }

    public void setStatName(String name) { this.statName = name; }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void makeFile() {
        try {
            if (!getFile().exists()) {
                getFile().getParentFile().mkdir();
                getFile().createNewFile();
                try (FileWriter writer = new FileWriter(getFile())) {
                    writer.write("{}");
                    writer.flush();
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfigFromFile() {
        if (!getFile().exists()) makeFile();
        apiKey = getString(APIKEY);
        this.statMode = getString(STATMODE);
        this.statName = getString(STATNAME);
    }

    public File getFile() {
        String here = Paths.get("").toAbsolutePath().toString();
        String file = here + File.separator + References.MODID + File.separator + "config.json";
        return new File(file);
    }

    public void init() {
        loadConfigFromFile();
    }

    public void save() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(APIKEY.toString(), getApiKey());
        map.put(STATMODE.toString(), getStatMode());
        map.put(STATNAME.toString(), getStatName());
        try (Writer writer = new FileWriter(getFile())) {
            Handler.getGson().toJson(map, writer);
        } catch (Exception e) {
            System.out.println("Unable to write to Config File!");
            e.printStackTrace();
            ChatUtils.sendMessage("UNABLE TO SAVE CONFIG! Severe error with Mod version: " + VERSION + ", You should report this!");
        }
    }

    public String getString(ModConfigNames key) {
        JsonParser parser = new JsonParser();
        String s = "";
        try {
            JsonObject object = (JsonObject) parser.parse(new FileReader(getFile()));
            s = object.get(key.toString()).getAsString();
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

}
