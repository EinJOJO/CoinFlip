package de.einjojo.coinflip.command.sub;

import de.einjojo.coinflip.command.SubCommand;
import de.einjojo.coinflip.messages.MessageKey;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

@Getter
public class HelpCommand implements SubCommand {

    private final LinkedList<SubCommand> subCommands = new LinkedList<>();


    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent.Builder helpBuilder = Component.text();
        for (var subCommand : subCommands) {
            helpBuilder.appendNewline();
            helpBuilder.append(createHelpLine(subCommand));
        }
        sender.sendMessage(helpBuilder.build());
    }

    private Component createHelpLine(SubCommand subCommand) {
        return MessageKey.COMMAND__HELP_LINE.getComponent(
                Placeholder.unparsed("syntax", subCommand.getSyntaxSuggestion()),
                Placeholder.unparsed("command", subCommand.getCommand()),
                Placeholder.unparsed("description", subCommand.getDescription())
        );
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    public @NotNull String getCommand() {
        return "help";
    }

    @Override
    public @Nullable Set<String> getAliases() {
        return Set.of();
    }

    @Override
    public @NotNull String getDescription() {
        return "";
    }

    @Override
    public @Nullable String getPermission() {
        return "";
    }
}
