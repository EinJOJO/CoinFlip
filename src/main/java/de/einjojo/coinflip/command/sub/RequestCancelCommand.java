package de.einjojo.coinflip.command.sub;

import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.command.SubCommand;
import de.einjojo.coinflip.messages.MessageKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class RequestCancelCommand implements SubCommand {

    private final CoinFlipPlugin plugin;

    public RequestCancelCommand(CoinFlipPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageKey.COMMAND__REQUIRES_PLAYER.getComponent());
            return;
        }
        if (plugin.getGameRequestManager().cancelRequest(player.getUniqueId())) {
            player.sendMessage(MessageKey.REQUEST__CANCELLED.getComponent());
        } else {
            player.sendMessage(MessageKey.NO_GAME_REQUEST.getComponent());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public @NotNull String getCommand() {
        return "cancel";
    }

    @Override
    public @Nullable Set<String> getAliases() {
        return Set.of();
    }

    @Override
    public @NotNull String getSyntaxSuggestion() {
        return "";
    }

    @Override
    public @NotNull String getDescription() {
        return "Breche deine Coinflip-Wette ab";
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }
}
