package de.einjojo.coinflip.economy;

import lombok.NonNull;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

/**
 * Wrapper class for vault economy
 */
public class VaultEconomy implements SimpleEconomy {

    private final Economy vaultEconomy;

    public VaultEconomy(@NonNull Economy economy) {
        this.vaultEconomy = economy;
    }

    @Override
    public boolean hasBalance(OfflinePlayer player, double amount) {
        return vaultEconomy.has(player, amount);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return vaultEconomy.getBalance(player);
    }

    @Override
    public void withdraw(OfflinePlayer player, double amount) throws EconomyException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!vaultEconomy.withdrawPlayer(player, amount).transactionSuccess()) {
            throw new EconomyException("Failed to withdraw " + amount + " from " + player.getName());
        }
    }

    @Override
    public void deposit(OfflinePlayer player, double amount) throws EconomyException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!vaultEconomy.depositPlayer(player, amount).transactionSuccess()) {
            throw new EconomyException("Failed to deposit " + amount + " to " + player.getName());
        }
    }

    @Override
    public void setBalance(OfflinePlayer player, double amount) throws EconomyException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!vaultEconomy.withdrawPlayer(player, vaultEconomy.getBalance(player)).transactionSuccess()) {
            throw new EconomyException("Failed to set balance of " + player.getName() + " to " + amount);
        }
        if (amount != 0) {
            if (!vaultEconomy.depositPlayer(player, amount).transactionSuccess()) {
                throw new EconomyException("Failed to set balance of " + player.getName() + " to " + amount);
            }
        }

    }
}
