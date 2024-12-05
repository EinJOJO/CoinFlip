package de.einjojo.coinflip.economy;

import org.bukkit.OfflinePlayer;


public class DummyEconomy implements SimpleEconomy {
    @Override
    public double getBalance(OfflinePlayer player) {
        return 10000;
    }

    @Override
    public boolean hasBalance(OfflinePlayer player, double amount) {
        return true;
    }

    @Override
    public void withdraw(OfflinePlayer player, double amount) throws EconomyException {
        logger.warn("[DUMMY] withdrawn {} from {}", amount, player.getName());
    }

    @Override
    public void setBalance(OfflinePlayer player, double amount) {
        logger.warn("[DUMMY] set {}'s balance to {}", player.getName(), amount);
    }

    @Override
    public void deposit(OfflinePlayer player, double amount) throws EconomyException {
        logger.warn("[DUMMY] deposited {} to {}", amount, player.getName());
    }
}
