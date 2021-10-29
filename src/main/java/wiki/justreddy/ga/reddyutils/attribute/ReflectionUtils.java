package wiki.justreddy.ga.reddyutils.attribute;

import org.bukkit.Bukkit;

public class ReflectionUtils {

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().info("[Reflection] Can't find NMS Class! (net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name + ")");
            return null;
        }
    }

    public static Class<?> getCBClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().info("[Reflection] Can't find CB Class! (org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name + ")");
            return null;
        }
    }

}
