package me.vovanov.homgund.social.chatandtab;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static java.lang.Math.round;
import static me.vovanov.homgund.Homgund.PLUGIN;
import static me.vovanov.homgund.Homgund.vanishedPlayers;
import static me.vovanov.homgund.social.FormattedNicknameGetter.getFormattedName;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static org.bukkit.Bukkit.getServer;

public class TabHandler {
    public static void registerTab() {
        new BukkitRunnable() {
            final Collection<? extends Player> playerList = getServer().getOnlinePlayers();
        @Override
        public void run() {
            for (Player player : playerList) {
                player.sendPlayerListHeaderAndFooter(
                        text().append(
                                text("Homgund\n ", GOLD, BOLD)
                        ).build(),
                        text().append(
                                text("\nПинг: ", WHITE), text(player.getPing(), YELLOW),
                                text(" TPS: ", WHITE), text(round(getServer().getTPS()[0]), YELLOW),
                                text("\nОнлайн: ", WHITE), text(playerList.size()-vanishedPlayers, YELLOW),
                                text("\nНаиграно: "), text(getTimePlayed(player), YELLOW)
                        ).build()
                );

                player.playerListName(getFormattedName(player, true, false, false));
            }
        }
    }.runTaskTimer(PLUGIN, 0L, 40L);
    }

    private static String getTimePlayed(Player player) {
        long secondsPlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE)/20;

        long months = secondsPlayed / 2592000;
        long remainingAfterMonths = secondsPlayed % 2592000;
        long days = remainingAfterMonths / 86400;
        long remainingAfterDays = remainingAfterMonths % 86400;
        long hours = remainingAfterDays / 3600;
        long remainingAfterHours = remainingAfterDays % 3600;
        long minutes = remainingAfterHours / 60;

        String monthsStr = months > 0 ? months + "мес " : "";
        String daysStr = days > 0 || months > 0 ? days + "дн " : "";
        String hoursStr = (hours > 0 || days > 0 || months > 0) ? hours + "ч " : "";
        String minutesStr = minutes + "мин";

        return monthsStr + daysStr + hoursStr + minutesStr;
    }
}
