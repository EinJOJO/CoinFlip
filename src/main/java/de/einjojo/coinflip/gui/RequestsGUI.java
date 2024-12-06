package de.einjojo.coinflip.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.einjojo.coinflip.CoinFlipPlugin;
import de.einjojo.coinflip.messages.MessageKey;
import de.einjojo.coinflip.model.GameException;
import de.einjojo.coinflip.model.GameRequest;
import de.einjojo.coinflip.util.BetAmountValidator;
import de.einjojo.coinflip.util.ItemBuilder;
import de.einjojo.coinflip.util.PlayerChatInput;
import de.einjojo.coinflip.util.TagResolverHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Getter
public class RequestsGUI extends ChestGui {
    public static final List<RequestsGUI> ACTIVE_GUIS = new LinkedList<>();
    private final CoinFlipPlugin coinFlipPlugin;
    private final PaginatedPane requestsPane;
    private final Player player;


    public RequestsGUI(CoinFlipPlugin coinFlipPlugin, Player viewer) {
        super(6, ComponentHolder.of(MessageKey.REQUEST_GUI__NAME.getComponent()));
        this.player = viewer;
        this.coinFlipPlugin = coinFlipPlugin;
        setOnGlobalClick((e) -> e.setCancelled(true));
        requestsPane = new PaginatedPane(0, 0, 9, 5, Pane.Priority.HIGH);
        addPane(createNavigationBar(requestsPane));
        addPane(createBackground());
        addPane(requestsPane);
        update();
        setOnClose((e) -> {
            ACTIVE_GUIS.remove(this);
        });
        ACTIVE_GUIS.add(this);
        show(viewer);

    }


    @Override
    public void update() {
        requestsPane.clear();
        requestsPane.populateWithGuiItems(createRequestIcons());
        super.update();
    }

    public void updateWithoutRepopulate() {
        super.update();
    }

    private Pane createBackground() {
        OutlinePane background = new OutlinePane(0, 0, 9, 6, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(Component.text("")).build()));
        background.setRepeat(true);
        return background;
    }

    private GuiItem createBetIcon() {
        var item = new ItemBuilder(Material.PAPER)
                .setLore(MessageKey.REQUEST_GUI__BET__LORE)
                .setDisplayName(MessageKey.REQUEST_GUI__BET__NAME).build();
        return new GuiItem(item, (e) -> {
            player.closeInventory();
            new PlayerChatInput(CoinFlipPlugin.getInstance(), player, MessageKey.INPUT_TITLE_ENTER_BET.getComponent(), (input -> {
                Integer parsed = new BetAmountValidator().validateAndParseInput(input, player);
                if (parsed != null) {
                    new BetGui(getCoinFlipPlugin().getGameRequestManager(), parsed, player);
                }
            }));
        });
    }


    /**
     * Behavior and decoration of a request icon
     *
     * @return a list of request icons
     */
    public ArrayList<GuiItem> createRequestIcons() {
        var requestList = getCoinFlipPlugin().getGameRequestManager().getRequests();
        var itemList = new ArrayList<GuiItem>(requestList.size());
        for (GameRequest request : requestList) {
            if (!request.isRequesterOnline()) continue;
            var headItem = new ItemStack(Material.PLAYER_HEAD);
            headItem.editMeta(SkullMeta.class, (skullMeta -> {
                TagResolver[] tagResolvers = TagResolverHelper.createRequestResolver(request);
                final List<Component> compList;
                if (!player.getUniqueId().equals(request.getRequester())) {
                    compList = new LinkedList<>(MessageKey.REQUEST_GUI__REQUEST_ITEM__LORE.getList(tagResolvers)); // because an immutable list is returned
                    compList.add(MessageKey.REQUEST_GUI__REQUEST_ITEM__CLICK_SUGGESTION.getComponent(tagResolvers));
                } else {
                    compList = MessageKey.REQUEST_GUI__REQUEST_ITEM__LORE.getList(tagResolvers);
                    skullMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                    skullMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                skullMeta.setPlayerProfile(request.getRequesterPlayer().getPlayerProfile());
                skullMeta.setOwningPlayer(request.getRequesterPlayer());
                skullMeta.displayName(MessageKey.REQUEST_GUI__REQUEST_ITEM__NAME.getComponent(tagResolvers).decoration(TextDecoration.ITALIC, false));
                skullMeta.lore(compList.stream().map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
            }));

            GuiItem guiItem = new GuiItem(headItem, (event) -> {
                if (event.isLeftClick()) {
                    if (player.getUniqueId().equals(request.getRequester())) {
                        playDeclineSound();
                        return;
                    }

                    try {
                        var game = getCoinFlipPlugin().getActiveGameManager().startCoinFlip(request, player);
                        player.closeInventory();
                        game.playAnimation(getCoinFlipPlugin());
                    } catch (GameException e) {
                        player.sendMessage(e.getReason().name());
                        playDeclineSound();
                    }
                }

            });
            itemList.add(guiItem);
        }
        return itemList;
    }

    public void playDeclineSound() {
        player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1, 1.2f);

    }

    private GuiItem createBookIcon() {
        var item = new ItemBuilder(Material.BOOK).setDisplayName(Component.text("Info"))
                .setLore(MessageKey.REQUEST_GUI__INFO_LORE.getList(
                        TagResolverHelper.createHistoryResolver(
                                getCoinFlipPlugin().getGameHistoryManager().getGameHistory(player.getUniqueId()))))
                .build();
        return new GuiItem(item, (event) -> {
            event.getWhoClicked().closeInventory();
            Bukkit.dispatchCommand(event.getWhoClicked(), "coinflip help");
        });
    }

    private Pane createNavigationBar(PaginatedPane paginatedPane) {
        StaticPane navigation = new StaticPane(0, 5, 9, 1);
        navigation.addItem(new GuiItem(new ItemBuilder(Material.ARROW).setDisplayName(MessageKey.REQUEST_GUI__PAGING_ITEM__PREVIOUS_PAGE).build(), event -> {
            if (paginatedPane.getPage() > 0) {
                paginatedPane.setPage(paginatedPane.getPage() - 1);

                updateWithoutRepopulate();
            }
        }), 0, 0);

        navigation.addItem(createBookIcon(), 5, 0);
        navigation.addItem(createBetIcon(), 3, 0);

        navigation.addItem(new GuiItem(new ItemBuilder(Material.SPECTRAL_ARROW).setDisplayName(MessageKey.REQUEST_GUI__PAGING_ITEM__NEXT_PAGE).build(), event -> {
            if (paginatedPane.getPage() < paginatedPane.getPages() - 1) {
                paginatedPane.setPage(paginatedPane.getPage() + 1);
                updateWithoutRepopulate();
            }
        }), 8, 0);
        return navigation;
    }


    ;
}
