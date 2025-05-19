package me.vovanov.homgund.misc;

import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static net.kyori.adventure.text.Component.text;

public class HorseMilk implements Listener {

    @EventHandler
    public void onHorseInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getRightClicked().getType() != EntityType.HORSE) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() != Material.BUCKET) {
            return;
        }

        event.setCancelled(true);
        if (itemInHand.getAmount() > 1 && player.getGameMode() != GameMode.CREATIVE) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
            player.getInventory().setItemInMainHand(itemInHand);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        ItemStack kumysItem = new ItemStack(Material.MILK_BUCKET);
        ItemMeta meta = kumysItem.getItemMeta();
        meta.displayName(text().append(text("Ведро кумыса")).decoration(TextDecoration.ITALIC, false).build());
        kumysItem.setItemMeta(meta);

        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(kumysItem);
        } else {
            player.getWorld().dropItem(player.getLocation(), kumysItem);
        }

        player.playSound(player.getLocation(), "entity.cow.milk", 1.0f, 1.0f);
    }
}
