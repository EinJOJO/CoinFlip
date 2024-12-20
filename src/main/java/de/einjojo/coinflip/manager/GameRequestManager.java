package de.einjojo.coinflip.manager;

import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.economy.SimpleEconomy;
import de.einjojo.coinflip.gui.RequestsGUI;
import de.einjojo.coinflip.model.GameRequest;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameRequestManager {
    private final SimpleEconomy economy = SimpleEconomy.create();
    private final Map<UUID, GameRequest> requestMap = new ConcurrentHashMap<>();

    /**
     * Upon registration, the money-amount will be withdrawn.
     *
     * @param gameRequest request
     * @throws IllegalStateException thrown if already a request exists
     */
    public void registerRequest(GameRequest gameRequest) throws IllegalStateException {
        if (requestMap.containsKey(gameRequest.getRequester())) {
            throw new IllegalStateException("Requester is already registered");
        }
        var offlinePlayer = Bukkit.getOfflinePlayer(gameRequest.getRequester());
        if (!economy.hasBalance(offlinePlayer, gameRequest.getMoney())) {
            throw new IllegalStateException("Money is not enough");
        }
        economy.withdraw(offlinePlayer, gameRequest.getMoney());
        gameRequest.setManager(this);
        requestMap.put(gameRequest.getRequester(), gameRequest);
        updateGuis();
    }

    /**
     * @return a copy of the current requests
     */
    public List<GameRequest> getRequests() {
        return new ArrayList<>(requestMap.values());
    }


    public boolean cancelRequest(UUID uuid) {
        return cancelRequest(uuid, true);
    }

    /**
     * Will refund the betted money
     *
     * @param uuid player
     * @return true if cancelled and refunded
     */
    public boolean cancelRequest(UUID uuid, boolean updateGui) {
        var removed = requestMap.remove(uuid);
        if (removed == null) {
            return false;
        }
        if (updateGui) {
            updateGuis();
        }
        economy.deposit(Bukkit.getOfflinePlayer(removed.getRequester()), removed.getMoney());
        removed.setManager(null);
        return true;
    }

    public void cancelAllRequests() {
        for (var entry : requestMap.keySet()) {
            cancelRequest(entry, false);
        }
        updateGuis();
    }

    /**
     * Invalidation will not refund the betted money
     *
     * @param requester UUID of the player who requested the flip
     * @return true if invalidated or false if no value was invalidated.
     * @see #cancelRequest(UUID)
     */
    public boolean invalidateRequest(UUID requester) {
        var removed = requestMap.remove(requester);
        if (removed != null) {
            removed.setManager(null);
            updateGuis();
            return true;
        }
        return false;

    }

    /**
     * thread safe
     */
    public void updateGuis() {
        if (Bukkit.isPrimaryThread()) {
            for (var gui : RequestsGUI.ACTIVE_GUIS) {
                gui.update();
            }
        } else {
            CoinFlipPlugin.getInstance().getScheduler().runTask(() -> {
                for (var gui : RequestsGUI.ACTIVE_GUIS) {
                    gui.update();
                }
            });
        }
    }

}
