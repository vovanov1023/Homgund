package me.vovanov.homgund.Economy.commands;

import me.vovanov.homgund.Economy.files.ATMOperations;
import me.vovanov.homgund.discordBot;
import me.vovanov.homgund.Economy.files.EconomyUser;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.homgund.Economy.files.general.*;
import static me.vovanov.homgund.Homgund.PLUGIN;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class withdraw implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда может быть использована только игроками!", RED));
            return true;
        }
        String nick = player.getName();
        EconomyUser user = EconomyUser.getUser(player);
        if (user.hasNoBankAccount()){
            player.sendMessage(
                    text("Вам нужно зарегистрировать банковский счёт для использования этой команды", RED)
                            .append(text("\n(Обратитесь в ближайший банк для регистрации)", YELLOW))
            );
            return false;
        }
        if (!(ATMOperations.find(player.getLocation(), player))){
            return false;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")){
            player.sendMessage(text("Введите сумму которую нужно вывести со счёта", RED).append(text("\n/withdraw <сумма>", YELLOW)));
            return false;
        }
        int money;
        try {
            money = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(text("Значение не должно быть словом/дробным числом", RED));
            return false;
        }
        if (money < 1){
            player.sendMessage(text("Нельзя вывести меньше 1 "+curAl(), RED));
            return false;
        }
        int bal = user.getBalance();
        int newBal = bal - money;
        if (newBal < 0) {
            player.sendMessage(text("Недостаточно средств", RED).append(text("\n(На счету " + bal + " "+curAl()+")", YELLOW)));
            return false;
        }
        if (money > 6400) {
            player.sendMessage(text("Нельзя вывести больше 6400 "+curAl(), RED));
            return false;
        }
        String currency = curIt();
        Server server = PLUGIN.getServer();
        server.dispatchCommand(server.getConsoleSender(), "give "+nick+" "+currency.toLowerCase()+" "+money);

        player.sendMessage(text().append(text("Со счёта успешно снято ", BLUE), text(money, WHITE), text(" "+curAl(), BLUE),
                text("\nВаш текущий баланс: ", GOLD), text(newBal, WHITE)).build());
        user.removeFromBalance(money);
        discordBot.logEconomy(nick + " вывел со счёта " + money + " "+curAl()+"\nЕго текущий баланс: " + newBal+" "+curAl());
        discordBot.sendDirect("Вы сняли "+money+" "+curAl()+" со своего счёта\nВаш текущий баланс: "+newBal+" "+curAl(), player);
        return true;
    }
}
