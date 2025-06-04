package me.vovanov.homgund.social.chatandtab;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static me.vovanov.homgund.Homgund.vanishedPlayers;

public class FakeJoinLeave implements Listener {
    GlobalMessages messages = new GlobalMessages();
    @EventHandler
    public void onVanish(PlayerHideEvent event) {
        ++vanishedPlayers;
        Player player = event.getPlayer();
        PLUGIN.getServer().broadcast(messages.getLeaveMessage(player));
    }

    @EventHandler
    public void onReappear(PlayerShowEvent event) {
        --vanishedPlayers;
        Player player = event.getPlayer();
        PLUGIN.getServer().broadcast(messages.getJoinMessage(player));
    }
}
