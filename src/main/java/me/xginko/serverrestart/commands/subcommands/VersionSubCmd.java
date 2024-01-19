package me.xginko.serverrestart.commands.subcommands;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.commands.SubCommand;
import me.xginko.serverrestart.enums.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class VersionSubCmd extends SubCommand {

    @Override
    public String getLabel() {
        return "version";
    }

    @Override
    public TextComponent getDescription() {
        return Component.text("Show the plugin version.").color(NamedTextColor.GRAY);
    }

    @Override
    public TextComponent getSyntax() {
        return Component.text("/restarts version").color(NamedTextColor.GOLD);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permissions.VERSION.get())) {
            final PluginDescriptionFile pluginYML = ServerRestart.getInstance().getDescription();
            sender.sendMessage(
                    Component.newline()
                    .append(
                            Component.text(pluginYML.getName()+" "+pluginYML.getVersion())
                            .color(NamedTextColor.GOLD)
                            .clickEvent(ClickEvent.openUrl(pluginYML.getWebsite()))
                    )
                    .append(Component.text(" by ").color(NamedTextColor.GRAY))
                    .append(
                            Component.text(pluginYML.getAuthors().get(0))
                            .color(NamedTextColor.DARK_AQUA)
                            .clickEvent(ClickEvent.openUrl("https://github.com/xGinko"))
                    )
                    .append(Component.newline())
            );
        } else {
            sender.sendMessage(ServerRestart.getLang(sender).no_permission);
        }
    }
}