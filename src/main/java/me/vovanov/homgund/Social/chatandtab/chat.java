package me.vovanov.homgund.Social.chatandtab;

import de.myzelyam.api.vanish.VanishAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;

import static me.vovanov.homgund.Homgund.IsSvEn;
import static me.vovanov.homgund.Social.Ignore.IgnoreImpl.getIgnoringPlayers;
import static net.kyori.adventure.text.Component.text;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static me.vovanov.homgund.Social.formattedNicknameGetter.getFormattedName;
import static me.vovanov.homgund.Social.utils.getPlayersNearList;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class chat implements Listener {
    public static ArrayList<TextDisplay> messagesOverHead = new ArrayList<>();
    private static final Pattern URL_PATTERN = Pattern.compile(
            "((https?|ftp)://|www\\.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessageSend(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        TextComponent name = getFormattedName(sender, true, true);
        Component message = event.message();

        String legacyMessage = LegacyComponentSerializer.legacySection().serialize(message).trim();

        if (legacyMessage.startsWith("!")) {
            sendGlobalMessage(event, sender, legacyMessage, name);
            return;
        }
        sendLocalMessage(event, sender, legacyMessage, name);
    }

    private void sendGlobalMessage(AsyncChatEvent event, Player sender, String legacyMessage, Component name) {
        String finalLegacyMessage = legacyMessage.substring(1);
        if (finalLegacyMessage.isEmpty()) {
            sender.sendMessage(text("Сообщение не может быть пустым", RED));
            event.setCancelled(true);
            return;
        }
        event.message(text(finalLegacyMessage));
        Component processedMessage = processMessageWithLinks(finalLegacyMessage, WHITE);
        event.message(processedMessage);
        event.renderer((source, sourceDisplayName, messageContent, viewer) ->
                text().append(
                        text("Ⓖ ", GOLD),
                        name, text(": "),
                        processedMessage
                ).build()
        );
        if (IsSvEn && VanishAPI.isInvisible(sender)) {
            event.viewers().clear();
            for (UUID checkedPlayerUUID : VanishAPI.getInvisiblePlayers()) {
                Player checkedPlayer = PLUGIN.getServer().getPlayer(checkedPlayerUUID);
                event.viewers().add(checkedPlayer);
            }
            hideFromIgnoringPlayers(event, sender);
            return;
        }
        hideFromIgnoringPlayers(event, sender);
        if (event.isCancelled()) return;
        createTextDisplay(sender, finalLegacyMessage);
    }

    private void sendLocalMessage(AsyncChatEvent event, Player sender, String legacyMessage, Component name) {
        List<Player> playersNearList = getPlayersNearList(sender);

        Component processedMessage = processMessageWithLinks(legacyMessage, GRAY);

        Component message = text().append(
                text("Ⓛ ", GREEN),
                name, text(": "),
                processedMessage
        ).build();

        boolean everyoneNearVanished = true;
        if (IsSvEn && !VanishAPI.isInvisible(sender)) {
            for (Player pl : playersNearList) {
                if (!VanishAPI.isInvisible(pl)) {
                    everyoneNearVanished = false;
                    break;
                }
            }
        } else {
            everyoneNearVanished = false;
        }

        if (playersNearList.isEmpty() || everyoneNearVanished) {
            message = text().append(message, text("\n...вокруг вас никого",RED),
                    text("\nПоставьте '!' перед сообщением для глобального чата",WHITE)).build();
        }

        Component finalMessage = message;
        event.renderer((source, sourceDisplayName, messageContent, viewer) ->
                finalMessage
        );
        event.message(text(""));
        event.viewers().clear();
        event.viewers().addAll(playersNearList);
        event.viewers().add(sender);

        hideFromIgnoringPlayers(event, sender);

        if ((IsSvEn && VanishAPI.isInvisible(sender)) || event.isCancelled()) return;
        createTextDisplay(sender, legacyMessage);
    }

    private void hideFromIgnoringPlayers(AsyncChatEvent event, Player sender) {
        List<String> ignoringUUIDs = getIgnoringPlayers(sender.getUniqueId().toString());
        List<Player> ignoringPlayers = new ArrayList<>();
        ignoringUUIDs.forEach(uuid -> ignoringPlayers.add(PLUGIN.getServer().getPlayer(UUID.fromString(uuid))));

        ignoringPlayers.forEach(event.viewers()::remove);
    }

    public static Component processMessageWithLinks(String message, NamedTextColor color) {
        Matcher matcher = URL_PATTERN.matcher(message);

        if (!matcher.find()) {
            return text(message, color);
        }

        matcher.reset();

        TextComponent.Builder builder = text().color(color);
        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                builder.append(text(message.substring(lastEnd, matcher.start()), color));
            }

            String url = matcher.group();
            String clickableUrl = url;

            if (url.startsWith("www.")) {
                clickableUrl = "https://" + url;
            }

            builder.append(
                    text(url, AQUA)
                            .decorate(TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.openUrl(clickableUrl))
                            .hoverEvent(HoverEvent.showText(text("Перейти по ссылке")))
            );

            lastEnd = matcher.end();
        }

        if (lastEnd < message.length()) {
            builder.append(text(message.substring(lastEnd), color));
        }

        return builder.build();
    }

    void createTextDisplay(Player player, String message) {
        List<Entity> passengers = player.getPassengers();
        Entity firstPassenger = passengers.isEmpty() ? null : passengers.getFirst();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (firstPassenger != null && firstPassenger.getType() == EntityType.TEXT_DISPLAY) firstPassenger.remove();
            }
        }.runTask(PLUGIN);

        if (firstPassenger != null && firstPassenger.getType() != EntityType.TEXT_DISPLAY) return;

        Location playerLoc  = player.getLocation().clone();
        double newY = playerLoc.getY() + 2;
        playerLoc.setY(newY);

        new BukkitRunnable() {
            @Override
            public void run() {
                TextDisplay text = player.getWorld().spawn(playerLoc, TextDisplay.class);
                text.setBillboard(Display.Billboard.CENTER);
                text.setLineWidth(150);
                text.setBackgroundColor(Color.fromARGB(40,0,0,0));
                player.addPassenger(text);
                text.text(text(message + "\n "));
                messagesOverHead.add(text);
                new BukkitRunnable() {@Override public void run() {messagesOverHead.remove(text); text.remove();}}.runTaskLater(PLUGIN, 100L);
            }
        }.runTask(PLUGIN);
    }
}