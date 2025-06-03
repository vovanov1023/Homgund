package me.vovanov.homgund;

import me.vovanov.homgund.Economy.commands.*;
import me.vovanov.homgund.Economy.files.*;
import me.vovanov.homgund.Social.*;
import me.vovanov.homgund.Social.Ignore.*;
import me.vovanov.homgund.Social.commands.*;
import me.vovanov.homgund.misc.*;
import me.vovanov.homgund.Social.chatandtab.*;
import me.vovanov.homgund.Social.playersit.*;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import static me.vovanov.homgund.Social.chatandtab.chat.messagesOverHead;

public final class Homgund extends JavaPlugin {
    public static LuckPerms LuckPermsAPI = null;
    public static Team hideNickname;
    public static int vanishedPlayers = 0;
    public static Plugin PLUGIN;
    private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
    public static boolean IsSvEn = false;

    @Override
    public void onEnable() {
        PLUGIN = this;
        registerCommands();
        registerEventListeners();
        hgreload.reload();

        DenySit.newFile();
        IgnoreImpl.newFile();

        ATMOperations.newFile();
        EconomyUser.createPlayerDataFolder();
        creditsHandler.newFile();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, creditsHandler::count, 1200L, 1200L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ATMOperations::checkATMs, 600L, 600L);

        enableLuckPermsIntegration();
        enableSuperVanishIntegration();
        enableGSitIntegration();

        tabHandler.registerTab();
        setupNicknameHide();
        PLUGIN.getLogger().info("Плагин запущен");
    }

    private void setupNicknameHide() {
        Scoreboard sc = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        hideNickname = sc.getTeam("hide");
        if (hideNickname == null) hideNickname = sc.registerNewTeam("hide");
        hideNickname.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    private void registerCommands() {
        getCommand("pm").setExecutor(new privateMessage());
        getCommand("try").setExecutor(new tryCommand());
        getCommand("mee").setExecutor(new meCommand());
        getCommand("do").setExecutor(new doCommand());
        getCommand("balance").setExecutor(new balance());
        getCommand("withdraw").setExecutor(new withdraw());
        getCommand("put").setExecutor(new put());
        getCommand("bank").setExecutor(new bank());
        getCommand("atm").setExecutor(new atm());
        getCommand("hgreload").setExecutor(new hgreload());
        getCommand("sign").setExecutor(new ItemSigning());
        getCommand("pay").setExecutor(new pay());
        getCommand("tp").setExecutor(new Teleport());
        getCommand("hat").setExecutor(new HatCommand());
        getCommand("ignore").setExecutor(new IgnoreCommand());
        getCommand("unignore").setExecutor(new UnignoreCommand());
        getCommand("unignore").setTabCompleter(new UnignoreCommand());
    }

    private void registerEventListeners() {
        Listener[] listeners = {
                new GlobalMessages(), new meCommand(), new chat(), new privateMessage(), new nicknameOnClick(),
                new ATMOperations(), new InvisibleItemFrames(), new BatsDropMembranes(), new DimensionChange(),
                new HorseMilk(), new discordBot()
        };
        for (Listener i : listeners)
            PLUGIN_MANAGER.registerEvents(i, this);
    }

    private void enableGSitIntegration() {
        Plugin sit = PLUGIN_MANAGER.getPlugin("GSit");
        if (sit == null || !sit.isEnabled()) {
            return;
        }
        PLUGIN_MANAGER.registerEvents(new DenySit(), this);

        getCommand("denysit").setExecutor(new DenySit());
        getCommand("allowsit").setExecutor(new AllowSit());

        PLUGIN.getLogger().info("Интеграция с GSit включена");
    }

    private void enableSuperVanishIntegration() {
        Plugin vanish = PLUGIN_MANAGER.getPlugin("SuperVanish");
        if (vanish == null || !vanish.isEnabled()) {
            return;
        }
        IsSvEn = true;
        PLUGIN_MANAGER.registerEvents(new FakeJoinLeave(), this);
        PLUGIN.getLogger().info("Интеграция с SuperVanish включена");
    }

    private void enableLuckPermsIntegration() {
        Plugin perms = PLUGIN_MANAGER.getPlugin("LuckPerms");
        if (perms == null || !perms.isEnabled()) {
            return;
        }
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        LuckPermsAPI = provider.getProvider();
        PLUGIN.getLogger().info("Интеграция с LuckPerms включена");
    }

    @Override
    public void onDisable() {
        if (!messagesOverHead.isEmpty()) {
            messagesOverHead.forEach(Entity::remove);
            PLUGIN.getLogger().info("Были удалены все сообщения над головой");
        }
        hideNickname.unregister();
        DenySit.save();
        IgnoreImpl.save();
        PLUGIN.getLogger().info("Плагин выключен");
    }
}
