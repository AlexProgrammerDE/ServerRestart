package me.xginko.serverrestart.paper.commands.subcommands;

import me.xginko.serverrestart.paper.ServerRestart;
import me.xginko.serverrestart.paper.commands.SubCommand;
import me.xginko.serverrestart.paper.enums.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class DisableSubCmd extends SubCommand {

    @Override
    public String label() {
        return "disable";
    }

    @Override
    public TextComponent description() {
        return Component.text("Disables all plugin tasks and listeners.").color(NamedTextColor.GRAY);
    }

    @Override
    public TextComponent syntax() {
        return Component.text("/restarts disable").color(NamedTextColor.WHITE);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.DISABLE.get())) {
            sender.sendMessage(ServerRestart.getLang(sender).no_permission);
            return;
        }

        sender.sendMessage(Component.text("Disabling SimpleRestarts...").color(NamedTextColor.RED));
        ServerRestart.getInstance().disablePlugin();
        sender.sendMessage(Component.text("Disabled all plugin listeners and tasks.").color(NamedTextColor.GREEN));
        sender.sendMessage(Component.text("You can enable the plugin again using the reload command.").color(NamedTextColor.YELLOW));
    }
}