package me.vovanov.homgund.Economy.commands;

import me.vovanov.homgund.Economy.files.ATMOperations;
import me.vovanov.homgund.discordBot;
import me.vovanov.homgund.Economy.files.playerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.homgund.Economy.files.general.*;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class put implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда может быть использована только игроками!", RED));
            return true;
        }
        String nick = player.getName();
        playerData.setup(nick);
        if (!(playerData.get().getBoolean("bankAccount"))){
            player.sendMessage(
                    text("Вам нужно зарегистрировать банковский счёт для использования этой команды", RED)
                            .append(text("\n(Обратитесь в ближайший банк для регистрации)", YELLOW))
            );
            return false;
        }
        if (!(ATMOperations.find(player.getLocation(), player))){
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            player.sendMessage(text("Введите сумму которую нужно положить на счёт", RED).append(text("\n/put <сумма>", YELLOW)));
            return false;
        }
        int money;
        try {
            money = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(text("Значение не должно быть словом/дробным числом", RED));
            return false;
        }
        if (money < 1) {
            player.sendMessage(text("Нельзя положить меньше 1 "+curAl(), RED));
            return false;
        }
        int bal = playerData.get().getInt("balance");
        int newBal = money+bal;
        if (giveAR(player, money)) {
            player.sendMessage(text().append(text("Ваш счёт успешно пополнен на ", GOLD), text(money, WHITE), text(" "+curAl(), GOLD),
                    text("\nВаш текущий баланс: ", GOLD), text(newBal, WHITE)).build());
            discordBot.logEconomy(nick + " положил на счёт " + money + " "+curAl()+"\nЕго текущий баланс: " + newBal+" "+curAl());
            discordBot.sendDirect("Вы положили "+money+" "+curAl()+" на свой счёт\nВаш текущий баланс: "+newBal+" "+curAl(), player);
            return true;
        }
        return false;
    }
}
