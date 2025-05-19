package me.vovanov.homgund.Social;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import static me.vovanov.homgund.Social.formattedNicknameGetter.getFormattedName;

public class nicknameOnClick implements Listener {
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event){
        Entity clickedEntity = event.getRightClicked();
        if (!(clickedEntity instanceof Player)) return;
        TextComponent name = getFormattedName((Player) clickedEntity, true, false);
        event.getPlayer().sendActionBar(name);
    }
}
