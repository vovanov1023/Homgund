package me.vovanov.homgund.Social;

// import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import de.myzelyam.api.vanish.VanishAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.vovanov.homgund.Homgund.IsSvEn;
import static me.vovanov.homgund.Homgund.PLUGIN;

public class utils {

    public static List<Player> getPlayersNearList(Player sender) {
        List<Player> playersNear = new ArrayList<>();
        Location senderLoc = sender.getLocation();
        World senderWorld = sender.getWorld();
        double radius = PLUGIN.getConfig().getDouble("near-radius");
        double radiusSq = radius * radius;

        List<Player> playersToCheck = new ArrayList<>();

        if (IsSvEn && VanishAPI.isInvisible(sender)) {
            for (UUID uuid : VanishAPI.getInvisiblePlayers()) {
                Player p = PLUGIN.getServer().getPlayer(uuid);
                if (p != null && !p.equals(sender) && senderWorld.equals(p.getWorld())) {
                    playersToCheck.add(p);
                }
            }
        } else {
            playersToCheck.addAll(senderWorld.getPlayers());
            playersToCheck.remove(sender);
        }

        for (Player p : playersToCheck) {
            if (p.getLocation().distanceSquared(senderLoc) <= radiusSq) {
                playersNear.add(p);
            }
        }

        return playersNear;
    }

    /*
    public static boolean arePlayersNear(Player player1, Player player2, int radius) {
        return player1.getLocation().distanceSquared(player2.getLocation()) <= radius*radius;
    }

    public static Player getNearestPlayer(Player sender, int maxDistance) {

        Location senderLoc = sender.getLocation();
        World senderWorld = sender.getWorld();
        Player nearestPlayer = null;
        double closestDistanceSq = Double.MAX_VALUE;

        for (Player target : senderWorld.getPlayers()) {
            if (target.equals(sender)) continue;
            if (target.getGameMode() == GameMode.SPECTATOR) continue;
            if (VanishAPI.isInvisible(target)) continue;

            double distanceSq = target.getLocation().distanceSquared(senderLoc);
            if (distanceSq > maxDistance*maxDistance) continue;

            if (distanceSq <= closestDistanceSq) {
                closestDistanceSq = distanceSq;
                nearestPlayer = target;
            }
        }
        return nearestPlayer;
    }
*/

}