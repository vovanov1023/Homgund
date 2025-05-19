package me.vovanov.homgund.Social.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;

import static me.vovanov.homgund.Social.Ignore.IgnoreImpl.getIgnoringPlayers;
import static me.vovanov.homgund.Social.chatandtab.chat.processMessageWithLinks;
import static me.vovanov.homgund.Social.formattedNicknameGetter.getFormattedName;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.bukkit.Bukkit.getPlayerExact;

public class privateMessage implements CommandExecutor, Listener {
    final Component PREFIX = text("[ЛС] ", RED);
    final Component ARROW = text(" -> ", WHITE);
    final Component COLON = text(": ", WHITE);
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Component senderName;
        if (sender instanceof Player player) senderName = getFormattedName(player, false, true);
        else senderName = text(sender.getName());

        if (args.length < 2 || args[0].isEmpty() || args[1].isEmpty()) return false;
        Player receiver = getPlayerExact(args[0]);
        if (receiver == null) {
            sender.sendMessage(text(args[0]+" оффлайн", RED));
            return true;
        }

        TextComponent receiverName = getFormattedName(receiver, false, true, false);
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Component processedMessage = processMessageWithLinks(message, YELLOW);

        if (sender.getName().equals(receiver.getName())){
            sender.sendMessage(text().append(PREFIX, text("Ваш внутренний голос", YELLOW), COLON, processedMessage).build());
            return true;
        }

        Component toReceiver = text().append(PREFIX, senderName.color(GOLD), ARROW, text("Вам", GOLD), COLON, processedMessage).build();
        Component toSender = text().append(PREFIX, text("Вы", GOLD), ARROW, receiverName.color(GOLD), COLON, processedMessage).build();

        String recud = receiver.getUniqueId().toString();
        String senud = sender instanceof Player player ? player.getUniqueId().toString() : null;

        if (!getIgnoringPlayers(senud).contains(recud)) receiver.sendMessage(toReceiver);
        sender.sendMessage(toSender);
        return true;
    }
}
