package wiki.justreddy.ga;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerUtil {

    default void getNameByUUID(UUID uuid){
        Bukkit.getPlayer(uuid).getName();
    }

    default Player getPlayerByUUID(UUID uuid){
        return Bukkit.getPlayer(uuid);
    }

}
