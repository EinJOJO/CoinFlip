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
    private final long money;

    private transient @Nullable GameRequestManager manager;

    public GameRequest(UUID requester, GameResult bet, long money) {
        this.requester = requester;
        this.bet = bet;
        this.money = money;
    }


    /**
     * @param cancelReason Can be null if no message should be sent.
     * @return true if request has been cancelled successfully
     */
    public boolean cancel(@Nullable String cancelReason) {
        if (manager == null) return false;
        if (cancelReason != null && isRequesterOnline()) {
            getRequesterPlayer().sendMessage(MessageKey.REQUEST__CANCELLED.getComponent(
                    TagResolverHelper.createRequestResolver(this)
            ));
        }
        manager.invalidateRequest(requester);
        return true;
    }


    public boolean isRequesterOnline() {
        return Bukkit.getPlayer(requester) != null;
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
