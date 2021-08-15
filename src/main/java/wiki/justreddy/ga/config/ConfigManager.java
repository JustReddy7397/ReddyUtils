package wiki.justreddy.ga.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Map<String, ConfigHandler> configurations;

    public ConfigManager() {
        configurations = new HashMap<>();
    }

    public ConfigHandler getFile(String fileType) {
        return configurations.get(fileType);
    }

    public void reloadFiles() {
        configurations.values().forEach(ConfigHandler::reload);
    }

    public void registerFile(JavaPlugin plugin, String fileType, String fileName) {
        configurations.put(fileType, new ConfigHandler(plugin, fileName));
        configurations.values().forEach(ConfigHandler::saveDefaultConfig);
    }

     public void saveFile(String fileType){
         configurations.get(fileType).save();
     }

     public void saveFiles(){
        configurations.values().forEach(ConfigHandler::save);
     }

    public void createFolder(JavaPlugin plugin){
        File file = new File("plugins/" + plugin.getDescription().getName());
        if(!file.exists()){
            file.mkdir();
        }
    }






}
