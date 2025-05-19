package me.vovanov.homgund.Economy.files;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class general {

    @NotNull
    public static String atBlock(){
        try {
            String block = PLUGIN.getConfig().getString("atm-block");
            ItemStack hui = new ItemStack(Material.matchMaterial(block));
            if (hui.getType().isBlock()) {
                return block;
            } else {
                PLUGIN.getLogger().warning("Указан недействительный параметр в качестве банкомата");
                return "lodestone";
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            PLUGIN.getLogger().warning("Указан недействительный параметр в качестве банкомата");
            return "lodestone";
        }
    }

    @NotNull
    public static String curIt(){
        try {
            String item = PLUGIN.getConfig().getString("currency");
            ItemStack hui = new ItemStack(Material.matchMaterial(item));
            if (hui.getType().isItem()) {
                return item;
            } else {
                PLUGIN.getLogger().warning("Указан недействительный параметр в качестве валюты");
                return "deepslate_diamond_ore";
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            PLUGIN.getLogger().warning("Указан недействительный параметр в качестве валюты");
            return "deepslate_diamond_ore";
        }
    }

    @NotNull
    public static String curAl(){
        try {
            return PLUGIN.getConfig().getString("currency-alias");
        } catch (NullPointerException e) {
            PLUGIN.getLogger().warning("Отсутствует псевдоним валюты");
            return "АР";
        }
    }

    public static Boolean giveAR(CommandSender sender, int quantity){
        if (!(sender instanceof Player player)) return false;
        Material currencyMat = Material.matchMaterial(curIt());
        int totalAR = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == currencyMat) {
                totalAR += item.getAmount();
            }
        }
        if (totalAR < quantity) {
            player.sendMessage(text("Недостаточно средств", RED).append(text("\n(В инвентаре " + totalAR + " "+curAl()+")", YELLOW)));
            return false;
        }
        int remaining = quantity;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == currencyMat) {
                int amountInStack = item.getAmount();

                if (amountInStack <= remaining) {
                    remaining -= amountInStack;
                    item.setAmount(0);
                } else {
                    item.setAmount(amountInStack - remaining);
                    break;
                }

                if (remaining == 0) break;
            }
        }

        playerData.get().set("balance", quantity + playerData.get().getInt("balance"));
        playerData.save();
        return true;

    }
}
