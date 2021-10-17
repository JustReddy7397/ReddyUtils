package wiki.justreddy.ga.reddyutils.menu.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.InventoryHolder;
import wiki.justreddy.ga.reddyutils.menu.AnvilMenu;
import wiki.justreddy.ga.reddyutils.menu.ChestMenu;
import wiki.justreddy.ga.reddyutils.menu.HopperMenu;

public class MenuEvent implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof ChestMenu) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            ChestMenu chestMenu = (ChestMenu) holder;
            chestMenu.handleMenu(e);
        }else if(holder instanceof HopperMenu){
            e.setCancelled(true);
            if(e.getCurrentItem() == null){
                return;
            }
            HopperMenu hopperMenu = (HopperMenu) holder;
            hopperMenu.handleMenu(e);
        }else if(holder instanceof AnvilMenu){
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onAnvilRename(PrepareAnvilEvent e){
        InventoryHolder holder = e.getInventory().getHolder();

        if(holder instanceof AnvilMenu){
            AnvilMenu anvilMenu = (AnvilMenu) holder;
            anvilMenu.handleMenu(e);
        }

    }


}
