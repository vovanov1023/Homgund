package me.vovanov.homgund.Economy.commands;

import me.vovanov.homgund.Economy.files.creditsHandler;
import me.vovanov.homgund.discordBot;
import me.vovanov.homgund.Economy.files.EconomyUser;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.vovanov.homgund.Economy.files.general.curAl;
import static me.vovanov.homgund.Economy.files.general.giveAR;
import static me.vovanov.homgund.Homgund.PLUGIN;
import static me.vovanov.homgund.discordBot.sendDirect;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.bukkit.util.NumberConversions.ceil;

public class bank implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        String[] subcom = {"help", "create", "credit", "credits", "paycredit", "debtors"};
        if (args.length == 0 || args[0].equalsIgnoreCase("help") || !(Arrays.asList(subcom).contains(args[0]))){
            sender.sendMessage(text().append(
                    text("Основная комманда для предоставления банковских услуг\n \n", YELLOW),
                    
                    text("/bank create <никнейм>", YELLOW), text(" - создаёт банковский счёт указанному игроку\n", WHITE),
                    text("/bank credit <никнейм> <сумма> <процентная ставка> <период выплаты>", YELLOW), text(" - выдаёт кредит указанному игроку\n", WHITE),
                    text("/bank credits <никнейм>", YELLOW), text(" - выводит список кредитов у указанного игрока\n", WHITE),
                    text("/bank paycredit <никнейм> <индекс> <сумма>", YELLOW), text(" - уменьшает задолженость игрока\n", WHITE),
                    text("/bank debtors", YELLOW), text(" - показывает список игроков с сорванными сроками выплаты кредита", WHITE)
                    ).build()
            );
            return false;
        }
        if (args[0].equalsIgnoreCase("debtors")){
            creditsHandler.getDebtorsList(sender);
            return true;
        }

        String getterName = args.length >= 2 ? args[1].replace(".", "") : null;
        if (getterName == null) {
            sender.sendMessage(text("Введите никнейм", RED));
            return true;
        }
        OfflinePlayer offlineGetter = PLUGIN.getServer().getOfflinePlayer(getterName);
        EconomyUser user = EconomyUser.getUser(offlineGetter);

        if (user == null) {
            sender.sendMessage(text("Этого игрока не существует", RED));
            return false;
        }

        if (args[0].equalsIgnoreCase("credits")) {
            creditsHandler.creditsList(getterName, sender);
            return true;
        }

        Player getter;
        boolean isGetterOnline = offlineGetter.isConnected();
        if (isGetterOnline) {
            getter = (Player) offlineGetter;
        } else {
            sender.sendMessage(text("Игрок должен быть онлайн", RED));
            return true;
        }
        String senderName = sender.getName();

        if (args[0].equalsIgnoreCase("create")){

            if (!user.hasNoBankAccount()) {
                sender.sendMessage(text().append(text("У ", RED), text(getterName, WHITE), text(" уже есть банковский счёт", RED)).build());
                return false;
            }

            user.setBankAccount(true);
            sender.sendMessage(text().append(text("Счёт для ", GOLD), text(getterName, WHITE), text(" был создан успешно", GOLD)).build());
            TextComponent message = text().append(text(senderName, WHITE), text(" создал вам банковский счёт!", GOLD)).build();
            getter.sendMessage(message);
            sendDirect(senderName+" создал вам банковский счёт!", getter);
            discordBot.logEconomy(senderName+" создал банковский счёт "+ getterName);
            return true;

        }
        if (args[0].equals("credit")){
            if (user.hasNoBankAccount()) {
                sender.sendMessage(text().append(text("У ", RED), text(getterName, WHITE), text(" нет банковского счёта", RED)).build());
                return false;
            }

            Set<String> keys = Collections.emptySet();
            ConfigurationSection confsec = creditsHandler.getCreditsConf().getConfigurationSection("credits");
            if (confsec != null) {
                keys = confsec.getKeys(false);
            }

            int quantity;
            int percent;
            int time;
            if (args.length >= 5) {
                try {
                    quantity = Integer.parseInt(args[2]);
                    percent = Integer.parseInt(args[3]);
                    time = Integer.parseInt(args[4]);
                    if (quantity < 1 || percent < 1 || time < 1){
                        sender.sendMessage(text("Значение не должно быть меньше единицы", RED));
                        return false;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(text("Значение не должно быть словом/дробным числом", RED));
                    return false;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(text("Эта команда доступна лишь игрокам"));
                    return true;
                }
                if (!(giveAR(getter, quantity, player))) {
                    return false;
                }
                int credit = ceil(quantity+(((float) quantity/100)*percent));
                List<String> credits;
                if (keys.contains(getterName)){
                    credits = creditsHandler.getCreditsConf().getStringList("credits."+ getterName);
                } else {
                    credits = new ArrayList<>();
                }
                credits.add(credit+","+time);
                creditsHandler.getCreditsConf().set("credits."+ getterName, credits);
                creditsHandler.save();

                sender.sendMessage(text().append(text("Успешно выдан кредит ", GOLD), text(getterName, WHITE), text(" в размере ", GOLD),
                        text(quantity + " ", WHITE), text(curAl(), GOLD), text(" под "), text(percent, WHITE),
                        text(" процента(ов).\nПериод выплаты: ", GOLD), text(time, WHITE), text(" дней", GOLD)
                ).build());

                getter.sendMessage(text().append(text(senderName, WHITE), text(" выдал вам кредит в размере ", GOLD),
                        text(quantity + " ", WHITE), text(curAl(), GOLD), text(" под "), text(percent, WHITE),
                        text(" процента(ов).\nПериод выплаты: ", GOLD), text(time, WHITE), text(" дней", GOLD)
                ).build());

                discordBot.logEconomy(senderName+" выдал кредит "+ getterName +" в размере "+quantity+" "+curAl()+" под "+percent+" процента(ов).\nПериод выплаты: "+time+ " дней");

                sendDirect(senderName+" выдал вам кредит "+ getterName +" в размере "+quantity+" "+curAl()+" под "+percent+" процента(ов).\nПериод выплаты: "+time+ " дней", getter);
            } else {
                sender.sendMessage(text("Недостаточно аргументов\n", RED).append(text(
                        "/bank credit <никнейм> <сумма> <процентная ставка> <период выплаты>", YELLOW)));
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("paycredit")) {
            try {
                if (args.length >= 4) {
                    creditsHandler.pay(Integer.parseInt(args[2]), getter, sender, Integer.parseInt(args[3]));
                    return true;
                } else {
                    sender.sendMessage(text("Недостаточно аргументов\n", RED).append(
                            text("/bank paycredit <никнейм> <индекс> <сумма>", YELLOW)
                    ));
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(text("Значение не должно быть словом/дробным числом", RED));
                return false;
            }
        }
        return false;
    }
}
