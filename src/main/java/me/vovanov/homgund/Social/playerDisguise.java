package me.vovanov.homgund.Social;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class playerDisguise {
    static final List<String> MASKS = List.of(
            "балаклава грабителя",
            "последний вздох",
            "военный противогаз",
            "противогаз",
            "маска гая фокса",
            "птичка по-киевски",
            "архимедик",
            "рейхсмахтский пикельхельм",
            "тыкво-головый",
            "хоккейная маска",
            "пришествие размуса",
            "секретарь зяблик",
            "тот самый нотч",
            "балаклава шпиона",
            "маска шпиона"
    );

    static boolean isInMask(Player player){
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet != null && helmet.hasItemMeta() && helmet.getItemMeta().hasDisplayName() && helmet.getType() == Material.CARVED_PUMPKIN) {
            Component helmetName = helmet.getItemMeta().displayName();
            if (helmetName == null) return false;
            return MASKS.contains(LegacyComponentSerializer.legacySection().serialize(helmetName).toLowerCase());
        } else
            return helmet != null && helmet.getType() == Material.CARVED_PUMPKIN;
    }
}
