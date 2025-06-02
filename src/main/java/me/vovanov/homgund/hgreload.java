package me.vovanov.homgund;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.homgund.Homgund.PLUGIN;

public class hgreload implements CommandExecutor {
    static FileConfiguration config;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        PLUGIN.reloadConfig();
        reload();
        PLUGIN.getLogger().info("Файл конфигурации был перезагружен");
        return false;
    }
    public static void reload(){
        config = PLUGIN.getConfig();
        config.options().copyDefaults(true);
        PLUGIN.saveDefaultConfig();
        try {
            new discordBot().initialiseBot(config.getString("bot-token"), config.getString("channel-id"));
        }
        catch (Exception e) {
            PLUGIN.getLogger().warning("Что-то пошло не так!\nВозможно, введённые в файл конфигурации токен бота и айди канала недействительны");
            PLUGIN.getLogger().warning(e.getMessage());
        }
    }
}
