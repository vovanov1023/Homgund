package me.vovanov.homgund.misc;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class DimensionChange implements Listener {

    @EventHandler
    public void onDimChange(PlayerPortalEvent event) {
        World.Environment toEnvironment = event.getTo().getWorld().getEnvironment();
        FileConfiguration config = PLUGIN.getConfig();
        if (toEnvironment == World.Environment.THE_END && !config.getBoolean("enable-end")) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(text("Энд закрыт!", RED));
        }
        if (toEnvironment == World.Environment.NETHER && !config.getBoolean("enable-nether")) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar(text("Незер закрыт!", RED));
        }
    }
}
