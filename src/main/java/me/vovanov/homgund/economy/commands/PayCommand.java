package me.vovanov.homgund.economy.commands;

import me.vovanov.homgund.economy.files.EconomyUser;
import me.vovanov.homgund.DiscordBot;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.homgund.economy.files.HEconomy.curAl;
import static me.vovanov.homgund.Homgund.PLUGIN;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class PayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда может быть использована только игроками!", RED));
            return true;
        }
        String senderName = sender.getName();
        EconomyUser senderUser = EconomyUser.getUser(player);
        if (senderUser.hasNoBankAccount()) {
            sender.sendMessage(
                    text("Вам нужно зарегистрировать банковский счёт для использования этой команды", RED)
                            .append(text("\n(Обратитесь в ближайший банк для регистрации)", YELLOW))
            );
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(text("Недостаточно аргументов!", RED));
            return false;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(text("Значение не должно быть словом/дробным числом", RED));
            return false;
        }
        if (amount < 1) {
            sender.sendMessage(text("Нельзя отправить меньше 1 "+curAl(), RED));
            return true;
        }

        String getterName = args[0].replace(".", "");
        if (getterName.equals(senderName)) {
            sender.sendMessage(text("Нельзя отправлять средства самому себе", RED));
            return true;
        }

        OfflinePlayer getter = PLUGIN.getServer().getOfflinePlayer(getterName);
        EconomyUser getterUser = EconomyUser.getUser(getter);

        if (getterUser == null) {
            sender.sendMessage(text("Такого игрока не существует", RED));
            return false;
        }

        if (getterUser.hasNoBankAccount()) {
            sender.sendMessage(text("У получателя нет банковского счёта", RED));
            return true;
        }

        int bal = senderUser.getBalance();
        int newBal = bal - amount;
        if (newBal < 0) {
            sender.sendMessage(text("Недостаточно средств", RED).append(text("\n(На счету " + bal + " "+curAl()+")", YELLOW)));
            return true;
        }

        senderUser.removeFromBalance(amount);

        int getterBal = getterUser.getBalance();
        int newGetterBal = getterBal + amount;

        getterUser.addToBalance(amount);

        sender.sendMessage(text().append(text("Успешно отправлено ", GREEN), text(amount +" ", WHITE), text(curAl() + " игроку ", GREEN),
                text(getterName, WHITE), text("\nВаш текущий баланс: ", GOLD), text(newBal, WHITE)).build());

        if (getter.getPlayer() != null)
            getter.getPlayer().sendMessage(text().append(text(senderName, WHITE), text(" отправил вам ", GREEN), text(amount +" ", WHITE),
                    text(curAl(), GREEN), text("\nВаш текущий баланс: ", GOLD), text(newGetterBal, WHITE)).build());

        DiscordBot.sendDirect(senderName+" отправил вам "+amount+" "+curAl()+"\nВаш текущий баланс: "+newGetterBal+" "+curAl(), getter);

        DiscordBot.sendDirect("Вы отправили "+getterName+" "+amount+" "+curAl()+"\nВаш текущий баланс: "+newBal+" "+curAl(), player);

        DiscordBot.logEconomy(senderName+" отправил "+amount+" "+curAl()+" "+getterName+
                "\nТекущий баланс получателя: "+newGetterBal+
                "\nТекущий баланс отправителя: "+newBal);
        return true;
    }
}
