package de.einjojo.coinflip.util;

import de.einjojo.coinflip.messages.MessageKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class BetAmountValidator {

    public @Nullable Integer validateAndParseInput(String bet, Player player) {
        try {
            int amount = Integer.parseInt(bet);
            if (amount <= 0) {
                player.sendMessage(MessageKey.INVALID_NUMBER.getComponent());
                return null;
            }
            return amount;
        } catch (NumberFormatException ex) {
            player.sendMessage(MessageKey.INVALID_NUMBER.getComponent());
        }
        return null;
    }

}
