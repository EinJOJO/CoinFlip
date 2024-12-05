package de.einjojo.coinflip.util;

import de.einjojo.coinflip.messages.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public class ItemBuilder {

    private Material material;

    private Component displayName;
    private List<Component> lore;

    public ItemBuilder(Material material) {
        this.material = material;
    }

    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder setLore(MessageKey key) {
        return setLore(key.getList().stream().map((c) -> c.decoration(TextDecoration.ITALIC, false)).toList());
    }

    public ItemBuilder setDisplayName(MessageKey key) {
        return setDisplayName(key.getComponent().decoration(TextDecoration.ITALIC, false));
    }

    public ItemBuilder setLore(List<Component> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder setDisplayName(Component displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemStack build() {
        var itemStack = new ItemStack(material);
        itemStack.editMeta((meta) -> {
            if (displayName != null) {
                meta.displayName(displayName);
            }
            if (lore != null) {
                meta.lore(lore);
            }
        });
        return itemStack;
    }
}
