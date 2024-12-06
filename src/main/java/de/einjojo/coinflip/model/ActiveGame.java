package de.einjojo.coinflip.model;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.manager.ActiveGameManager;
import de.einjojo.coinflip.messages.MessageKey;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;


@Setter
public class ActiveGame {
    private static final SecureRandom RANDOM = new SecureRandom();
    @Getter
    private final Player host;
    @Getter
    private final Player guest;
    @Getter
    private final GameRequest request;
    private final Title titleHead;
    private final Title titleTail;

    private MyScheduledTask animationTask;
    private CompletableFuture<Void> animationCompletionFuture;

    @Getter
    private GameResult result;

    @Getter
    private transient ActiveGameManager manager;

    private int counter;
    private int tickDelay;
    private int ticks;


    public ActiveGame(GameRequest request, Player guest) {
        this.host = request.getRequesterPlayer();
        this.guest = guest;
        this.request = request;
        this.result = RANDOM.nextBoolean() ? GameResult.HEAD : GameResult.TAIL;
        this.titleHead = Title.title(MessageKey.HEAD.getComponent(), Component.text(""));
        this.titleTail = Title.title(MessageKey.TAIL.getComponent(), Component.text(""));
    }

    public boolean isHostWinner() {
        return getRequest().getBet().equals(result);
    }

    public int getPartialAmount() {
        return getRequest().getMoney();
    }

    public int getReward() {
        return getRequest().getMoney() * 2;
    }

    /**
     * @param plugin plugin
     * @return a future that completes when animation is finished
     */
    public CompletableFuture<Void> playAnimation(CoinFlipPlugin plugin) {
        if (animationCompletionFuture != null && animationCompletionFuture.isDone()) {
            throw new IllegalStateException("animation has been played already");
        }

        if (animationTask == null) {
            animationCompletionFuture = new CompletableFuture<>();
            animationTask = plugin.getScheduler().runTaskTimerAsynchronously(this::tickAnimationAsync, 1, 2);

        }
        return animationCompletionFuture;
    }

    protected void tickAnimationAsync() {
        counter++;
        if (counter >= tickDelay) {
            counter = 0;
            ticks++;
            if (ticks > (RANDOM.nextInt(5) + 4)) {
                tickDelay++;
            }
            if (ticks >= (RANDOM.nextInt(8) + 6)) {
                complete();
                return;
            }
            sendGameTitle(ticks % 2 == 0 ? GameResult.TAIL : GameResult.HEAD);
        }
    }

    public Title getTitle(GameResult result) {
        if (result == GameResult.HEAD) {
            return titleHead;
        } else {
            return titleTail;
        }
    }

    public void sendGameTitle(GameResult result) {
        if (host.isOnline()) {
            host.showTitle(getTitle(result));
            host.playSound(host, Sound.ENTITY_BAT_TAKEOFF, 1, 1.3f);
        }
        if (guest.isOnline()) {
            guest.showTitle(getTitle(result));
            host.playSound(host, Sound.ENTITY_BAT_TAKEOFF, 1, 1.3f);
        }
    }

    /**
     * Announces winner and gives out rewards
     */
    public void complete() {
        if (animationTask != null && animationCompletionFuture != null) {
            animationCompletionFuture.complete(null);
            animationCompletionFuture = null;
            animationTask.cancel();
            animationTask = null;
            if (getManager() != null) {
                getManager().completeGame(this);
            } else {
                throw new IllegalStateException("Manager reference is null - No Rewards will be applied");
            }
        } else {
            throw new IllegalStateException("Game is not active");
        }

    }


}
