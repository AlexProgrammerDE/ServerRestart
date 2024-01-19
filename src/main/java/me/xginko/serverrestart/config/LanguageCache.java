package me.xginko.serverrestart.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import me.xginko.serverrestart.ServerRestart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.util.List;

public class LanguageCache {

    private final ConfigFile lang;
    public final Component no_permission, server_is_restarting;

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
        this.lang = ConfigFile.loadConfig(langYML);

        this.no_permission = getTranslation("messages.no-permission",
                "<red>You don't have permission to use this command.");
        this.server_is_restarting = getTranslation("messages.server-is-restarting",
                "<gold>Server is restarting and will be back in a few minutes.");

        try {
            lang.save();
        } catch (Exception e) {
            ServerRestart.getLog().severe("Failed to save language file: "+ langYML.getName() +" - " + e.getLocalizedMessage());
        }
    }

    public Component getTranslation(String path, String defaultTranslation) {
        lang.addDefault(path, defaultTranslation);
        return MiniMessage.miniMessage().deserialize(lang.getString(path, defaultTranslation));
    }

    public Component getTranslation(String path, String defaultTranslation, String comment) {
        lang.addDefault(path, defaultTranslation, comment);
        return MiniMessage.miniMessage().deserialize(lang.getString(path, defaultTranslation));
    }

    public List<Component> getListTranslation(String path, List<String> defaultTranslation) {
        lang.addDefault(path, defaultTranslation);
        return lang.getStringList(path).stream().map(MiniMessage.miniMessage()::deserialize).toList();
    }

    public List<Component> getListTranslation(String path, List<String> defaultTranslation, String comment) {
        lang.addDefault(path, defaultTranslation, comment);
        return lang.getStringList(path).stream().map(MiniMessage.miniMessage()::deserialize).toList();
    }
}