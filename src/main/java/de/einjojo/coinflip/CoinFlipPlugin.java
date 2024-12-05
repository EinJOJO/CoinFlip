package de.einjojo.coinflip;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import de.einjojo.coinflip.command.CFCommand;
import de.einjojo.coinflip.listener.GameIntegrityListener;
import de.einjojo.coinflip.manager.GameRequestManager;
import de.einjojo.coinflip.messages.MessageManager;
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

    @Override
    public void onEnable() {
        instance = this;
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {

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