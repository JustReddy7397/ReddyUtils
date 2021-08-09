package wiki.justreddy.ga;

import org.bukkit.entity.Player;
import wiki.justreddy.ga.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface CooldownManager extends ChatUtil {

    Map<UUID, Long> players_cooldown = new HashMap<>();

    default void addCooldown(Player p, long time){
        players_cooldown.put(p.getUniqueId(), time);
    }

    default void removeCooldown(Player p){
        players_cooldown.remove(p.getUniqueId());
    }

    default void checkCooldown(Player p, ConfigManager configManager , String type, String path){
        if (players_cooldown.containsKey(p.getUniqueId())) {
            if (players_cooldown.get(p.getUniqueId()) > System.currentTimeMillis()) {
                String prefix = configManager.getFile(type).getConfig().getString("prefix");
                long timeLeft = (players_cooldown.get(p.getUniqueId()) - System.currentTimeMillis()) / 1000;
                try{
                    p.sendMessage(c(configManager.getFile(type).getConfig().getString(path).replace("%prefix%", prefix != null && !prefix.isEmpty() ? prefix : "").replace("%time%", String.valueOf(timeLeft))));
                }catch (NullPointerException ex){
                    p.sendMessage(c("&cCan't find the cooldown message"));
                }
                return;
            }
        }
    }

}
