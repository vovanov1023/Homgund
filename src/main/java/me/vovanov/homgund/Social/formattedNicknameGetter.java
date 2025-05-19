package me.vovanov.homgund.Social;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

import java.util.Objects;

import static me.vovanov.homgund.Homgund.*;
import static me.vovanov.homgund.Social.playerDisguise.isInMask;
import static net.kyori.adventure.text.Component.text;

public class formattedNicknameGetter {

    static CachedMetaData getPlayerMetaData(Player player) {
        User user = LuckPermsAPI.getUserManager().getUser(player.getUniqueId());
        CachedDataManager cachedData = Objects.requireNonNull(user).getCachedData();
        return cachedData.getMetaData();
    }

    static TextComponent getPrefix(Player player){
        String prefix = getPlayerMetaData(player).getPrefix();
        return prefix != null ? colorize(prefix) : text("");
    }

    static TextComponent getSuffix(Player player){
        String suffix = getPlayerMetaData(player).getSuffix();
        return suffix != null ? colorize(suffix) : text("");
    }

    public static TextComponent getFormattedName(Player player, boolean addPrefixAndSuffix, boolean addClickAction) {
        return getFormattedName(player, addPrefixAndSuffix, addClickAction, true);
    }

    public static TextComponent getFormattedName(Player player, boolean addPrefixAndSuffix, boolean addClickAction, boolean applyDisguise) {
        if (isInMask(player) && applyDisguise) return text("???");
        TextComponent formattedName = text(player.getName());
        if (addPrefixAndSuffix && LuckPermsAPI != null) formattedName = text().append(getPrefix(player)).append(formattedName).append(getSuffix(player)).build();

        if (addClickAction) {
            formattedName = formattedName
                    .hoverEvent(HoverEvent.showText(text("Написать личное сообщение " + player.getName())))
                    .clickEvent(ClickEvent.suggestCommand("/pm " + player.getName() + " "));
        }
        return formattedName;
    }

    static TextComponent colorize(String string){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
    }
}
