package wiki.justreddy.ga;

import org.bukkit.Bukkit;

public interface VersionManager {

    boolean mc_18 = Bukkit.getServer().getVersion().contains("1.8.8");
    @Deprecated
    boolean mc_19 = Bukkit.getServer().getVersion().contains("1.9.4");
    @Deprecated
    boolean mc_10 = Bukkit.getServer().getVersion().contains("1.10.2");
    @Deprecated
    boolean mc_11 = Bukkit.getServer().getVersion().contains("1.11.2");
    boolean mc_12 = Bukkit.getServer().getVersion().contains("1.12.2");
    boolean mc_13 = Bukkit.getServer().getVersion().contains("1.13.2");
    @Deprecated
    boolean mc_14 = Bukkit.getServer().getVersion().contains("1.14.4");
    @Deprecated
    boolean mc_15 = Bukkit.getServer().getVersion().contains("1.15.2");
    boolean mc_16 = Bukkit.getServer().getVersion().contains("1.16.5");
    @Deprecated
    boolean mc_17 = Bukkit.getServer().getVersion().contains("1.17.1");

}
