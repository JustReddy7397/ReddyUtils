package wiki.justreddy.ga.reddyutils.uitl;

import org.bukkit.Bukkit;

import java.util.UUID;

public interface PlayerUtil {

    default void getUUIDByName(String name){
        Bukkit.getOfflinePlayer(name);
    }

    default void getNameByUUID(UUID uuid){
        Bukkit.getOfflinePlayer(uuid).getName();
    }

}
