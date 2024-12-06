package de.einjojo.coinflip.command.sub;

import de.einjojo.coinflip.command.SubCommand;
import de.einjojo.coinflip.manager.GameHistoryManager;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.util.TagResolverHelper;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StatsCommand implements SubCommand {
    private final GameHistoryManager historyManager;

    public StatsCommand(@NonNull GameHistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                showStats(sender, player);
            } else {
                sender.sendMessage(MessageKey.COMMAND__REQUIRES_PLAYER.getComponent());
            }
        } else if (args.length == 1) {
            if (!sender.hasPermission("coinflip.stats.other")) {
                sender.sendMessage(MessageKey.COMMAND__NO_PERMISSION.getComponent());
                return;
            }
            var player = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (player == null) {
                sender.sendMessage(MessageKey.COMMAND__PLAYER_NOT_FOUND.getComponent());
                return;
            }
            showStats(sender, player);
        }
    }

    public void showStats(CommandSender sender, OfflinePlayer player) {
        var history = historyManager.getGameHistory(player.getUniqueId());
        var builder = Component.text();
        for (var comp : MessageKey.COMMAND__STATS_INFO.getList(TagResolverHelper.createHistoryResolver(history))) {
            builder.append(comp).appendNewline();
        }
        sender.sendMessage(builder.build());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> list = new LinkedList<>();
        String filter = args.length > 0 ? args[0] : "";
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().startsWith(filter)) {
                list.add(p.getName());
            }
        }
        if (list.isEmpty()) {
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                String name = p.getName();
                if (name == null) continue;
                if (name.startsWith(filter)) {
                    list.add(p.getName());
                }
            }
        }
        return list;
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

    @Override
    public @NotNull String getSyntaxSuggestion() {
        return "[Spieler]";
    }
}
