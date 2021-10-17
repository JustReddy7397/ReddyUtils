package wiki.justreddy.ga.reddyutils.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class HopperMenu implements InventoryHolder {

    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();

    public abstract String getMenuName();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems(Player p);

    public void open(Player p){

        inventory = Bukkit.createInventory(this, InventoryType.HOPPER, getMenuName());

        setMenuItems(p);

        p.openInventory(inventory);

    }


    public void setFillerGlass() {
        for (int i = 0; i < 5; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
