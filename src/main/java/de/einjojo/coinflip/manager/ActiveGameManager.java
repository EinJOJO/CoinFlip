package de.einjojo.coinflip.manager;

import de.einjojo.coinflip.economy.SimpleEconomy;
import de.einjojo.coinflip.model.ActiveGame;
import de.einjojo.coinflip.model.GameException;
import de.einjojo.coinflip.model.GameRequest;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ActiveGameManager {
    private final SimpleEconomy economy = SimpleEconomy.create();
    @Getter
    private final List<ActiveGame> activeGames = new LinkedList<>();

    public ActiveGameManager() {
    }


    public ActiveGame startCoinFlip(GameRequest request, Player guest) throws GameException {
        // Condition checks
        if (request.getRequester().equals(guest.getUniqueId())) {
            throw new GameException(GameException.Reason.GUEST_IS_REQUESTER, request, guest.getUniqueId());
        }
        if (!economy.hasBalance(guest, request.getMoney())) {
            throw new GameException(GameException.Reason.GUEST_NOT_ENOUGH_MONEY, request, guest.getUniqueId());
        }
        if (!request.isValid()) {
            throw new GameException(GameException.Reason.REQUEST_INVALID, request, guest.getUniqueId());
        }
        if (!Objects.requireNonNull(request.getManager()).invalidateRequest(request.getRequester())) {
            throw new GameException(GameException.Reason.REQUEST_INVALID, request, guest.getUniqueId());
        }


        var game = new ActiveGame(request, guest);
        registerGame(game);
        return game;
    }

    public void registerGame(ActiveGame game) {
        game.setManager(this);
        activeGames.add(game);
    }

    public void completeGame(ActiveGame game) {
        if (game.isHostWinner()) {
            economy.deposit(game.getHost(), game.getReward());
        } else {
            economy.deposit(game.getGuest(), game.getReward());
        }
        //TODO stats
        activeGames.remove(game);
        game.setManager(null);


    }

}
