package de.einjojo.coinflip.command.sub;

import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.command.SubCommand;
import de.einjojo.coinflip.gui.BetGui;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.util.BetAmountValidator;
import de.einjojo.coinflip.util.PlayerChatInput;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class BetCommand implements SubCommand {
    private final CoinFlipPlugin plugin;
    private final BetAmountValidator parser = new BetAmountValidator();

    public BetCommand(CoinFlipPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageKey.COMMAND__REQUIRES_PLAYER.getComponent());
            return;
        }
        if (args.length == 0) {
            new PlayerChatInput(plugin, player, MessageKey.INPUT_ENTER_BET.getComponent(), (input) -> {
                startBet(player, input);
            });
        } else {
            startBet(player, args[0]);
        }
    }

    public void startBet(Player player, String input) {
        Integer bet = parser.validateAndParseInput(input, player);
        if (bet != null) {
            new BetGui(plugin.getGameRequestManager(), bet, player);
        }
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of("50", "100", "800");
    }

    @Override
    public @NotNull String getCommand() {
        return "bet";
    }

    @Override
    public @Nullable Set<String> getAliases() {
        return Set.of("wetten");
    }

    @Override
    public @NotNull String getSyntaxSuggestion() {
        return "[betrag]";
    }

    @Override
    public @NotNull String getDescription() {
        return "Eröffne eine Wette";
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }
}
