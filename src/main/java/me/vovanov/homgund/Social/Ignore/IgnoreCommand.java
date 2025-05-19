package me.vovanov.homgund.Social.Ignore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static me.vovanov.homgund.Social.Ignore.IgnoreImpl.getIgnoredPlayers;
import static me.vovanov.homgund.Social.Ignore.IgnoreImpl.ignorePlayer;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class IgnoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда доступна лишь игрокам!", RED));
            return true;
        }
        if (args.length == 0 || args[0] == null || args[0].isBlank()) return false;

        String ignoredPlayerName = args[0];
        String ignorerName = player.getName();

        if (ignorerName.equals(ignoredPlayerName)) {
            sender.sendMessage(text("Нельзя игнорировать самого себя", RED));
            return true;
        }

        UUID ignoredUUID = Bukkit.getPlayerUniqueId(ignoredPlayerName);
        if (ignoredUUID == null) {
            player.sendMessage(text("Такого игрока не существует", RED));
            return true;
        }
        String ignorerUUID = player.getUniqueId().toString();

        List<String> list = getIgnoredPlayers(ignorerUUID);
        if (list.contains(ignoredUUID.toString())) {
            player.sendMessage(text("Вы уже игнорируете этого игрока", RED));
            return true;
        }
        ignorePlayer(ignorerUUID, ignoredUUID.toString());
        player.sendMessage(text("Теперь вы игнорируете сообщения "+ignoredPlayerName, GREEN));
        return true;
    }

}
