package de.einjojo.coinflip.messages;

import de.einjojo.coinflip.messages.MessageKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

@Getter
@Slf4j
public class MessagesConfig {

    private final YamlConfiguration messagesConfig;
    private final File messagesFile;

    public MessagesConfig(JavaPlugin plugin, String language) {
        messagesFile = new File(plugin.getDataFolder(), "messages_" + language + ".yml");
        if (!messagesFile.exists()) {
            createNewMessageFile(messagesFile);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    @Nullable
    public String getString(@NotNull String path) {
        return messagesConfig.getString(path);
    }

    public @NotNull List<String> getStringList(@NotNull String path) {
        return messagesConfig.getStringList(path);
    }

    public void set(@NotNull String path, @Nullable Object value) {
        messagesConfig.set(path, value);
    }

    public void createNewMessageFile(File file) {
        try {
            if (file.getParentFile().mkdirs()) {
                log.info("Created folder");
            }
            if (file.createNewFile()) {
                log.info("Message File created: {}", file.getName());
            } else {
                log.info("File already exists.");
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (var key : MessageKey.values()) {
                if (key.getDefaultValueArray() == null) {
                    config.set(key.getKey(), key.getDefaultValue());
                } else {
                    config.set(key.getKey(), key.getDefaultValueArray());
                }
            }
            config.save(file);
        } catch (Exception e) {
            log.error("An error occurred while creating the message file", e);
        }
    }

    public boolean save() {
        try {
            messagesConfig.save(messagesFile);
            return true;
        } catch (Exception e) {
            log.error("An error occurred while saving the message file", e);
            return false;
        }
    }
}
