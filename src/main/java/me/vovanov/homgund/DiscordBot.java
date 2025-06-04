package me.vovanov.homgund;

import github.scarsz.discordsrv.DiscordSRV;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static me.vovanov.homgund.Homgund.PLUGIN;

public class DiscordBot implements Listener {
    private static TextChannel channel;
    private static JDA bot;
    private static final ConcurrentHashMap<UUID, String> discordUsers = new ConcurrentHashMap<>();

    public void initialiseBot(String token, String id) {
        try {
            if(bot != null) {
                bot.shutdownNow();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        bot = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .build();
        try { bot.awaitReady(); } catch (InterruptedException ignore) { PLUGIN.getLogger().severe("Бот не смог включится, причина в " + ignore.getClass().getName() + " " + ignore.getMessage()); }
        channel = bot.getTextChannelById(id);
        try{
            PLUGIN.getLogger().info("Канал для логирования экономики: "+channel.getName()+" ("+channel.getId()+")");}
        catch (NullPointerException e) {PLUGIN.getLogger().warning("Не найдено канала с таким айди: "+id);}
    }

    public static void logEconomy(String message) {
        if (channel != null) channel.sendMessage(message).queue();
    }

    public static void sendDirect(String message, OfflinePlayer player) {
        if (bot == null) return;
        UUID playerUUID = player.getUniqueId();

        String cachedId = discordUsers.get(playerUUID);
        if (cachedId != null) {
            sendDirectToUser(cachedId, message);
        } else {
            cacheDiscordUser(playerUUID, userID -> {
                if (userID != null) {
                    sendDirectToUser(userID, message);
                }
            });
        }
    }

    private static void sendDirectToUser(String userId, String message) {
        bot.retrieveUserById(userId).queue(user -> user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(
                success -> {},
                error -> PLUGIN.getLogger().warning("Не удалось отправить ЛС: " + error.getMessage())),
                        error -> PLUGIN.getLogger().warning("Не удалось открыть ЛС: " + error.getMessage())), error -> PLUGIN.getLogger().warning("Не удалось найти пользователя: " + error.getMessage()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (bot == null) return;
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (discordUsers.containsKey(playerUUID)) return;
        cacheDiscordUser(playerUUID, userID -> {});
    }

    private static void cacheDiscordUser(UUID playerUUID, Consumer<String> callback) {
        PLUGIN.getServer().getScheduler().runTaskAsynchronously(PLUGIN, () -> {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.complete(DiscordSRV.getPlugin()
                    .getAccountLinkManager()
                    .getDiscordId(playerUUID));
            future.thenAccept(userID -> {
                if (userID != null) {
                    discordUsers.put(playerUUID, userID);
                    callback.accept(userID);
                }
            }).exceptionally(e -> {
                PLUGIN.getLogger().warning("Не удалось получить Discord ID: " + e.getMessage());
                return null;
            });
        });
    }
}
