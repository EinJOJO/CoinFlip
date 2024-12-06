package de.einjojo.coinflip.manager;

import de.einjojo.coinflip.model.ActiveGame;
import de.einjojo.coinflip.model.GameHistory;
import de.einjojo.coinflip.storage.ConnectionProvider;
import de.einjojo.coinflip.storage.SQLHistoryStorage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

@Slf4j
public class GameHistoryManager {
    private final SQLHistoryStorage storage;
    private final Map<UUID, GameHistory> gameHistory = new HashMap<>();

    public GameHistoryManager(SQLHistoryStorage storage) {
        this.storage = storage;
    }


    @Blocking
    public void load() {
        gameHistory.clear();
        long startTime = System.currentTimeMillis();
        List<GameHistory.Entry> historyEntryList = storage.readAllGameHistoryEntries();
        for (var entry : historyEntryList) {
            getGameHistory(entry.loser()).addEntry(entry);
            getGameHistory(entry.winner()).addEntry(entry);
        }
        long deltaTime = System.currentTimeMillis() - startTime;
        log.info("Loaded {} game histories in {}ms", gameHistory.size(), deltaTime);
    }

    public void addToHistories(ActiveGame activeGame) {
        getGameHistory(activeGame.getHost().getUniqueId()).add(activeGame);
        getGameHistory(activeGame.getGuest().getUniqueId()).add(activeGame);
    }

    @Blocking
    public void updateAll() {
        int affectedHistories = 0;
        long startTime = System.currentTimeMillis();
        Set<Instant> savedEntries = new HashSet<>(); // because the histories would double on saving
        for (var _gameHistory : gameHistory.values()) {
            if (!_gameHistory.isDirty()) {
                continue;
            }
            List<GameHistory.Entry> addedEntries = _gameHistory.flush();
            for (var addedEntry : addedEntries) {
                if (savedEntries.contains(addedEntry.timestamp())) continue;
                savedEntries.add(addedEntry.timestamp());
                storage.createGameHistoryEntry(addedEntry);
            }
            affectedHistories++;
        }
        long deltaTime = System.currentTimeMillis() - startTime;
        if (affectedHistories > 0) {
            log.info("Updated {} histories and added {} entries in {}ms", affectedHistories, savedEntries.size(), deltaTime);
        }
    }


    public @NotNull GameHistory getGameHistory(@NotNull UUID uuid) {
        return gameHistory.computeIfAbsent(uuid, GameHistory::new);
    }

}
