package wiki.justreddy.ga;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import wiki.justreddy.ga.config.ConfigManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public interface ChatUtil {

    default String c(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    default List<String> cList(List<String> input) {
        List<String> ret = new ArrayList<>();
        for (String line : input) ret.add(ChatColor.translateAlternateColorCodes('&', line));
        return ret;
    }

}