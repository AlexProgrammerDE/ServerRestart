package me.xginko.serverrestart.paper.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.serverrestart.paper.ServerRestart;
import me.xginko.serverrestart.common.CommonUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class LanguageCache {

    private final @NotNull ConfigFile langFile;
    public final @NotNull Component no_permission, server_restarting, server_restarting_on_fire,
            restart_delayed_playercount, countdown_now, restart_in;

    public LanguageCache(String locale) throws Exception {
        ServerRestart plugin = ServerRestart.getInstance();
        File langYML = new File(plugin.getDataFolder() + File.separator + "lang", locale + ".yml");
        // Check if the lang folder has already been created
        File parent = langYML.getParentFile();
        if (!parent.exists() && !parent.mkdir())
            ServerRestart.getLog().error("Unable to create lang directory.");
        // Check if the file already exists and save the one from the plugins resources folder if it does not
        if (!langYML.exists())
            plugin.saveResource("lang" + File.separator + locale + ".yml", false);
        // Finally load the lang file with configmaster
        this.langFile = ConfigFile.loadConfig(langYML);

        this.no_permission = getTranslation("messages.no-permission",
                "<red>You don't have permission to use this command.");
        this.server_restarting = getTranslation("messages.server-restarting",
                "<gold>Server is restarting and will be back in a few minutes.");
        this.server_restarting_on_fire = getTranslation("messages.server-restarting",
                "<gold>Server is restarting and will be back in a few minutes.");
        this.restart_delayed_playercount = getTranslation("messages.restart-delayed-high-playercount",
                "<gray>Delaying restart for %time% due to high playercount.");

        this.restart_in = getTranslation("countdown.timer-message", "<gold>Restarting in %time% ...");
        this.countdown_now = getTranslation("countdown.now", "<bold><red>Restarting now");

        try {
            this.langFile.save();
        } catch (Exception e) {
            ServerRestart.getLog().error("Failed to save language file: "+ langYML.getName() +" - " + e.getLocalizedMessage());
        }
    }

    public @NotNull Component time_until_restart(final Duration remainingTime) {
        return this.restart_in.replaceText(TextReplacementConfig.builder()
                .match("%time%")
                .replacement(CommonUtil.formatDuration(remainingTime))
                .build());
    }

    private @NotNull Component getTranslation(String path, String defaultTranslation) {
        this.langFile.addDefault(path, defaultTranslation);
        return MiniMessage.miniMessage().deserialize(this.langFile.getString(path, defaultTranslation));
    }

    private @NotNull Component getTranslation(String path, String defaultTranslation, String comment) {
        this.langFile.addDefault(path, defaultTranslation, comment);
        return MiniMessage.miniMessage().deserialize(this.langFile.getString(path, defaultTranslation));
    }

    private @NotNull List<Component> getListTranslation(String path, List<String> defaultTranslation) {
        this.langFile.addDefault(path, defaultTranslation);
        return this.langFile.getStringList(path).stream().map(MiniMessage.miniMessage()::deserialize).toList();
    }

    private @NotNull List<Component> getListTranslation(String path, List<String> defaultTranslation, String comment) {
        this.langFile.addDefault(path, defaultTranslation, comment);
        return this.langFile.getStringList(path).stream().map(MiniMessage.miniMessage()::deserialize).toList();
    }
}