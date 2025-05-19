package me.vovanov.homgund.Social.Ignore;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static me.vovanov.homgund.Homgund.PLUGIN;

public class IgnoreImpl {
    private static ConfigurationSection ignoringPlayers;
    private static ConfigurationSection ignoredPlayers;
    private static final File dataFolder = PLUGIN.getDataFolder();
    private static FileConfiguration fileContents;
    private static final File file = new File(dataFolder, "Ignore.yml");

    public static void newFile() {
        try {
            if(!file.exists()) file.createNewFile();
            fileContents = YamlConfiguration.loadConfiguration(file);

            ignoredPlayers = fileContents.getConfigurationSection("ignoredPlayers") != null ?
                    fileContents.getConfigurationSection("ignoredPlayers") :
                    fileContents.createSection("ignoredPlayers");

            ignoringPlayers = fileContents.getConfigurationSection("ignoringPlayers") != null ?
                    fileContents.getConfigurationSection("ignoringPlayers") :
                    fileContents.createSection("ignoringPlayers");

        } catch (IOException e) {e.printStackTrace();}
    }

    public static void save() {
        try {
            fileContents.set("ignoredPlayers", ignoredPlayers);
            fileContents.set("ignoringPlayers", ignoringPlayers);
            fileContents.save(file);
        } catch (IOException e) {e.printStackTrace();}
    }

    /**Возвращает список игроков, которых игнорирует указанный*/
    public static List<String> getIgnoredPlayers(String uuid) {
        return ignoringPlayers.getStringList(uuid);
    }

    /**Возвращает список игроков, которые игнорируют указанного*/
    public static List<String> getIgnoringPlayers(String uuid) {
        return ignoredPlayers.getStringList(uuid);
    }

    public static void ignorePlayer(String ignorerPlayerUUID, String ignoredPlayerUUID) {
        List<String> list = ignoredPlayers.getStringList(ignoredPlayerUUID);
        list.add(ignorerPlayerUUID);
        ignoredPlayers.set(ignoredPlayerUUID, list);

        list = ignoringPlayers.getStringList(ignorerPlayerUUID);
        list.add(ignoredPlayerUUID);
        ignoringPlayers.set(ignorerPlayerUUID, list);
    }

    public static void unignorePlayer(String ignorerPlayerUUID, String ignoredPlayerUUID) {
        List<String> list = ignoredPlayers.getStringList(ignoredPlayerUUID);
        list.remove(ignorerPlayerUUID);
        list = list.isEmpty() ? null : list;
        ignoredPlayers.set(ignoredPlayerUUID, list);

        list = ignoringPlayers.getStringList(ignorerPlayerUUID);
        list.remove(ignoredPlayerUUID);
        list = list.isEmpty() ? null : list;
        ignoringPlayers.set(ignorerPlayerUUID, list);
    }
}
