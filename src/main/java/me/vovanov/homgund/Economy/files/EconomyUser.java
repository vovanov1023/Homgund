package me.vovanov.homgund.Economy.files;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static me.vovanov.homgund.Homgund.PLUGIN;

public class EconomyUser {

    private static final File dataFolder = PLUGIN.getDataFolder();
    private static final File playerDataFolder = new File(dataFolder, "playerData");

    private int balance;
    private boolean bankAccount;
    private final File playerDataFile;
    private final FileConfiguration playerData;
    private final OfflinePlayer player;

    private EconomyUser(File playerDataFile, OfflinePlayer player) {
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        this.player = player;
        this.playerData = playerData;
        this.playerDataFile = playerDataFile;
        this.balance = playerData.getInt("balance");
        this.bankAccount = playerData.getBoolean("bankAccount");
    }

    public static EconomyUser getUser(OfflinePlayer player) {
        if (player == null) return null;
        String name = player.getName();
        if (name == null || name.isBlank()) return null;
        File playerDataFile = new File(playerDataFolder, name + ".yml");
        if (!playerDataFile.exists()) return null;
        return new EconomyUser(playerDataFile, player);
    }

    public int getBalance() {
        return this.balance;
    }

    public void addToBalance(int toAdd) {
        this.balance += toAdd;
        saveData("balance", this.balance);
    }

    public void removeFromBalance(int toRemove) {
        this.balance -= toRemove;
        saveData("balance", this.balance);
    }

    public boolean hasNoBankAccount() {
        return !this.bankAccount;
    }

    public void setBankAccount(boolean toSet) {
        this.bankAccount = toSet;
        saveData("bankAccount", toSet);
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    private void saveData(String data, Object value) {
        this.playerData.set(data, value);
        try {
            this.playerData.save(this.playerDataFile);
        } catch (IOException e) {
            PLUGIN.getLogger().severe("Произошла ошибка во время сохранения данных "
                    + this.playerDataFile.getName().replace(".yml", "")+": " + e.getMessage()
                    + "\nПричина: " + e.getCause());
        }
    }

    public static void newFile(String nick) {
        File playerDataFile = new File(playerDataFolder, nick + ".yml");
        try {
            createNewDataFile(playerDataFile);
        } catch (IOException e) {
            PLUGIN.getLogger().severe("Произошла ошибка во время создания файла данных "
                    +nick+": " + e.getMessage() + "\nПричина: " + e.getCause());
        }
    }

    private static void createNewDataFile(File playerDataFile) throws IOException {
        if (playerDataFile.createNewFile()) return;
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        playerData.addDefault("bankAccount", false);
        playerData.addDefault("balance", 0);
        playerData.options().copyDefaults(true);

        playerData.save(playerDataFile);
    }

    public static void createPlayerDataFolder() {
        if (playerDataFolder.mkdir()) PLUGIN.getLogger().info("Папка для данных создана");
    }
}
