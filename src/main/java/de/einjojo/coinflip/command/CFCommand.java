package de.einjojo.coinflip.command;

import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.command.sub.*;
import de.einjojo.coinflip.gui.RequestsGUI;
import de.einjojo.coinflip.messages.MessageKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CFCommand implements CommandExecutor, TabCompleter, SubCommand {
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final CoinFlipPlugin plugin;

    public CFCommand(CoinFlipPlugin plugin) {
        this.plugin = plugin;
        var helpCommand = new HelpCommand();
        registerSubCommand(helpCommand);
        registerSubCommand(new StatsCommand());
        registerSubCommand(new RequestCancelCommand(plugin));
        registerSubCommand(new ReloadCommand(plugin));
        registerSubCommand(new BetCommand(plugin));

        helpCommand.getSubCommands().addAll(subCommands.values());
    }

    private void registerSubCommand(SubCommand command) {
        subCommands.put(command.getCommand(), command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        execute(sender, args);
        return true;
    }

    public void openRequestGUI(CommandSender sender) {
        if (sender instanceof Player player) {
            player.closeInventory();
            new RequestsGUI(plugin.getGameRequestManager(), player);
        } else {
            sender.sendMessage(MessageKey.COMMAND__REQUIRES_PLAYER.getComponent());
        }
    }

    public void handleUnknown(CommandSender sender, String unknown) {
        try {
            Integer.parseInt(unknown);
        } catch (NumberFormatException ex) {
            sender.sendMessage(MessageKey.INVALID_NUMBER.getComponent());
        }
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command _command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args.length == 1) {
            return subCommands.values().stream()
                    .filter(command -> command.getPermission() == null || sender.hasPermission(command.getPermission()))
                    .map(SubCommand::getCommand)
                    .filter(command -> command.startsWith(args[0])).toList();
        }
        SubCommand command = subCommands.values().stream().filter(subCommand -> subCommand.getCommand().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (command != null) {
            return command.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            openRequestGUI(sender);
            return;
        }
        SubCommand subCommand = subCommands.get(args[0]);
        if (subCommand == null) {
            handleUnknown(sender, args[0]);
            return;
        }
        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(MessageKey.COMMAND__NO_PERMISSION.getComponent());
            return;
        }
        subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public @NotNull String getCommand() {
        return "";
    }

    @Override
    public @Nullable Set<String> getAliases() {
        return Set.of();
    }

    @Override
    public @NotNull String getSyntaxSuggestion() {
        return "[betrag]";
    }

    @Override
    public @NotNull String getDescription() {
        return "";
    }

    @Override
    public @Nullable String getPermission() {
        return "";
    }
}
