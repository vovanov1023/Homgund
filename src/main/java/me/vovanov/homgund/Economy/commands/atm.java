package me.vovanov.homgund.Economy.commands;

import me.vovanov.homgund.Economy.files.ATMOperations;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class atm implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        String[] subcom = {"help", "create", "remove", "list"};
        if (args.length == 0 || args[0].equalsIgnoreCase("help") || !(Arrays.asList(subcom).contains(args[0]))) {
            sender.sendMessage(text().append(
                    text("Основная команда для управления банкоматами\n \n", YELLOW),

                    text("/atm create <x> <y> <z> [мир]", YELLOW), text(" - создаёт банкомат\n", WHITE),
                    text("/atm remove <x> <y> <z>", YELLOW), text(" - удаляет банкомат на указанных координатах\n", WHITE),
                    text("/atm list <страница>", YELLOW), text(" - выводит список банкоматов", WHITE)
                    ).build()
            );
            return false;
        }
        if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove")){
            int x;
            int y;
            int z;
            String world;
            if (args.length < 4) {
                sender.sendMessage("§cНедостаточно аргументов");
                return false;
            }
            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
                world = args.length >= 5 ? args[4] : null;
            } catch (NumberFormatException e) {
                sender.sendMessage(text("Значение не должно быть словом/дробным числом", RED));
                return false;
            }

            if (args[0].equalsIgnoreCase("create")) {
                ATMOperations.newATM(x, y, z, sender, world);
            } else {
               ATMOperations.removeATM(x, y, z, sender);
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("list")){
            TextComponent[][] atms = ATMOperations.ATMsList();
            int page;
            String apage;
            if (args.length == 1) {page=0; apage="1";} else {
                try {
                    page = Integer.parseInt(args[1]) - 1;
                    apage = args[1];
                } catch (NumberFormatException e) {
                    sender.sendMessage(text("Значение не должно быть словом/дробным числом", RED));
                    return false;
                }
            }
            TextComponent[] list;
            try {
                list = atms[page];
            } catch (IndexOutOfBoundsException e){
                sender.sendMessage(text("Этой страницы нет", RED));
                return false;
            }
            sender.sendMessage(text("Список всех банкоматов\n ", GOLD));
            for (TextComponent i : list){
                if (i != null) {
                    sender.sendMessage(i);
                }
            }
            sender.sendMessage(text(" \nСтраница "+apage+" / "+atms.length, YELLOW));
            return true;
        }
        return false;
    }
}
