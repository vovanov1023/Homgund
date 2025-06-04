package me.vovanov.homgund.economy.files;

import me.vovanov.homgund.DiscordBot;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static me.vovanov.homgund.economy.files.HEconomy.*;
import static me.vovanov.homgund.DiscordBot.sendDirect;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class CreditsHandler {
    private static final File dataFolder = PLUGIN.getDataFolder();
    private static final File creditsFile = new File(dataFolder, "credits.yml");
    private static final FileConfiguration creditsConf = YamlConfiguration.loadConfiguration(creditsFile);

    public static FileConfiguration getCreditsConf(){
        return creditsConf;
    }

    public static void save(){
        try {creditsConf.save(creditsFile);} catch (IOException e) {
            PLUGIN.getLogger().severe("Произошла ошибка во время сохранения файла для хранения кредитов: "
                    + e.getMessage() + "\nПричина: " + e.getCause());
        }
    }

    public static void newFile(){
        File credits = new File(dataFolder, "credits.yml");
        try {
           if (credits.createNewFile())  PLUGIN.getLogger().info("Файл для хранения кредитов создан");
        } catch (IOException e) {
            PLUGIN.getLogger().severe("Произошла ошибка во время создания файла для хранения кредитов: "
                    + e.getMessage() + "\nПричина: " + e.getCause());
        }
        creditsConf.addDefault("count", 0);
        creditsConf.options().copyDefaults(true);
        save();
    }

    public static void getDebtorsList(CommandSender player){
        ConfigurationSection confSec = creditsConf.getConfigurationSection("credits");
        if (confSec == null || confSec.getKeys(false).isEmpty()) {
            player.sendMessage(text("Должников нет :)", GREEN));
            return;
        }
        Set<String> keys = confSec.getKeys(false);
        String[] b;
        player.sendMessage(text("Список должников\n ", GOLD));
        for (String i : keys){
            List<String> c = confSec.getStringList(i);
            for (String a : c) {
                b = a.split(",");
                if (Integer.parseInt(b[1]) == 0){
                    player.sendMessage(text(i, YELLOW));
                    break;
                }
            }
        }
    }

    public static void creditsList(String nick, CommandSender player){
        ConfigurationSection confSec = creditsConf.getConfigurationSection("credits");
        if (confSec == null) {
            creditsConf.createSection("credits");
            player.sendMessage(text("Произошла внутренняя ошибка, попробуйте ещё раз!", RED));
            save();
            return;
        }
        Set<String> keys = confSec.getKeys(false);
        if (!keys.contains(nick)) {
            player.sendMessage(text("У этого игрока нет кредитов", RED));
            return;
        }
        int index = 1;
        player.sendMessage(text().append(text("Кредиты ", YELLOW), text(nick, WHITE), text("\nОсталось выплатить / Осталось дней\n ", YELLOW)).build());
        for (String i : confSec.getStringList(nick)){
            String[] info = i.split(",");
            player.sendMessage(text(index+". " + info[0] + " "+curAl()+" / " + info[1] + " дней"));
            index++;
        }
    }

    public static void pay(int index, Player getter, CommandSender sender, int toPay){
        ConfigurationSection confSec = creditsConf.getConfigurationSection("credits");
        if (confSec == null) {
            creditsConf.createSection("credits");
            sender.sendMessage(text("Произошла внутренняя ошибка, попробуйте ещё раз!", RED));
            save();
            return;
        }
        String getterName = getter.getName();
        String senderName = sender.getName();
        Set<String> keys = confSec.getKeys(false);
        if (!keys.contains(getterName)) {
            sender.sendMessage(text("У этого игрока нет кредитов", RED));
            return;
        }
        try {
            List<String> credits = confSec.getStringList(getterName);
            String[] credit = credits.get(index - 1).split(",");
            int playerOwes = Integer.parseInt(credit[0]);
            if (playerOwes - toPay <= 0) {
                credits.remove(index - 1);

                sender.sendMessage(text("Закрыта задолженность по кредиту ", YELLOW).append(text(getterName, WHITE)));
                getter.sendMessage(text(senderName, WHITE).append(text(" закрыл задолженность по одному из ваших кредитов", YELLOW)));

                DiscordBot.logEconomy(senderName + " закрыл задолженность по кредиту " + getterName + "\n(Индекс кредита: " + index + ")");
                sendDirect(senderName+" закрыл задолженность по одному из ваших кредитов", getter);
            } else {
                credit[0] = String.valueOf(playerOwes - toPay);
                String a = credit[0] + "," + credit[1];
                credits.set(index - 1, a);
                sender.sendMessage(text().append(text("Успешно уменьшена задолженность по кредиту ", YELLOW), text(getterName, WHITE), text(" на ", YELLOW),
                        text(toPay+" ", WHITE), text(curAl(), YELLOW)).build());

                getter.sendMessage(text().append(text(senderName, WHITE), text(" уменьшил задолженность по одному из ваших кредитов на ", YELLOW),
                        text(toPay+" ", WHITE), text(curAl()+"\nТекущая задолженность: "+credit[0], YELLOW)).build());

                DiscordBot.logEconomy(senderName + " уменьшил задолженность " + getterName + " на " + toPay + " " + curAl() + "\nТекущая задолженность: " + credit[0] +
                        "\n(Индекс кредита: " + index + ")");
                sendDirect(senderName + " уменьшил задолженность по одному из ваших кредитов" + " на " + toPay + " " + curAl() + "\nТекущая задолженность: " + credit[0], getter);
            }
            if (credits.isEmpty()) {
                creditsConf.set("credits." + getterName, null);
            } else {
                creditsConf.set("credits." + getterName, credits);
            }
            save();
        } catch (IndexOutOfBoundsException e) {
            sender.sendMessage(text("Кредита с таким индексом нет", RED));
        }
    }

    public static void removeDay(){
        ConfigurationSection playersList = creditsConf.getConfigurationSection("credits");
        if (playersList == null) {
            creditsConf.createSection("credits");
            PLUGIN.getLogger().warning("В файле с кредитами нет секции для кредитов. Оставшиеся для оплаты кредита дни не будут вычтены");
            save();
            return;
        }
        Set<String> keys = playersList.getKeys(false);
        if (keys.isEmpty()) {
            return;
        }
        int creditIndex = 0;
        for (String playerName : keys) {
            List<String> playerCreditsList = playersList.getStringList(playerName);
            for (String credit : playerCreditsList) {
                String[] creditValues = credit.split(",");
                String amountRemaining = creditValues[0];
                int newDaysRemaining = Integer.parseInt(creditValues[1])-1;
                if (newDaysRemaining >= 0) {
                    String newCreditValues = amountRemaining + "," + newDaysRemaining;
                    playerCreditsList.set(creditIndex, newCreditValues);
                } else {
                    sendDirect("У вас задолженность "+amountRemaining+" "+curAl()+" по одному из ваших кредитов! Скорее обратитесь в банк для погашения.", PLUGIN.getServer().getOfflinePlayer(playerName));
                }
                creditIndex += 1;
            }
            creditsConf.set("credits."+playerName, playerCreditsList);
            creditIndex = 0;
        }
        save();
    }

    public static void count() {
        int a = creditsConf.getInt("count")+1;
        if (a < 1440) {
            creditsConf.set("count", a);
        } else {
            try {removeDay();} catch (NullPointerException ignore){}
            creditsConf.set("count", 0);
        }
        save();
    }
}
