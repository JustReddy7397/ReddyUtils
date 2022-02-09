package wiki.justreddy.ga.reddyutils.manager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SubCommand {

    private final String name;
    private final String description;
    private final String syntax;
    private final String permission;
    private final boolean playersOnly;
    private final List<String> aliases = new ArrayList<>();

    public SubCommand(String name, String description, String syntax, String permission, boolean playersOnly, String... aliases){
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.permission = permission;
        this.playersOnly = playersOnly;
        if(aliases != null){
            this.aliases.addAll(Arrays.asList(aliases));
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isPermissionEmpty(){
        return permission.isEmpty();
    }

    public boolean isPlayersOnly() {
        return playersOnly;
    }

    public List<String> getAliases() {
        return aliases;
    }


    /**
     * You use this when "playersOnly" is set to true
     */

    public abstract void onCommand(Player player, String[] args);

    /**
     * You use this when "playersOnly" is set to false
     */

    public abstract void onCommand(CommandSender sender, String[] args);

}
