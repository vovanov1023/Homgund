package me.vovanov.homgund.Social.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import java.util.List;

import static me.vovanov.homgund.Social.formattedNicknameGetter.getFormattedName;
import static me.vovanov.homgund.Social.utils.getPlayersNearList;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class meCommand implements Listener, CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда доступна только игрокам", NamedTextColor.RED));
            return true;
        }

        Component name = getFormattedName(player, false, false);
        String action = String.join(" ", args);
        Component message = text().append(text("* "), name, text(" "), text(action)).build();

        if (action.isBlank()) return false;

        List<Player> playersNear = getPlayersNearList(player);
        if (playersNear.isEmpty()){
            player.sendMessage(message.append(text("\n...вокруг вас никого", RED)));
            return true;
        }

        for (Player checkedPlayer : playersNear) checkedPlayer.sendMessage(message);
        player.sendMessage(message);
        return true;
    }
}
