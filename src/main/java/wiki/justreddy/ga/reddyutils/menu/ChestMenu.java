package wiki.justreddy.ga.reddyutils.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class ChestMenu implements InventoryHolder {

    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();

    public abstract String getMenuName();

    public abstract int getSlots();


    public abstract void handleMenu(InventoryClickEvent e);


    public abstract void setMenuItems(Player p);


    public void open(Player p) {

        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems(p);


        p.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setFillerGlass() {
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    @Deprecated
    public ItemStack makeItem(Material material, String displayName, String... lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);

        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }

}
