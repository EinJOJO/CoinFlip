package de.einjojo.coinflip.manager;

import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.economy.SimpleEconomy;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.model.ActiveGame;
import de.einjojo.coinflip.model.GameException;
import de.einjojo.coinflip.model.GameRequest;
import de.einjojo.coinflip.model.GameResult;
import de.einjojo.coinflip.util.TagResolverHelper;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ActiveGameManager {
    private final SimpleEconomy economy = SimpleEconomy.create();
    @Getter
    private final List<ActiveGame> activeGames = new LinkedList<>();
    @Getter
    private final CoinFlipPlugin plugin;

    public ActiveGameManager(CoinFlipPlugin plugin) {
        this.plugin = plugin;
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
            playWinSound(game.getHost(), game);
            playFirework(game.getHost());
            playLooseSound(game.getGuest(), game);
        } else {
            economy.deposit(game.getGuest(), game.getReward());
            playWinSound(game.getGuest(), game);
            playFirework(game.getGuest());
            playLooseSound(game.getHost(), game);
        }
        plugin.getGameHistoryManager().addToHistories(game);
        activeGames.remove(game);
        game.setManager(null);
    }


    public void playWinSound(Player player, ActiveGame game) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        player.sendMessage(MessageKey.GAME__WON.getComponent(
                TagResolverHelper.createBetPlaceholder(game.getResult()),
                Placeholder.unparsed("amount", String.valueOf(game.getPartialAmount() * 2))
        ));
    }

    public void playLooseSound(Player player, ActiveGame game) {
        player.playSound(player, Sound.ENTITY_BLAZE_DEATH, 1, 0.8f);
        player.sendMessage(MessageKey.GAME__LOST.getComponent(
                TagResolverHelper.createBetPlaceholder(game.getResult().reverse()),
                Placeholder.unparsed("amount", String.valueOf(game.getPartialAmount()))
        ));
    }

    public void playFirework(Player player) {

    }

}
