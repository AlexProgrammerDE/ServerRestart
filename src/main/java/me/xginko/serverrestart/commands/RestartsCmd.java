package me.xginko.serverrestart.commands;

import me.xginko.serverrestart.ServerRestart;
import me.xginko.serverrestart.commands.subcommands.DisableSubCmd;
import me.xginko.serverrestart.commands.subcommands.ReloadSubCmd;
import me.xginko.serverrestart.commands.subcommands.VersionSubCmd;
import me.xginko.serverrestart.enums.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RestartsCmd implements TabCompleter, CommandExecutor {

    private final List<SubCommand> subCommands;
    private final List<String> tabCompleter;
    private static final List<String> NO_COMPLETE = Collections.emptyList();

    public RestartsCmd() {
        subCommands = List.of(new ReloadSubCmd(), new VersionSubCmd(), new DisableSubCmd());
        tabCompleter = subCommands.stream().map(SubCommand::label).toList();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return args.length == 1 ? tabCompleter : NO_COMPLETE;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            this.showCmdHelp(sender);
            return true;
        }

        boolean cmdExists = false;
        for (SubCommand subCommand : subCommands) {
            if (args[0].equalsIgnoreCase(subCommand.label())) {
                subCommand.perform(sender, args);
                cmdExists = true;
                break;
            }
        }

        if (!cmdExists) {
            this.showCmdHelp(sender);
        }

        return true;
    }

    private void showCmdHelp(CommandSender sender) {
        if (Arrays.stream(Permissions.values()).noneMatch(perm -> sender.hasPermission(perm.get()))) return;

        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text(ServerRestart.getInstance().getPluginMeta().getName() + " Commands").color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
        subCommands.forEach(subCommand -> sender.sendMessage(
                subCommand.syntax().append(Component.text(" - ").color(NamedTextColor.DARK_GRAY)).append(subCommand.description())));
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
    }
}