package me.vovanov.homgund.misc;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class BatsDropMembranes implements Listener {

    @EventHandler
    public void onBatDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Bat bat)) return;
        Player player = bat.getKiller();
        if (player == null) return;
        int level = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOTING);
        int count = getCount(2+level);
        if (count == 0) return;
        ItemStack membranes = ItemStack.of(Material.PHANTOM_MEMBRANE, count);
        event.getDrops().add(membranes);
    }

    private int getCount(int max) {
        return (int) Math.round(Math.random() * max);
    }
}
