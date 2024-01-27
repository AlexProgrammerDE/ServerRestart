package me.xginko.serverrestart.commands.subcommands;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.commands.SubCommand;
import me.xginko.serverrestart.enums.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCmd extends SubCommand {

    @Override
    public String label() {
        return "reload";
    }

    @Override
    public TextComponent description() {
        return Component.text("Reloads the plugin.").color(NamedTextColor.GRAY);
    }

    @Override
    public TextComponent syntax() {
        return Component.text("/restarts reload").color(NamedTextColor.WHITE);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.RELOAD.get())) {
            sender.sendMessage(ServerRestart.getLang(sender).no_permission);
            return;
        }

        sender.sendMessage(Component.text("Reloading "+ServerRestart.getInstance().getPluginMeta().getName()+"...").color(NamedTextColor.WHITE));
        ServerRestart.getInstance().reloadPlugin();
        sender.sendMessage(Component.text("Reload complete.").color(NamedTextColor.GREEN));
    }
}