package de.einjojo.coinflip.model;

import de.einjojo.coinflip.manager.GameRequestManager;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.util.TagResolverHelper;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@Setter
public class GameRequest {
    private final UUID requester;
    private final GameResult bet;
    private final int money;
    private long createdAt = System.currentTimeMillis();

    private transient @Nullable GameRequestManager manager;

    public GameRequest(UUID requester, GameResult bet, int money) {
        this.requester = requester;
        this.bet = bet;
        this.money = money;
    }


    /**
     * Will refund the money
     *
     * @return true if request has been cancelled successfully
     */
    public boolean cancel() {
        if (manager == null) return false;
        return manager.cancelRequest(requester);
    }


    public boolean isRequesterOnline() {
        return Bukkit.getOfflinePlayer(requester).isOnline();
    }


    public boolean isValid() {
        return manager != null;
    }

    /**
     * @return Player
     * @throws IllegalStateException if player is not online
     * @see #isRequesterOnline()
     */
    public @NotNull Player getRequesterPlayer() throws IllegalStateException {
        var player = Bukkit.getPlayer(requester);
        if (player == null) {
            throw new IllegalStateException("Player not online");
        }
        return player;
    }


}
