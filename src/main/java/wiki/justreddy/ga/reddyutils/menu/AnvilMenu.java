package wiki.justreddy.ga.reddyutils.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class AnvilMenu implements InventoryHolder {

    protected Inventory inventory;

    public abstract String getMenuName();

    public abstract void handleMenu(PrepareAnvilEvent e);

    public void open(Player p , ItemStack item){

        inventory = Bukkit.createInventory(this, InventoryType.ANVIL, getMenuName());

        inventory.setItem(0, item);

        p.openInventory(inventory);

    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
