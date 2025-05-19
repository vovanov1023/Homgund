package me.vovanov.homgund.misc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class Teleport implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда доступна только игрокам", RED));
            return true;
        }
        if (args.length == 1) {
            Player toTeleport = PLUGIN.getServer().getPlayerExact(args[0]);
            if (toTeleport == null) {
                sender.sendMessage(text("Этого игрока не существует", RED));
                return true;
            }
            player.teleport(toTeleport);
            player.sendMessage(text("Телепортировано к "+args[0]));
            return true;
        }
        if (args.length >= 3) {
            double x;
            double y;
            double z;
            try {
                x = Double.parseDouble(args[0]);
                y = Double.parseDouble(args[1]);
                z = Double.parseDouble(args[2]);
            } catch (NumberFormatException ignore) {
                sender.sendMessage(text("Значение не должно быть словом", RED));
                return false;
            }
            Location toTeleport = new Location(player.getWorld(), x, y, z);
            player.teleport(toTeleport);
            player.sendMessage(text("Телепортировано в "+x+" "+y+" "+z));
            return true;
        }
        return false;
    }
}
