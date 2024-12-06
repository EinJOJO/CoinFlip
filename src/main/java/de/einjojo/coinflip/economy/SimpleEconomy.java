package de.einjojo.coinflip.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SimpleEconomy {
    Logger logger = LoggerFactory.getLogger(SimpleEconomy.class);

    /**
     * Factory Method
     *
     * @return economy
     */
    static SimpleEconomy create() {
        try {
            var service = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (service != null) {
                var provider = service.getProvider();
                return new VaultEconomy(provider);
            } else {
                logger.warn("Vault is installed, but no Economy Provider has been found. You might need EssentialsX");
            }
        } catch (NoClassDefFoundError ex) {
            logger.warn("Vault is not installed!");
        }
        logger.warn("No Economy provider has been found.");
        logger.warn("Will be using Dummy-Economy Provider");
        return new DummyEconomy();
    }

    double getBalance(OfflinePlayer player);

    /**
     * Returns the balance of the player.
     *
     * @param player the player
     * @return the balance
     */
    boolean hasBalance(OfflinePlayer player, double amount);

    /**
     * Removes the specified amount from the player's balance.
     *
     * @param player the player
     * @param amount the amount to remove
     * @throws EconomyException if the money could not be withdrawn
     */
    void withdraw(OfflinePlayer player, double amount) throws EconomyException;

    void setBalance(OfflinePlayer player, double amount);

    /**
     * Adds the specified amount to the player's balance.
     *
     * @param player the player
     * @param amount the amount to add
     * @throws EconomyException if the money could not be deposited
     */
    void deposit(OfflinePlayer player, double amount) throws EconomyException;


}
