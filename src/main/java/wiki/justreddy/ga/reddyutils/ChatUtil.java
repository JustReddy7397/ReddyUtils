package wiki.justreddy.ga.reddyutils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

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