package de.einjojo.coinflip.model;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import de.einjojo.coinflip.CoinFlipPlugin;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class ActiveGame {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final Player host;
    private final Player guest;
    private final GameRequest request;
    private final Title titleHead;
    private final Title titleTail;

    private MyScheduledTask animationTask;
    private CompletableFuture<Void> animationCompletionFuture;
    private GameResult result;


    private int counter;
    private int tickDelay;
    private int ticks;


    public ActiveGame(Player host, Player guest, GameRequest request) {
        this.host = host;
        this.guest = guest;
        this.request = request;
        this.result = RANDOM.nextBoolean() ? GameResult.HEAD : GameResult.TAIL;
        this.titleHead = Title.title(Component.text("Kopf"), Component.text(""));
        this.titleTail = Title.title(Component.text("Zahl"), Component.text(""));
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
            if (ticks == (RANDOM.nextInt(10) + 8)) {
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
        }
        if (guest.isOnline()) {
            guest.showTitle(getTitle(result));
        }
    }

    /**
     * Announces winner and gives out rewards
     */
    public void complete() {
        if (animationTask != null) {
            animationTask.cancel();
            animationTask = null;
        }
        sendGameTitle(result);
    }


}
