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
    REQUEST__CANCELLED("<prefix>Deine Coinflip Anfrage wurde abgebrochen."),
    COMMAND__HELP_LINE("<prefix><yellow><command></yellow> <color:#fffda1><syntax></color> "),
    COMMAND__REQUIRES_PLAYER("<prefix><red>Nur für Spieler"),
    COMMAND__NO_PERMISSION("<prefix><red>Dazu hast du keine Rechte"),
    INVALID_NUMBER("<prefix><red>Ungültige Zahl"),
    REQUEST_GUI__NAME("Coinflip"),
    REQUEST_GUI__PAGING_ITEM__PREVIOUS_PAGE("<green>Vorherige Seite"),
    REQUEST_GUI__PAGING_ITEM__NEXT_PAGE("<green>Nächste Seite"),
    REQUEST_GUI__REQUEST_ITEM__NAME("<player>"),
    REQUEST_GUI__REQUEST_ITEM__LORE("",
            "<player>",
            "<amount>",
            "<bet>"),
    REQUEST_GUI__REQUEST_ITEM__CLICK_SUGGESTION("[Linksklick] Mit <amount> auf <anti_bet> setzen"),
    NO_GAME_REQUEST("<prefix><red>Du hast keine Coinflip Wette inseriert."),
    INPUT_CANCEL_SUBTITLE("<gray>Gebe <red>cancel</red> in den Chat ein, um abzubrechen."),
    HEAD("<b><yellow>KOPF</yellow></b>"),
    TAIL("<b><#ffffff>Zahl</#ffffff></b>"),
    INPUT_TITLE_ENTER_BET("Wettbetrag eingeben"),
    INPUT_CANCELLED_INFO("<prefix>Abgebrochen"),
    REQUEST__CREATED("<prefix>Du hast eine Wette erstellt und auf <bet> gesetzt"),
    REQUEST__NOT_CREATED_BECAUSE_ALREADY_EXISTS("<prefix><red>Du hast bereits eine Wette am laufen."),
    REQUEST__NOT_CREATED_UNKNOWN("<prefix><red>Deine Wette konnte nicht gestellt werden.");


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
