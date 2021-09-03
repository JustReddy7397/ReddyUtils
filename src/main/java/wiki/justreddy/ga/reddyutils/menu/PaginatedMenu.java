package wiki.justreddy.ga.reddyutils.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;

    protected int maxItemsPerPage = 28;

    protected int index = 0;


    //Set the border and menu buttons for the menu
    public void addMenuBorder() {
        ItemStack left = XMaterial.LIME_DYE.parseItem();
        ItemMeta meta = left.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Left");
        left.setItemMeta(meta);
        ItemStack right = XMaterial.LIME_DYE.parseItem();
        ItemMeta metaa = right.getItemMeta();
        metaa.setDisplayName(ChatColor.GREEN + "Right");
        right.setItemMeta(metaa);
        inventory.setItem(48, left);

        inventory.setItem(49, makeItem(Material.BARRIER, ChatColor.DARK_RED + "Close"));

        inventory.setItem(50, right);

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
        }

        inventory.setItem(17, super.FILLER_GLASS);
        inventory.setItem(18, super.FILLER_GLASS);
        inventory.setItem(26, super.FILLER_GLASS);
        inventory.setItem(27, super.FILLER_GLASS);
        inventory.setItem(35, super.FILLER_GLASS);
        inventory.setItem(36, super.FILLER_GLASS);

        for (int i = 44; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
        }
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}