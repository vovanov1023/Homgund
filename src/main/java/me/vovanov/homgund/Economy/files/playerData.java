package me.vovanov.homgund.Economy.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static me.vovanov.homgund.Homgund.PLUGIN;

public class playerData {

    private static File dataFileInstance;
    private static FileConfiguration dataFileContents;
    private static final File dataFolder = PLUGIN.getDataFolder();
    private static final File playerDataFolder = new File(dataFolder, "playerData");

    public static void newFile(String nick) {
        dataFileInstance = new File(playerDataFolder, nick + ".yml");

        if (!dataFileInstance.exists()) {
            try {
                dataFileInstance.createNewFile();
                dataFileContents = YamlConfiguration.loadConfiguration(dataFileInstance);
                dataFileContents.addDefault("bankAccount", false);
                dataFileContents.addDefault("balance", 0);
                dataFileContents.options().copyDefaults(true);
                playerData.save();
                return;
            } catch (IOException e) {
                PLUGIN.getLogger().severe("Произошла ошибка во время создания файла данных " + nick);
                e.printStackTrace();
                return;
            }
        }
        dataFileContents = YamlConfiguration.loadConfiguration(dataFileInstance);
    }

    public static void newFolder(){
        File playerDataFolder = new File(dataFolder, "playerData");
        playerDataFolder.mkdir();
    }

    public static boolean setup(String nick) {
        if (nick == null || nick.isEmpty() ) {
            return false;
        }
        dataFileInstance = new File(playerDataFolder, nick + ".yml");

        if (!dataFileInstance.exists()) {
            return false;
        } else {
            dataFileContents = YamlConfiguration.loadConfiguration(dataFileInstance);
            return true;
        }
    }

    public static FileConfiguration get() {return dataFileContents;}

    public static void save() {
        try {
            dataFileContents.save(dataFileInstance);
        } catch (IOException e) {
            PLUGIN.getLogger().severe("Произошла ошибка во время сохранения файла");
            e.printStackTrace();
        }
    }
}
