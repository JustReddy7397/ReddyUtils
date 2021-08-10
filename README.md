# ReddyUtils [![](https://jitpack.io/v/JustReddy7397/ReddyUtils.svg)](https://jitpack.io/#JustReddy7397/ReddyUtils)
Hey there! With this Utils API you can easily create yml files, commands with arguments ( /test hello for example ), add colors to your messages / lists, create cooldowns and get a players name by UUID or get a player by UUID.

**NOTE:** You are not allowed to fork this!

To add it to your project you can do the following:

Maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>com.github.JustReddy7397</groupId>
        <artifactId>ReddyUtils</artifactId>
        <version>1.2</version>
    </dependency>
</dependencies>
```
Gradle:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation "com.github.JustReddy7397:ReddyUtils:1.2"
}
```

Alright, now that you have implemented this into your project, i'll show you some examples!

##Creating YML Files:
````java
package wiki.justreddy.ga;

import org.bukkit.plugin.java.JavaPlugin;
import wiki.justreddy.ga.config.ConfigManager;

public class TestPlugin extends JavaPlugin {
    
    private static TestPlugin plugin;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        plugin = this;

        configManager = new ConfigManager();
        
        configManager.createFolder(this); // Optional, but its recommend.
        // If you don't use the default config.yml from spigots api , it won't load the plugins folder
        // That is why this method exists
        
        configManager.registerFile(this, "config", "config");
        // FileType: Name that will be used to get the file, filename: the name of the file whiteout the .yml
        // When you did this, make sure you created the .yml file in the resources folder!
        configManager.registerFile(this, "messages", "messages");
        // You can make as many files are u like
        
        
    }
    
    // These methods are gonna get used by the next example
    
    public static TestPlugin getPlugin(){
        return plugin;
    }
    
    public ConfigManager getConfigManager(){
        return configManager;
    }
    
}
````

## Getting a path from a yml file:
````java
package wiki.justreddy.ga;

import org.bukkit.entity.Player;
import wiki.justreddy.ga.manager.SubCommand;

public class TestCommand implements SubCommand {
    @Override
    public String getName() {
        return "1";
    }

    @Override
    public String getDescription() {
        return "test 1";
    }

    @Override
    public String getSyntax() {
        return "/test 1";
    }

    @Override
    public void run(Player p, String[] args) {
        
        // Will throw an error if it cant find the file type or the path
        p.sendMessage(TestPlugin.getPlugin().getConfigManager().getFile("messages").getConfig().getString("hello-world"));

    }
}
````

## Registering the CommandManager and adding a subcommand
`````java
package wiki.justreddy.ga;

import org.bukkit.plugin.java.JavaPlugin;
import wiki.justreddy.ga.manager.CommandManager;

public class TestPlugin extends JavaPlugin {
    
    private CommandManager commandManager;
    
    @Override
    public void onEnable() {
        
        commandManager = new CommandManager();
        
        
        // This part is optional, you can create your own help message
        commandManager.addHelpMessage("--------------------");
        // Placeholders:
        // %name% - Returns the actual command name. For example: help
        // %description% - Returns the commands description. For example: Shows this message
        // %syntax% - Retuns the commands syntax. For example: /test help
        commandManager.addHelpMessage("%syntax% - %description%");
        commandManager.addHelpMessage("--------------------");
        
        // How to register the actual command:
        // Ofcourse you have to put this command name in the plugin.yml
        // subcommands don't need to be registered in the plugin.yml
        getCommand("test").setExecutor(new CommandManager());
        // Register subcommands
        commandManager.addSubCommand(new HelpCommand(commandManager));
        
    }
}
`````
## Creating the subcommand
````java
package wiki.justreddy.ga;

import org.bukkit.entity.Player;
import wiki.justreddy.ga.manager.CommandManager;
import wiki.justreddy.ga.manager.SubCommand;

public class HelpCommand implements SubCommand {

    // This command goes over how to make your custom help command

    private CommandManager commandManager;

    public HelpCommand(CommandManager commandManager){
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows this message";
    }

    @Override
    public String getSyntax() {
        return "/test help";
    }

    @Override
    public void run(Player p, String[] args) {

        p.sendMessage("--------------------");
        for(int i = 0; i < commandManager.getSubcommands().size(); i++){
            p.sendMessage(commandManager.getSubcommands().get(i).getSyntax() + " - " + commandManager.getSubcommands().get(i).getDescription());
        }
        p.sendMessage("--------------------");

    }
}

````
