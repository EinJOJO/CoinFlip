package de.einjojo.coinflip.command.sub;

import de.einjojo.coinflip.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class StatsCommand implements SubCommand {
    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public @NotNull String getCommand() {
        return "stats";
    }

    @Override
    public @Nullable Set<String> getAliases() {
        return null;
    }

    @Override
    public @NotNull String getDescription() {
        return "Schaue dir die Coinflip Statistiken an";
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }
}
