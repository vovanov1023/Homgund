package me.vovanov.homgund.social.playersit;

import dev.geco.gsit.api.event.PrePlayerPlayerSitEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class DenySit implements CommandExecutor, Listener {
    public static List<String> denySit = new ArrayList<>();
    private static final File dataFolder = PLUGIN.getDataFolder();
    private static FileConfiguration fileContents;
    private static final File file = new File(dataFolder, "DeniedSitting.yml");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда может быть использована только игроками!", RED));
            return true;
        }
        String uuid = player.getUniqueId().toString();
        if (denySit.contains(uuid)) {
            player.sendMessage(text("На вас и так не могут садится игроки", RED));
            return true;
        }
        denySit.add(uuid);
        player.sendMessage(text("На вас больше не могут садится игроки", GREEN));
        return false;
    }

    @EventHandler
    public void onPlayerSit(PrePlayerPlayerSitEvent event) {
        Player player = event.getPlayer();
        Player target = event.getTarget();
        if (denySit.contains(target.getUniqueId().toString())) {
            player.sendMessage(text("Вы не можете сесть на этого игрока!", RED));
            event.setCancelled(true);
        }
    }

    public static void newFile() {
        try {
            if (file.createNewFile()) PLUGIN.getLogger().info("Создан файл хранения для запрета сидения");
            fileContents = YamlConfiguration.loadConfiguration(file);
            denySit = fileContents.getStringList("list");
        } catch (IOException e) {
            PLUGIN.getLogger().severe("Произошла ошибка во время создания файла хранения для запрета сидения: "
                    +e.getMessage() + "\nПричина: " + e.getCause());
        }
    }

    public static void save() {
        try {
            fileContents.set("list", denySit);
            fileContents.save(file);
        } catch (IOException e) {
            PLUGIN.getLogger().severe("Произошла ошибка во время сохранения файла хранения для для запрета сидения: "
                    +e.getMessage() + "\nПричина: " + e.getCause());
        }
    }
}
