package de.einjojo.coinflip.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.manager.GameRequestManager;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.model.GameRequest;
import de.einjojo.coinflip.util.BetAmountValidator;
import de.einjojo.coinflip.util.ItemBuilder;
import de.einjojo.coinflip.util.PlayerChatInput;
import de.einjojo.coinflip.util.TagResolverHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class RequestsGUI extends ChestGui {
    private final GameRequestManager requestManager;
    private final PaginatedPane requestsPane;
    private final Player player;


    public RequestsGUI(GameRequestManager requestManager, Player viewer) {
        super(6, ComponentHolder.of(MessageKey.REQUEST_GUI__NAME.getComponent()));
        this.player = viewer;
        this.requestManager = requestManager;
        setOnGlobalClick((e) -> e.setCancelled(true));
        requestsPane = createRequestsPane();
        addPane(createNavigationBar(requestsPane));
        addPane(createBackground());
        update();
        show(viewer);

    }

    private PaginatedPane createRequestsPane() {
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5, Pane.Priority.HIGH);
        pane.populateWithGuiItems(createRequestIcons());
        return pane;
    }

    private Pane createBackground() {
        OutlinePane background = new OutlinePane(0, 0, 9, 6, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(Component.text(" ")).build()));
        background.setRepeat(true);
        return background;
    }

    private GuiItem createBetIcon() {
        var item = new ItemBuilder(Material.PAPER).setDisplayName(Component.text("Wetten")).build();
        return new GuiItem(item, (e) -> {
            player.closeInventory();
            new PlayerChatInput(CoinFlipPlugin.getInstance(), player, MessageKey.INPUT_ENTER_BET.getComponent(), (input -> {
                Integer parsed = new BetAmountValidator().validateAndParseInput(input, player);
                if (parsed != null) {
                    new BetGui(requestManager, parsed, player);
                }
            }));
        });
    }

    public ArrayList<GuiItem> createRequestIcons() {
        var requestList = requestManager.getRequests();
        var itemList = new ArrayList<GuiItem>(requestList.size());
        for (GameRequest request : requestList) {
            if (!request.isRequesterOnline()) continue;
            var headItem = new ItemStack(Material.PLAYER_HEAD);
            headItem.editMeta(SkullMeta.class, (skullMeta -> {
                skullMeta.setPlayerProfile(request.getRequesterPlayer().getPlayerProfile());
                skullMeta.setOwningPlayer(request.getRequesterPlayer());
                TagResolver[] tagResolvers = TagResolverHelper.createRequestResolver(request);
                var compList = MessageKey.REQUEST_GUI__REQUEST_ITEM__LORE.getList(tagResolvers);
                if (!player.getUniqueId().equals(request.getRequester())) {
                    compList.add(MessageKey.REQUEST_GUI__REQUEST_ITEM__CLICK_SUGGESTION.getComponent(tagResolvers));
                } else {
                    skullMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                    skullMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                skullMeta.lore(compList.stream().map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            }));

            GuiItem guiItem = new GuiItem(headItem, (event) -> {
                if (event.isLeftClick()) {
                    if (player.getUniqueId().equals(request.getRequester())) {
                        player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 1.2f);
                        return;
                    }

                }

            });
            itemList.add(guiItem);
        }
        return itemList;
    }


    private GuiItem createBookIcon() {
        var item = new ItemBuilder(Material.BOOK).setDisplayName(Component.text("Info")).build();
        return new GuiItem(item, (event) -> {
            event.getWhoClicked().closeInventory();
            Bukkit.dispatchCommand(event.getWhoClicked(), "coinflip help");
        });
    }

    private Pane createNavigationBar(PaginatedPane paginatedPane) {
        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.addItem(new GuiItem(new ItemStack(Material.ARROW), event -> {
            if (paginatedPane.getPage() > 0) {
                paginatedPane.setPage(paginatedPane.getPage() - 1);

                update();
            }
        }), 0, 0);

        navigation.addItem(createBookIcon(), 5, 0);
        navigation.addItem(createBetIcon(), 3, 0);

        navigation.addItem(new GuiItem(new ItemStack(Material.SPECTRAL_ARROW), event -> {
            if (paginatedPane.getPage() < paginatedPane.getPages() - 1) {
                paginatedPane.setPage(paginatedPane.getPage() + 1);
                update();
            }
        }), 8, 0);
        return navigation;
    }

    ;
}
