package me.vovanov.homgund.Social.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.vovanov.homgund.Social.formattedNicknameGetter.getFormattedName;
import static me.vovanov.homgund.Social.utils.getPlayersNearList;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.UNDERLINED;

public class tryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда доступна только игрокам", RED));
            return true;
        }

        String action = String.join(" ", args);
        Component name = getFormattedName(player, false, false);
        Component result = round(random())==1 ? text("УСПЕХ", GREEN, UNDERLINED) : text("ПРОВАЛ", RED, UNDERLINED);
        Component message = text().append(text("* "), name, text(" "), text(action), text(" ("), result, text(")", WHITE).decoration(UNDERLINED, false)).build();

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
