package de.einjojo.coinflip.manager;

import de.einjojo.coinflip.model.GameRequest;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameRequestManager {
    private final Map<UUID, GameRequest> requestMap = new ConcurrentHashMap<>();

    public void registerRequest(GameRequest gameRequest) throws IllegalStateException {
        if (requestMap.containsKey(gameRequest.getRequester())) {
            throw new IllegalStateException("Player already has a game request");
        }
        requestMap.put(gameRequest.getRequester(), gameRequest);
    }

    /**
     * @param requester UUID of the player who requested the flip
     * @return true if invalidated or false if no value was invalidated.
     */
    public boolean invalidateRequest(UUID requester) {
        var removed = requestMap.remove(requester);
        if (removed != null) {
            removed.setManager(null);
            return true;
        }
        return false;

    }

}
