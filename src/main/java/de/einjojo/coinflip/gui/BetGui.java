package de.einjojo.coinflip.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import de.einjojo.coinflip.manager.GameRequestManager;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.model.GameRequest;
import de.einjojo.coinflip.model.GameResult;
import de.einjojo.coinflip.util.ItemBuilder;
import de.einjojo.coinflip.util.TagResolverHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BetGui extends ChestGui {
    private final int betAmount;
    private final Player player;
    private final GameRequestManager gameRequestManager;

    public BetGui(GameRequestManager gameRequestManager, int betAmount, Player player) {
        super(3, "Wetten: " + betAmount);
        this.gameRequestManager = gameRequestManager;
        this.betAmount = betAmount;
        this.player = player;
        setOnGlobalClick((e) -> e.setCancelled(true));
        OutlinePane head = new OutlinePane(0, 0, 4, 3);
        head.addItem(new GuiItem(new ItemBuilder(Material.GOLD_INGOT).setDisplayName(MessageKey.HEAD).build()));
        head.setRepeat(true);
        head.setOnClick((e) -> {
            createBet(GameResult.HEAD);
        });
        OutlinePane tail = new OutlinePane(5, 0, 4, 3);
        tail.addItem(new GuiItem(new ItemBuilder(Material.PAPER).setDisplayName(MessageKey.TAIL).build()));
        tail.setRepeat(true);
        tail.setOnClick((e) -> {
            createBet(GameResult.TAIL);
        });
        addPane(tail);
        addPane(head);
        show(player);

    }

    public void createBet(GameResult result) {
        player.closeInventory();
        var request = new GameRequest(
                player.getUniqueId(),
                result,
                betAmount
        );
        try {
            gameRequestManager.registerRequest(request);
            player.sendMessage(MessageKey.REQUEST__CREATED.getComponent(
                    TagResolverHelper.createRequestResolver(request)
            ));
        } catch (IllegalStateException ex) {
            player.sendMessage(MessageKey.REQUEST__NOT_CREATED.getComponent());
        }


    }


}
