package de.einjojo.coinflip.command.sub;

import de.einjojo.coinflip.command.SubCommand;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.util.TagResolverHelper;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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
        helpBuilder.append(MessageKey.PREFIX.getComponent().append(Component.text(" Hilfe", NamedTextColor.GRAY)));
        for (var subCommand : subCommands) {
            String permission = subCommand.getPermission();
            if (permission != null && !sender.hasPermission(permission)) {
                continue;
            }
            helpBuilder.appendNewline();
            helpBuilder.append(MessageKey.COMMAND__HELP_LINE.getComponent(
                    TagResolverHelper.createSubCommandResolver(subCommand)
            ));
        }
        sender.sendMessage(helpBuilder.build());
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
    public @NotNull String getSyntaxSuggestion() {
        return "";
    }

    @Override
    public @NotNull String getDescription() {
        return "Zeige dir eine Befehls-Übersicht an";
    }

    @Override
    public @Nullable String getPermission() {
        return null;
    }
}
