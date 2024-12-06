package de.einjojo.coinflip.model;

import lombok.Getter;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class GameHistory {
    @Getter
    private final UUID uuid;
    private final LinkedList<Entry> entries = new LinkedList<>();
    private List<Entry> added = new LinkedList<>();
    @Getter
    private int wins;
    @Getter
    private int lostMoney;
    @Getter
    private int wonMoney;
    @Getter
    private Instant lastGame;

    public GameHistory(UUID uuid) {
        this.uuid = uuid;
    }


    public void add(ActiveGame game) {
        boolean isHostWinner = game.getRequest().getBet().equals(game.getResult());
        UUID winner = isHostWinner ? game.getHost().getUniqueId() : game.getGuest().getUniqueId();
        UUID loser = isHostWinner ? game.getGuest().getUniqueId() : game.getHost().getUniqueId();
        var entry = new Entry(winner, loser, game.getPartialAmount(), game.getResult(), Instant.now());
        added.add(entry);
        addEntry(entry);
    }

    public void addEntry(GameHistory.Entry entry) {
        if (entry.winner().equals(uuid)) {
            wins += 1;
            wonMoney += entry.amount();
        } else {
            lostMoney += entry.amount();
        }
        if (lastGame != null) {
            if (lastGame.compareTo(entry.timestamp()) < 0) {
                lastGame = entry.timestamp();
            }
        } else {
            lastGame = entry.timestamp();
        }
        entries.add(entry);
    }

    public int getLostGames() {
        return getTotalGames() - getWins();
    }

    public int getTotalGames() {
        return entries.size();
    }

    public List<Entry> flush() {
        final List<Entry> added = this.added;
        this.added = new LinkedList<>();
        return added;
    }

    public boolean isDirty() {
        return !added.isEmpty();
    }


    public LinkedList<Entry> getEntries() {
        return new LinkedList<>(entries);
    }

    public record Entry(UUID winner, UUID loser, int amount, GameResult result, Instant timestamp) {
        public int getTotal() {
            return 2 * amount;
        }
    }


}
