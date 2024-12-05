package de.einjojo.coinflip.command;

import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.command.sub.HelpCommand;
import de.einjojo.coinflip.command.sub.StatsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFCommand implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public CFCommand(CoinFlipPlugin plugin) {
        var helpCommand = new HelpCommand();
        registerSubCommand(helpCommand);
        registerSubCommand(new StatsCommand());

        helpCommand.getSubCommands().addAll(subCommands.values());
    }

    private void registerSubCommand(SubCommand command) {
        subCommands.put(command.getCommand(), command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
