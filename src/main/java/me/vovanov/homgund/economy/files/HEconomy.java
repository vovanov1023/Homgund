package me.vovanov.homgund.economy.files;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.homgund.Homgund.PLUGIN;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class HEconomy {

    @NotNull
    public static String atBlock(){
        String item = PLUGIN.getConfig().getString("atm-block");
        if (item == null) return "lodestone";
        Material mat = Material.matchMaterial(item);
        if (mat == null) return "lodestone";
        ItemStack stack = new ItemStack(mat);
        if (stack.getType().isBlock()) return item;
        else return "lodestone";
    }

    @NotNull
    public static String curIt(){
        String item = PLUGIN.getConfig().getString("currency");
        if (item == null) return "deepslate_diamond_ore";
        Material mat = Material.matchMaterial(item);
        if (mat == null) return "deepslate_diamond_ore";
        ItemStack stack = new ItemStack(mat);
        if (stack.getType().isItem()) return item;
        else return "deepslate_diamond_ore";
    }

    @NotNull
    public static String curAl(){
        String alias = PLUGIN.getConfig().getString("currency-alias");
        return alias == null ? "АР" : alias;
    }

    public static Boolean giveAR(Player moneyGetter, int quantity) {
        return giveAR(moneyGetter, quantity, moneyGetter);
    }

    public static Boolean giveAR(Player moneyGetter, int quantity, Player moneySender) {
        Material currencyMat = Material.matchMaterial(curIt());
        int totalAR = 0;
        for (ItemStack item : moneySender.getInventory().getContents()) {
            if (item != null && item.getType() == currencyMat) {
                totalAR += item.getAmount();
            }
        }
        if (totalAR < quantity) {
            moneySender.sendMessage(text("Недостаточно средств", RED).append(text("\n(В инвентаре " + totalAR + " "+curAl()+")", YELLOW)));
            return false;
        }
        int remaining = quantity;
        for (ItemStack item : moneySender.getInventory().getContents()) {
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
        EconomyUser user = EconomyUser.getUser(moneyGetter);
        user.addToBalance(quantity);
        return true;

    }
}
