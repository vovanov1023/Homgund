package me.vovanov.homgund.Economy.commands;

import me.vovanov.homgund.Economy.files.playerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class balance implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (sender instanceof Player player) {
            String nick = player.getName();
            playerData.setup(nick);
            if (!(playerData.get().getBoolean("bankAccount"))){
                player.sendMessage(
                        text("Вам нужно зарегистрировать банковский счёт для использования этой команды", RED)
                        .append(text("\n(Обратитесь в ближайший банк для регистрации)", YELLOW))
                );
                return false;
            }

            if (args.length == 0) {
                player.sendMessage(text("Ваш текущий баланс: " + playerData.get().getInt("balance") + " АР", YELLOW));
                return true;
            } else if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage(text("Показывает текущий баланс на счету", YELLOW));
                return false;
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("give")) {
            String plr = args[1];
            double money = Double.parseDouble(args[2]);
            if (playerData.setup(plr)) {
                PLUGIN.getLogger().info("Баланс " + plr + " пополнен на " + money);
                playerData.get().set("balance", money + playerData.get().getInt("balance"));
                playerData.save();
                return true;
            } else {
                PLUGIN.getLogger().info("Этого игрока не существует");
                return false;
            }
        }
        return false;
    }
}