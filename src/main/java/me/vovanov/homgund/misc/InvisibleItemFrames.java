package me.vovanov.homgund.misc;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class InvisibleItemFrames implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        if (!isItemFrame(entity)) return;
        if (!player.isSneaking()) return;
        boolean entityInvisible = entity.isInvisible();
        Material item = player.getInventory().getItemInMainHand().getType();
        Location loc = entity.getLocation();

        if (!entityInvisible && item == Material.GLOW_INK_SAC) {
            entity.setInvisible(true);
            player.playSound(loc, Sound.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            event.setCancelled(true);
        } else if (entityInvisible && item == Material.INK_SAC) {
            entity.setInvisible(false);
            player.playSound(loc, Sound.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            event.setCancelled(true);
        }
    }

    private boolean isItemFrame(Entity entity) {
        EntityType entityType = entity.getType();
        return entityType == EntityType.ITEM_FRAME || entityType == EntityType.GLOW_ITEM_FRAME;
    }
}