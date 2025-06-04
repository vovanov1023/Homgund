package me.vovanov.homgund.social.chatandtab;

import de.myzelyam.api.vanish.VanishAPI;
import io.papermc.paper.advancement.AdvancementDisplay;
import me.vovanov.homgund.economy.files.EconomyUser;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.vovanov.homgund.Homgund.*;
import static me.vovanov.homgund.social.FormattedNicknameGetter.getFormattedName;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class GlobalMessages implements Listener {

    public TextComponent getJoinMessage(Player player) {
        TextComponent name = getFormattedName(player, true, true);
        return text().append(text("[+] ", GOLD), name, text(" присоединился к игре", GREEN)).build();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        TextComponent name = getFormattedName(player, true, true);
        TextComponent joinMessage;
        if (player.hasPlayedBefore())
            joinMessage = getJoinMessage(player);
        else
            joinMessage = text().append(text("[+] ", GOLD), name, text(" зашёл на сервер впервые", YELLOW)).build();
        event.joinMessage(joinMessage);

        hideNickname.addEntry(player.getName());

        String legacyName = player.getName();
        EconomyUser.newFile(legacyName);

        if (IsSvEn && VanishAPI.isInvisible(player)) {
            ++vanishedPlayers;
        }
    }

    public TextComponent getLeaveMessage(Player player) {
        TextComponent name = getFormattedName(player, true, false);
        return text().append(text("[-] ", GOLD), name, text(" покинул игру", RED)).build();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.quitMessage(getLeaveMessage(player));

        hideNickname.removeEntry(player.getName());

        if (IsSvEn && VanishAPI.isInvisible(player)) {
            --vanishedPlayers;
        }
    }

    @EventHandler
    public void onAchievement(PlayerAdvancementDoneEvent event) {
        AdvancementDisplay dis =  event.getAdvancement().getDisplay();
        if (dis == null) return;
        AdvancementDisplay.Frame frame = dis.frame();
        if (frame != AdvancementDisplay.Frame.CHALLENGE) {
            event.message(null);
        }
    }
}
