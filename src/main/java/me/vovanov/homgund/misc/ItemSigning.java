package me.vovanov.homgund.misc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class ItemSigning implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда доступна только игрокам", NamedTextColor.RED));
            return true;
        }
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(text("У вас нет предмета в руке!", NamedTextColor.RED));
            return true;
        }

        Component playerName = text(player.getName());
        ItemMeta itemMeta = itemInHand.getItemMeta();
        List<Component> lore = itemInHand.lore();

        if (itemMeta.hasLore() && lore.contains(text("Подписали:"))) {
            if (lore.contains(playerName)) {
                player.sendMessage(text("Вы уже подписали этот предмет!", NamedTextColor.RED));
                return true;
            }
        } else {
            lore = new ArrayList<>();
            lore.add(text("Подписали:"));
        }

        String text = String.join(" ", args);
        lore.add(playerName);
        if (!text.isBlank()) {
            lore.add(text(text.strip()));
        }
        lore.add(text(" "));
        itemInHand.lore(lore);
        player.getInventory().setItemInMainHand(itemInHand);
        player.sendMessage(text("Подпись добавлена!", NamedTextColor.GREEN));
        return true;
    }
}
