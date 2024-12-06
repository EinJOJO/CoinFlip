package de.einjojo.coinflip;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import de.einjojo.coinflip.command.CFCommand;
import de.einjojo.coinflip.listener.GameIntegrityListener;
import de.einjojo.coinflip.manager.ActiveGameManager;
import de.einjojo.coinflip.manager.GameHistoryManager;
import de.einjojo.coinflip.manager.GameRequestManager;
import de.einjojo.coinflip.messages.MessageManager;
import de.einjojo.coinflip.model.ActiveGame;
import de.einjojo.coinflip.storage.ConnectionProvider;
import de.einjojo.coinflip.storage.SQLHistoryStorage;
import de.einjojo.coinflip.storage.SQLLiteConnectionProvider;
import lombok.Getter;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public class CoinFlipPlugin extends JavaPlugin {
    @Getter
    private static CoinFlipPlugin instance;
    private final GameRequestManager gameRequestManager = new GameRequestManager();
    private final TaskScheduler scheduler = UniversalScheduler.getScheduler(this);
    private final MessageManager messageManager = new MessageManager(this);


    private ConnectionProvider connectionProvider;
    private GameHistoryManager gameHistoryManager;
    private ActiveGameManager activeGameManager;

    @Override
    public void onEnable() {
        instance = this;
        messageManager.loadMessages("de");
        connectionProvider = new SQLLiteConnectionProvider(this);
        SQLHistoryStorage historyStorage = new SQLHistoryStorage(connectionProvider);
        historyStorage.init();
        gameHistoryManager = new GameHistoryManager(historyStorage);
        activeGameManager = new ActiveGameManager(gameHistoryManager);
        gameHistoryManager.load();
        registerListeners();
        registerCommands();
        registerUpdateScheduler();
    }

    @Override
    public void onDisable() {
        getGameRequestManager().cancelAllRequests(); // refund
        if (activeGameManager != null) {
            for (ActiveGame game : activeGameManager.getActiveGames()) {
                game.complete();
            }
        }
        if (gameHistoryManager != null) {
            gameHistoryManager.updateAll();
        }
    }

    private void registerUpdateScheduler() {
        int interval = 20 * 60; // every minute
        getScheduler().runTaskTimerAsynchronously(gameHistoryManager::updateAll, interval, interval);
    }

    private void registerCommands() {
        PluginCommand pluginCommand = Objects.requireNonNull(getCommand("coinflip"), "Forgot registering 'coinflip' command in plugin.yml?");
        CFCommand cfCommand = new CFCommand(this);
        pluginCommand.setExecutor(cfCommand);
        pluginCommand.setTabCompleter(cfCommand);
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new GameIntegrityListener(gameRequestManager), this);
    }
}
