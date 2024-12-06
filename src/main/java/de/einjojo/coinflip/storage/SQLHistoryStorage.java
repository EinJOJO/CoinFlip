package de.einjojo.coinflip.storage;

import de.einjojo.coinflip.model.GameHistory;
import de.einjojo.coinflip.model.GameResult;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SQLHistoryStorage {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final ConnectionProvider provider;

    public SQLHistoryStorage(ConnectionProvider provider) {
        this.provider = provider;
    }

    /**
     * Ensures all tables exist.
     * Updates local id counter
     */
    public boolean init() {
        String table = """
                CREATE TABLE IF NOT EXISTS coinflip_history (
                    id INTEGER PRIMARY KEY,
                    winner VARCHAR(36),
                    loser VARCHAR(36),
                    amount INTEGER,
                    result TINYINT,
                    played_at TIMESTAMP
                );
                """;
        String determineAutoIncrementSQL = """
                    SELECT MAX(id) FROM coinflip_history;
                """;
        try (Connection connection = provider.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(table);
            try (ResultSet resultSet = statement.executeQuery(determineAutoIncrementSQL)) {
                if (resultSet.next()) {
                    ID_GENERATOR.set(resultSet.getInt(1) + 1);
                }
                log.info("Profile id generator set to {}", ID_GENERATOR.get());
            }
            return true;
        } catch (SQLException exception) {
            log.warn("Could not init", exception);
            return false;
        }
    }

    public boolean createGameHistoryEntry(GameHistory.Entry entry) {
        String insertSQL = """
                INSERT INTO coinflip_history (id, winner, loser, amount, result, played_at)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        try (Connection connection = provider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, ID_GENERATOR.getAndIncrement());
            preparedStatement.setString(2, entry.winner().toString());
            preparedStatement.setString(3, entry.loser().toString());
            preparedStatement.setInt(4, entry.amount());
            preparedStatement.setInt(5, entry.result().ordinal());
            preparedStatement.setTimestamp(6, Timestamp.from(entry.timestamp()));
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException exception) {
            log.warn("Could not create game history entry", exception);
            return false;
        }
    }

    public List<GameHistory.Entry> readAllGameHistoryEntries() {
        String selectSQL = "SELECT winner, loser, amount, result, played_at FROM coinflip_history;";
        List<GameHistory.Entry> entries = new ArrayList<>();
        try (Connection connection = provider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                entries.add(parseResultSet(resultSet));
            }
        } catch (SQLException exception) {
            log.warn("Could not read game history entries", exception);
        }
        return entries;
    }



    public GameHistory.Entry parseResultSet(ResultSet resultSet) throws SQLException {
        UUID winner = UUID.fromString(resultSet.getString("winner"));
        UUID loser = UUID.fromString(resultSet.getString("loser"));
        int amount = resultSet.getInt("amount");
        Timestamp timestamp = resultSet.getTimestamp("played_at");
        GameResult result = GameResult.values()[resultSet.getInt("result")];
        return new GameHistory.Entry(winner, loser, amount, result, timestamp.toInstant());
    }

    public boolean deleteGameHistoryEntry(int id) {
        String deleteSQL = "DELETE FROM coinflip_history WHERE id = ?;";
        try (Connection connection = provider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException exception) {
            log.warn("Could not delete game history entry with id {}", id, exception);
            return false;
        }
    }


}
