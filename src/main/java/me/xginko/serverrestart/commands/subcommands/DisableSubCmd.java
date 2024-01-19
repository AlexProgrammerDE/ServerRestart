package me.xginko.serverrestart.commands.subcommands;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.commands.SubCommand;
import me.xginko.serverrestart.enums.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class DisableSubCmd extends SubCommand {

    @Override
    public String getLabel() {
        return "disable";
    }

    @Override
    public TextComponent getDescription() {
        return Component.text("Disable all plugin tasks and listeners.").color(NamedTextColor.GRAY);
    }

    @Override
    public TextComponent getSyntax() {
        return Component.text("/restarts disable").color(NamedTextColor.WHITE);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permissions.DISABLE.get())) {
            sender.sendMessage(Component.text("Disabling SimpleRestarts...").color(NamedTextColor.RED));
            ServerRestart.getInstance().disablePlugin();
            sender.sendMessage(Component.text("Disabled all plugin listeners and tasks.").color(NamedTextColor.GREEN));
            sender.sendMessage(Component.text("You can enable the plugin again using the reload command.").color(NamedTextColor.YELLOW));
        } else {
            sender.sendMessage(ServerRestart.getLang(sender).no_permission);
        }
    }
}