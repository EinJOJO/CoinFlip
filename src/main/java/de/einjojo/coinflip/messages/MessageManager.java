package de.einjojo.coinflip.messages;

import de.einjojo.coinflip.CoinFlipPlugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageManager {
    private static final String NOT_LOADED = "Looks like the messages are not loaded yet.";
    private final CoinFlipPlugin plugin;
    /**
     * The messages loaded for the current language.
     */
    private @Nullable ConcurrentHashMap<String, String> messages;
    private @Nullable ConcurrentHashMap<String, List<String>> messagesLists;
    @Getter
    private MiniMessage miniMessage = MiniMessage.miniMessage();

    public MessageManager(CoinFlipPlugin plugin) {
        this.plugin = plugin;
    }


    /**
     * Loads the messages for the specified language.
     *
     * @param language language code
     * @return true if the messages were loaded successfully, false otherwise.
     */
    public boolean loadMessages(String language) {
        // Load messages
        messages = new ConcurrentHashMap<>();
        messagesLists = new ConcurrentHashMap<>();

        try {
            var messagesConfig = new MessagesConfig(plugin, language);
            for (MessageKey key : MessageKey.values()) {
                var defaultValueArray = key.getDefaultValueArray();
                if (defaultValueArray == null) {
                    var read = messagesConfig.getString(key.getKey());
                    if (read == null) {
                        log.info("Message key {} added to messages file", key.getKey());
                        messagesConfig.set(key.getKey(), key.getDefaultValue());
                        messages.put(key.getKey(), key.getDefaultValue());
                    } else {
                        messages.put(key.getKey(), read);
                    }
                } else {
                    var read = messagesConfig.getStringList(key.getKey());
                    if (read.isEmpty()) {
                        log.info("Message-List key {} added to messages file", key.getKey());
                        messagesConfig.set(key.getKey(), defaultValueArray);
                        messagesLists.put(key.getKey(), List.of(defaultValueArray));
                    } else {
                        messagesLists.put(key.getKey(), read);
                    }
                }
            }
            messagesConfig.save();
            miniMessage = MiniMessage.builder().editTags(builder -> { //overwrite
                builder.tag("prefix", Tag.selfClosingInserting(getMessage(MessageKey.PREFIX)));
            }).build();
        } catch (Exception e) {
            log.error("Failed to load messages", e);
        }
        return false;
    }

    public CompletableFuture<Boolean> loadMessagesAsync(String language) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        plugin.getScheduler().runTaskAsynchronously(() -> {
            future.complete(loadMessages(language));
        });
        return future;
    }

    public @NotNull String getPlain(MessageKey messageKey) {
        String plain = Objects.requireNonNull(messages, NOT_LOADED).get(messageKey.getKey());
        if (plain == null) {
            handleUnknownMessage(messageKey);
            return messageKey.getDefaultValue();
        }
        return plain;
    }

    /**
     * @param messageKey requires default value array
     * @return list of strings
     * @throws IllegalArgumentException if the message key does not have a default value array
     */
    public @NotNull List<String> getPlainList(MessageKey messageKey) {
        if (messageKey.getDefaultValueArray() == null) {
            throw new IllegalArgumentException("Message key does not have a default value array.");
        }
        return (Objects.requireNonNull(messagesLists, NOT_LOADED).get(messageKey.getKey()));
    }

    public @NotNull Component getMessage(MessageKey message, TagResolver... tagResolvers) {
        return Objects.requireNonNull(miniMessage, NOT_LOADED).deserialize(getPlain(message), tagResolvers);
    }

    public @NotNull List<Component> getMessageList(MessageKey message, TagResolver... tagResolvers) {
        MiniMessage notNullMiniMessage = Objects.requireNonNull(miniMessage, NOT_LOADED);
        return getPlainList(message).stream().map(s -> notNullMiniMessage.deserialize(s, tagResolvers)).toList();
    }

    public void sendMessage(@NotNull CommandSender sender, @NotNull MessageKey message, TagResolver... tagResolvers) {
        sender.sendMessage(getMessage(message, tagResolvers));
    }

    protected void handleUnknownMessage(MessageKey messageKey) {
        log.warn("Unknown message key: {}", messageKey.getKey());
    }

    /**
     * Returns the message with the specified key.
     *
     * @return the message
     */
    public boolean hasLoadedMessages() {
        return messages != null;
    }

}
