package de.einjojo.coinflip.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface SubCommand {

    void execute(CommandSender sender, String[] args);

    List<String> tabComplete(CommandSender sender, String[] args);

    @NotNull String getCommand();

    @Nullable Set<String> getAliases();

    default boolean isAlias(String cmd) {
        if (getAliases() == null) return false;
        return getAliases().contains(cmd);
    }

    @NotNull String getSyntaxSuggestion();

    @NotNull String getDescription();

    @Nullable String getPermission();


}
