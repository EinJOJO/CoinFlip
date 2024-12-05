/*
 * Copyright (c) 2018-2024, Thomas Meaney
 * Copyright (c) contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.einjojo.coinflip.util;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.messages.MessageKey;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerChatInput implements Listener {

    private final CoinFlipPlugin plugin;

    private final MyScheduledTask taskId;
    private final InputRunnable runWhenComplete;
    private final UUID playerUuid;
    private final boolean inputMode;

    private final Map<UUID, PlayerChatInput> inputs = new HashMap<>();

    public PlayerChatInput(CoinFlipPlugin plugin, Player player, Component titleComponent, InputRunnable runWhenComplete) {
        this.plugin = plugin;
        Component subtitle = MessageKey.INPUT_CANCEL_SUBTITLE.getComponent();
        Title title = Title.title(titleComponent, subtitle, Title.Times.times(Duration.ZERO, Duration.ofMillis(1500), Duration.ZERO));
        this.taskId = plugin.getScheduler().runTaskTimer(() -> {
            player.showTitle(title);
        }, 0, 20);

        this.playerUuid = player.getUniqueId();
        this.runWhenComplete = runWhenComplete;
        this.inputMode = true;

        player.closeInventory();
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1.2f);

        this.register();
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        String input = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();

        if (!inputs.containsKey(playerUuid)) {
            return;
        }

        PlayerChatInput current = inputs.get(playerUuid);
        if (!current.inputMode) {
            return;
        }

        event.setCancelled(true);

        if (input.equalsIgnoreCase("cancel")) {
            current.taskId.cancel();
            current.unregister();
            player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 1);
            player.clearTitle();
            player.sendMessage(MessageKey.INPUT_CANCELLED.getComponent());
            return;
        }

        current.taskId.cancel();
        current.plugin.getScheduler().runTask(() -> current.runWhenComplete.run(input));
        player.clearTitle();
        current.unregister();
    }

    private void register() {
        inputs.put(this.playerUuid, this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void unregister() {
        inputs.remove(this.playerUuid);
        HandlerList.unregisterAll(this);
    }

    /**
     * Callback executed on main-thread
     */
    @FunctionalInterface
    public interface InputRunnable {

        void run(String input);
    }
}