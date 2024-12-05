package de.einjojo.coinflip.listener;

import de.einjojo.coinflip.manager.GameRequestManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@Slf4j
public class GameIntegrityListener implements Listener {
    private final GameRequestManager gameRequestManager;


    public GameIntegrityListener(@NonNull GameRequestManager gameRequestManager) {
        this.gameRequestManager = gameRequestManager;
    }

    @EventHandler
    public void abortRequestOnLeave(PlayerQuitEvent event) {
        if (gameRequestManager.cancelRequest(event.getPlayer().getUniqueId())) {
            log.info("Invalidated request because requester has left the server");
        }
    }


}
