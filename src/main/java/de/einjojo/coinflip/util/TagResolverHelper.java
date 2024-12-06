package de.einjojo.coinflip.util;

import de.einjojo.coinflip.command.SubCommand;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.model.GameRequest;
import de.einjojo.coinflip.model.GameResult;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.logging.log4j.message.Message;

public class TagResolverHelper {

    public static TagResolver[] createRequestResolver(GameRequest gameRequest) {
        return new TagResolver[]{
                Placeholder.component("!bet", gameRequest.getBet() == GameResult.HEAD ? MessageKey.TAIL.getComponent() : MessageKey.HEAD.getComponent()),
                Placeholder.component("bet", gameRequest.getBet() == GameResult.HEAD ? MessageKey.HEAD.getComponent() : MessageKey.TAIL.getComponent()),
                Placeholder.unparsed("player", gameRequest.getRequesterPlayer().getName()),
                Placeholder.unparsed("money", String.valueOf(gameRequest.getMoney())),
                Placeholder.unparsed("amount", String.valueOf(gameRequest.getMoney()))

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

}
