package net.ironingot.kanachat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KanaChatDictionary {
    private static final String FILE_NAME = "dictionary.yml";
    private static final String LEGACY_FILE_NAME = "dictionary.xlsx";
    private static final String ENTRIES_PATH = "entries";

    private final JavaPlugin plugin;
    private final File file;
    private final Map<String, List<String>> entries = new LinkedHashMap<>();

    public KanaChatDictionary(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), FILE_NAME);
        warnAboutLegacyDictionary();
        load();
        migrateLegacyDictionary();
    }

    public synchronized Map<String, String> getEntries() {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : entries.entrySet()) {
            result.put(entry.getKey(), String.join(", ", entry.getValue()));
        }
        return result;
    }

    public synchronized Map<String, String> getValues() {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : entries.entrySet()) {
            for (String reading : entry.getValue()) {
                result.put(reading.toLowerCase(), entry.getKey());
            }
        }
        return result;
    }

    public synchronized List<String> getWords() {
        return new ArrayList<>(entries.keySet());
    }

    public synchronized boolean remove(String word) {
        if (entries.remove(word) == null) {
            return false;
        }
        save();
        return true;
    }

    public synchronized void set(String word, String[] readings) {
        entries.put(word, new ArrayList<>(Arrays.asList(readings)));
        save();
    }

    private void load() {
        if (!file.exists()) {
            save();
            return;
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        for (Map<?, ?> serializedEntry : configuration.getMapList(ENTRIES_PATH)) {
            Object wordValue = serializedEntry.get("word");
            Object readingsValue = serializedEntry.get("readings");
            if (!(wordValue instanceof String) || !(readingsValue instanceof Collection)) {
                continue;
            }

            List<String> readings = new ArrayList<>();
            for (Object reading : (Collection<?>) readingsValue) {
                if (reading instanceof String && !((String) reading).trim().isEmpty()) {
                    readings.add(((String) reading).trim());
                }
            }
            if (!readings.isEmpty()) {
                entries.put((String) wordValue, readings);
            }
        }
    }

    private void save() {
        try {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new IOException("Failed to create directory " + file.getParentFile());
            }

            List<Map<String, Object>> serializedEntries = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : entries.entrySet()) {
                Map<String, Object> serializedEntry = new LinkedHashMap<>();
                serializedEntry.put("word", entry.getKey());
                serializedEntry.put("readings", new ArrayList<>(entry.getValue()));
                serializedEntries.add(serializedEntry);
            }

            YamlConfiguration configuration = new YamlConfiguration();
            configuration.set(ENTRIES_PATH, serializedEntries);
            configuration.save(file);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to save " + file, exception);
        }
    }

    private void migrateLegacyDictionary() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("dictionary");
        if (section == null || section.getKeys(false).isEmpty()) {
            return;
        }

        for (String word : section.getKeys(false)) {
            List<String> readings = section.getStringList(word);
            if (!readings.isEmpty() && !entries.containsKey(word)) {
                entries.put(word, new ArrayList<>(readings));
            }
        }
        save();
        plugin.getConfig().set("dictionary", null);
        plugin.saveConfig();
    }

    private void warnAboutLegacyDictionary() {
        File legacyFile = new File(plugin.getDataFolder(), LEGACY_FILE_NAME);
        if (!file.exists() && legacyFile.exists()) {
            plugin.getLogger().warning("Found legacy " + LEGACY_FILE_NAME
                    + ". Convert it to " + FILE_NAME + " before using the dictionary.");
        }
    }
}
