package de.einjojo.coinflip.command.sub;


import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ReloadCommand implements SubCommand {
    private final CoinFlipPlugin plugin;
    private CompletableFuture<Boolean> futureLock;

    public ReloadCommand(CoinFlipPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (futureLock != null && !futureLock.isDone()) {
            sender.sendRichMessage("<red>please wait until loading is done...");
            return;
        }
        futureLock = plugin.getMessageManager().loadMessagesAsync("de").whenComplete((success, throwable) -> {
            if (throwable != null) {
                sender.sendRichMessage("<red>Fehler beim Neuladen: " + throwable.getMessage());
                return;
            }
            if (success) {
                sender.sendMessage("<green>Config erfolgreich neu geladen!");
            } else {
                sender.sendMessage("<red>Fehler beim Neuladen!");
            }

        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public @NotNull String getCommand() {
        return "reload";
    }

    @Override
    public @Nullable Set<String> getAliases() {
        return Set.of("rl");
    }

    @Override
    public @NotNull String getSyntaxSuggestion() {
        return "";
    }

    @Override
    public @NotNull String getDescription() {
        return "Lade die Config neu";
    }

    @Override
    public @Nullable String getPermission() {
        return "coinflip.reload";
    }
}
