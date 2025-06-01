package me.vovanov.homgund.Economy.commands;

import me.vovanov.homgund.Economy.files.EconomyUser;
import org.bukkit.OfflinePlayer;
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
            EconomyUser user = EconomyUser.getUser(player);
            if (user == null) return false;
            if (user.hasNoBankAccount()) {
                player.sendMessage(
                        text("Вам нужно зарегистрировать банковский счёт для использования этой команды", RED)
                        .append(text("\n(Обратитесь в ближайший банк для регистрации)", YELLOW))
                );
                return false;
            }

            player.sendMessage(text("Ваш текущий баланс: " + user.getBalance() + " АР", YELLOW));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(text("Недостаточно аргументов", RED));
            return true;
        }
        if (args[0].equalsIgnoreCase("give")) {
            String name = args[1];
            OfflinePlayer player = PLUGIN.getServer().getOfflinePlayer(name);
            EconomyUser user = EconomyUser.getUser(player);
            if (user == null) {
                sender.sendMessage(text("Этого игрока не существует", RED));
                return true;
            }

            int money = Integer.parseInt(args[2]);
            user.addToBalance(money);
            int newBal = user.getBalance();
            PLUGIN.getLogger().info("Баланс " + name + " пополнен на " + money+"\nТекущий баланс: "+newBal);
            return true;
        }

        return true;
    }
}