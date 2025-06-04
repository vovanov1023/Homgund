package me.vovanov.homgund.social.playersit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.homgund.social.playersit.DenySit.denySit;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class AllowSit implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда может быть использована только игроками!", RED));
            return true;
        }
        if (!denySit.remove(player.getUniqueId().toString())) {
            player.sendMessage(text("На вас и так могут садится игроки", RED));
            return true;
        }
        player.sendMessage(text("На вас вновь могут садится игроки", RED));
        return true;
    }
}
