package me.vovanov.homgund.Social;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static me.vovanov.homgund.Social.formattedNicknameGetter.getFormattedName;

public class nicknameOnClick implements Listener {
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;
        Player player = event.getPlayer();

        int maxDistance = PLUGIN.getConfig().getInt("max-nametag-distance");
        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                maxDistance,
                entity -> entity instanceof Player && !entity.equals(player)
        );
        if (result != null && result.getHitEntity() instanceof Player targetPlayer) {
            TextComponent name = getFormattedName(targetPlayer, true, false);
            player.sendActionBar(name);
        }
    }
}
