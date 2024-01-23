package me.xginko.serverrestart.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.serverrestart.ServerRestart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.util.List;

public class LanguageCache {

    private final ConfigFile langFile;
    public final Component no_permission, server_is_restarting, restart_delayed_playercount,
            countdown_15min, countdown_10min, countdown_5min, countdown_4min, countdown_3min, countdown_2min, countdown_1min,
            countdown_30sec, countdown_10sec, countdown_9sec, countdown_8sec, countdown_7sec, countdown_6sec, countdown_5sec,
            countdown_4sec, countdown_3sec, countdown_2sec, countdown_1sec, countdown_now;

    public LanguageCache(String locale) throws Exception {
        ServerRestart plugin = ServerRestart.getInstance();
        File langYML = new File(plugin.getDataFolder() + File.separator + "lang", locale + ".yml");
        // Check if the lang folder has already been created
        File parent = langYML.getParentFile();
        if (!parent.exists() && !parent.mkdir())
            ServerRestart.getLog().severe("Unable to create lang directory.");
        // Check if the file already exists and save the one from the plugins resources folder if it does not
        if (!langYML.exists())
            plugin.saveResource("lang" + File.separator + locale + ".yml", false);
        // Finally load the lang file with configmaster
        this.langFile = ConfigFile.loadConfig(langYML);

        this.no_permission = getTranslation("messages.no-permission",
                "<red>You don't have permission to use this command.");
        this.server_is_restarting = getTranslation("messages.server-is-restarting",
                "<gold>Server is restarting and will be back in a few minutes.");
        this.restart_delayed_playercount = getTranslation("messages.restart-delayed-high-playercount",
                "<gray>Delaying restart for %time% due to high playercount.");

        this.countdown_15min = getTranslation("countdown.15min", "<gold>Server restart in 15 minutes");
        this.countdown_10min = getTranslation("countdown.10min", "<gold>Server restart in 10 minutes");
        this.countdown_5min = getTranslation("countdown.5min", "<gold>Server restart in 5 minutes");
        this.countdown_4min = getTranslation("countdown.4min", "<gold>Server restart in 4 minutes");
        this.countdown_3min = getTranslation("countdown.3min", "<gold>Server restart in 3 minutes");
        this.countdown_2min = getTranslation("countdown.2min", "<gold>Server restart in 2 minutes");
        this.countdown_1min = getTranslation("countdown.1min", "<gold>Server restart in 1 minutes");
        this.countdown_30sec = getTranslation("countdown.30sec", "<gold>Server restart in 15 seconds");
        this.countdown_10sec = getTranslation("countdown.10sec", "<gold>Server restart in 10 seconds");
        this.countdown_9sec = getTranslation("countdown.9sec", "<gold>Server restart in 9 seconds");
        this.countdown_8sec = getTranslation("countdown.8sec", "<gold>Server restart in 8 seconds");
        this.countdown_7sec = getTranslation("countdown.7sec", "<gold>Server restart in 7 seconds");
        this.countdown_6sec = getTranslation("countdown.6sec", "<gold>Server restart in 6 seconds");
        this.countdown_5sec = getTranslation("countdown.5sec", "<gold>Server restart in 5 seconds");
        this.countdown_4sec = getTranslation("countdown.4sec", "<gold>Server restart in 4 seconds");
        this.countdown_3sec = getTranslation("countdown.3sec", "<gold>Server restart in 3 seconds");
        this.countdown_2sec = getTranslation("countdown.2sec", "<gold>Server restart in 2 seconds");
        this.countdown_1sec = getTranslation("countdown.1sec", "<gold>Server restart in 1 seconds");
        this.countdown_now = getTranslation("countdown.now", "<bold><red>Restarting now");

        try {
            this.langFile.save();
        } catch (Exception e) {
            ServerRestart.getLog().severe("Failed to save language file: "+ langYML.getName() +" - " + e.getLocalizedMessage());
        }
    }

    public Component getTranslation(String path, String defaultTranslation) {
        this.langFile.addDefault(path, defaultTranslation);
        return MiniMessage.miniMessage().deserialize(this.langFile.getString(path, defaultTranslation));
    }

    public Component getTranslation(String path, String defaultTranslation, String comment) {
        this.langFile.addDefault(path, defaultTranslation, comment);
        return MiniMessage.miniMessage().deserialize(this.langFile.getString(path, defaultTranslation));
    }

    public List<Component> getListTranslation(String path, List<String> defaultTranslation) {
        this.langFile.addDefault(path, defaultTranslation);
        return this.langFile.getStringList(path).stream().map(MiniMessage.miniMessage()::deserialize).toList();
    }

    public List<Component> getListTranslation(String path, List<String> defaultTranslation, String comment) {
        this.langFile.addDefault(path, defaultTranslation, comment);
        return this.langFile.getStringList(path).stream().map(MiniMessage.miniMessage()::deserialize).toList();
    }
}