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
    public String getLabel() {
        return "reload";
    }

    @Override
    public TextComponent getDescription() {
        return Component.text("Reload the plugin configuration.").color(NamedTextColor.GRAY);
    }

    @Override
    public TextComponent getSyntax() {
        return Component.text("/restarts reload").color(NamedTextColor.WHITE);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permissions.RELOAD.get())) {
            sender.sendMessage(Component.text("Reloading VillagerOptimizer...").color(NamedTextColor.WHITE));
            ServerRestart.getInstance().reloadPlugin();
            sender.sendMessage(Component.text("Reload complete.").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(ServerRestart.getLang(sender).no_permission);
        }
    }
}