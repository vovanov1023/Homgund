package me.vovanov.homgund.Economy.files;

import me.vovanov.homgund.discordBot;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static me.vovanov.homgund.Economy.files.general.*;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ATMOperations implements Listener {

    private static final File dataFolder = PLUGIN.getDataFolder();
    private static final File ATMsFile = new File(dataFolder, "ATMs.yml");
    private static FileConfiguration ATMsConfig;

    public static void newFile(){
        try {ATMsFile.createNewFile();} catch (IOException e) {e.printStackTrace();}
        ATMsConfig = YamlConfiguration.loadConfiguration(ATMsFile);
    }

    public static void checkATMs(){
        Set<String> keys = ATMsConfig.getKeys(false);
        String[] coords;
        Location location;
        World world;
        for (String key : keys) {
            coords = key.split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            double z = Double.parseDouble(coords[2]);
            String worldName = ATMsConfig.getString(key);
            world = Bukkit.getServer().getWorld(worldName);
            location = new Location(world, x, y, z);
            if (location.getBlock().getType() != Material.matchMaterial(atBlock())){
                ATMsConfig.set(key, null);
                discordBot.logEconomy("Был сломан банкомат на: "+x+" "+y+" "+z+" в "+worldName);
            }
        }
        save();
    }

    public static void newATM(int x, int y, int z, CommandSender sender, String worldName){
        String key = x+","+y+","+z;
        World world = getWorldFromSender(sender, worldName);
        if (world == null) return;
        Location loc = new Location(world, x, y, z);
        Block block = loc.getBlock();
        if (block.getType() != Material.matchMaterial(atBlock()) ) {
            sender.sendMessage(text("На этих координатах нет "+atBlock().replace("_", " "), RED));
            return;
        }
        if (ATMsConfig.getKeys(false).contains(key)) {
            sender.sendMessage(text("На этих координатах уже есть банкомат", RED));
            return;
        }
        ATMsConfig.set(key, world.getName());
        save();
        sender.sendMessage(text("Успешно создан банкомат на: ", GOLD).append(text(x+" "+y+" "+z, WHITE)));
        discordBot.logEconomy("Был создан банкомат на: "+x+" "+y+" "+z);

    }

    public static void removeATM(int x, int y, int z, CommandSender player){
        String key = x+","+y+","+z;
        if (!ATMsConfig.getKeys(false).contains(key)) {
            player.sendMessage(text("Нет банкомата на этих координатах", RED));
            return;
        }
        ATMsConfig.set(key, null);
        save();
        player.sendMessage(text("Успешно удалён банкомат на: ", BLUE).append(text(x+" "+y+" "+z, WHITE)));
        discordBot.logEconomy("Был удалён банкомат на: "+x+" "+y+" "+z);
    }

    private static World getWorldFromSender(CommandSender sender, String worldName) {

        World world;
        if (sender instanceof Player player && worldName == null) {
            world = player.getWorld();
            return world;
        } else if (worldName == null) {
            sender.sendMessage(text("Введите мир", RED));
            return null;
        } else {
            world = PLUGIN.getServer().getWorld(worldName);
            if (world == null) {
                sender.sendMessage(text("Такого мира нет", RED));
                return null;
            }
        }
        return world;
    }

    public static TextComponent[][] ATMsList(){
        Set<String> keys = ATMsConfig.getKeys(false);
        String[] coords;
        TextComponent worldName;
        int page = 0;
        int index = 0;
        TextComponent[][] pages = new TextComponent[(keys.size()/10)+1][10];
        for (String key : keys) {
            coords = key.split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            double z = Double.parseDouble(coords[2]);
            if (ATMsConfig.get(key).toString().equals("world")) {
                worldName = text("Верхний мир", GREEN);
            } else if (ATMsConfig.get(key).toString().equals("world_nether")) {
                worldName = text("Незер", RED);
            } else {
                worldName = text("Край", YELLOW);
            }
            pages[page][index] = text(x+" "+y+" "+z+" ").append(worldName);
            if (index + 1 != 10) {
                index += 1;
            } else {
                index = 0;
                page += 1;
            }
        }
        return pages;
    }

    public static Boolean find(Location playerLocation, Player player){
        Set<String> keys = ATMsConfig.getKeys(false);
        String[] coords;
        Location location;
        World world;
        int minDistance = PLUGIN.getConfig().getInt("atm-radius");
        try {
            for (String key : keys) {
                coords = key.split(",");
                double x = Double.parseDouble(coords[0]);
                double y = Double.parseDouble(coords[1]);
                double z = Double.parseDouble(coords[2]);
                String worldName = ATMsConfig.get(key).toString();
                world = Bukkit.getServer().getWorld(worldName);
                location = new Location(world, x, y, z);
                if (!((playerLocation.getWorld()).equals(world))) {
                    continue;
                }
                if ((playerLocation.distanceSquared(location)) < minDistance*minDistance) {
                    return true;
                }
            }
            player.sendMessage(text("Для использования этой команды вы должны подойти к банкомату", RED));
            return false;
        } catch (NullPointerException ignore) {}
        player.sendMessage(text("Для использования этой команды вы должны подойти к банкомату", RED));
        return false;
    }

    public static void save(){
        try {ATMsConfig.save(ATMsFile);} catch (IOException e){e.printStackTrace();}
    }

    @EventHandler
    public void atmBroken(BlockBreakEvent event){
        Block b = event.getBlock();
        int x = b.getX();
        int y = b.getY();
        int z = b.getZ();
        String key = x+","+y+","+z;
        if (ATMsConfig.getKeys(false).contains(key)){
            String worldName = ATMsConfig.getString(key);
            ATMsConfig.set(key, null);
            save();
            discordBot.logEconomy("Был сломан банкомат на: "+x+" "+y+" "+z+" в "+worldName);
        }
    }
}
