package de.einjojo.coinflip.util;

import de.einjojo.coinflip.command.SubCommand;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.model.GameHistory;
import de.einjojo.coinflip.model.GameRequest;
import de.einjojo.coinflip.model.GameResult;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.logging.log4j.message.Message;

public class TagResolverHelper {

    public static TagResolver[] createRequestResolver(GameRequest gameRequest) {
        return new TagResolver[]{
                Placeholder.component("anti_bet", gameRequest.getBet() == GameResult.HEAD ? MessageKey.TAIL.getComponent() : MessageKey.HEAD.getComponent()),
                createBetPlaceholder(gameRequest.getBet()),
                Placeholder.unparsed("player", gameRequest.getRequesterPlayer().getName()),
                Placeholder.unparsed("money", String.valueOf(gameRequest.getMoney())),
                Placeholder.unparsed("amount", String.valueOf(gameRequest.getMoney()))

        };
    }

    public static TagResolver[] createHistoryResolver(GameHistory history) {
        return new TagResolver[]{
                Placeholder.unparsed("wins", String.valueOf(history.getWins())),
                Placeholder.unparsed("lost", String.valueOf(history.getLostGames())),
                Placeholder.unparsed("games", String.valueOf(history.getTotalGames())),
                Placeholder.unparsed("lost_money", String.valueOf(history.getLostMoney())),
                Placeholder.unparsed("won_money", String.valueOf(history.getWonMoney())),
        };
    }

    public static TagResolver[] createSubCommandResolver(SubCommand subCommand) {
        String alias = subCommand.getAliases() == null ? "-" : subCommand.getAliases().stream().reduce("", (partial, element) -> partial + ", " + element);
        String permission = subCommand.getPermission() == null ? "-" : subCommand.getPermission();
        return new TagResolver[]{
                Placeholder.unparsed("command", subCommand.getCommand()),
                Placeholder.unparsed("description", subCommand.getDescription()),
                Placeholder.unparsed("permission", permission),
                Placeholder.unparsed("syntax", subCommand.getSyntaxSuggestion()),
                Placeholder.unparsed("alias", alias),
        };
    }

    public static TagResolver createBetPlaceholder(GameResult result) {
        return Placeholder.component("bet", result == GameResult.HEAD ? MessageKey.HEAD.getComponent() : MessageKey.TAIL.getComponent());
    }
}
