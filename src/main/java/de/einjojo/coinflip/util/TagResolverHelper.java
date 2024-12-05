package de.einjojo.coinflip.util;

import de.einjojo.coinflip.command.SubCommand;
import de.einjojo.coinflip.model.GameRequest;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class TagResolverHelper {

    public static TagResolver[] createRequestResolver(GameRequest gameRequest) {
        return new TagResolver[]{

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
