package de.einjojo.coinflip.messages;

import de.einjojo.coinflip.CoinFlipPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

@Getter
public enum MessageKey {
    PREFIX("<gray>[<yellow>Coinflip</yellow>]</gray> "),
    REQUEST__CANCELLED("<prefix>Deine Coinflip Anfrage wurde abgebrochen <reason>"),
    /**
     *
     */
    COMMAND__HELP_LINE("<prefix><yellow><command></yellow> <color:#fffda1><syntax></color> ");

    private final String defaultValue;
    @Nullable
    private final String[] defaultValueArray;
    private final String key;

    MessageKey(String defaultValue) {
        this.key = name().toLowerCase().replaceAll("__", ".");
        this.defaultValue = defaultValue;
        this.defaultValueArray = null;

    }

    MessageKey(String... defaultValueArray) {
        this.key = name().toLowerCase().replaceAll("__", ".");
        this.defaultValue = "";
        this.defaultValueArray = defaultValueArray;
    }

    public List<Component> getList(TagResolver... resolvers) {
        return CoinFlipPlugin.getInstance().getMessageManager().getMessageList(this, resolvers);
    }

    public Component getComponent(TagResolver... tagResolvers) {
        return CoinFlipPlugin.getInstance().getMessageManager().getMessage(this, tagResolvers);
    }

    public String getPlain() {
        return CoinFlipPlugin.getInstance().getMessageManager().getPlain(this);
    }

    /**
     * @param key key format like "prefix" or "internal.plugin_loading_success"
     * @return MessageKey
     * @throws IllegalArgumentException if the key does not exist
     */
    public static MessageKey of(String key) {
        return valueOf(key.replaceAll("\\.", "__").toUpperCase(Locale.ROOT));
    }
}
