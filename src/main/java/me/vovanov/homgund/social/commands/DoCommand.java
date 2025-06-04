package me.vovanov.homgund.social.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.List;

import static me.vovanov.homgund.social.Utils.getPlayersNearList;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class DoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда доступна только игрокам", RED));
            return true;
        }

        String action = String.join(" ", args);
        Component message = text("* "+action);

        if (action.isBlank()) return false;

        List<Player> playersNear = getPlayersNearList(player);
        if (playersNear.isEmpty()) {
            player.sendMessage(message.append(text("\n...вокруг вас никого", RED)));
            return true;
        }

        for (Player checkedPlayer : playersNear) checkedPlayer.sendMessage(message);
        player.sendMessage(message);
        return true;
    }
}
