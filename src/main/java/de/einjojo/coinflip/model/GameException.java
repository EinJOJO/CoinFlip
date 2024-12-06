package de.einjojo.coinflip.model;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class GameException extends Exception {
    private final GameRequest request;
    private final UUID guest;
    private final Reason reason;

    public GameException(Reason reason, GameRequest request, UUID guest) {
        super(reason.name());
        this.request = request;
        this.guest = guest;
        this.reason = reason;
    }

    public enum Reason {
        REQUEST_INVALID,
        GUEST_NOT_ENOUGH_MONEY,
        GUEST_IS_REQUESTER
    }
}
