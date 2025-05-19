package me.vovanov.homgund.misc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class HatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Эта команда доступна только игрокам!", RED));
            return true;
        }
        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInMainHand();
        if (itemInHand.getType().isAir()) {
            player.sendMessage(text("У вас нет предмета в руке", RED));
            return true;
        }
        ItemStack helmet = inventory.getHelmet();
        inventory.setItemInMainHand(helmet);
        player.playSound(player.getLocation(), "item.armor.equip_leather", 1, 1);
        inventory.setHelmet(itemInHand);
        return true;
    }
}
