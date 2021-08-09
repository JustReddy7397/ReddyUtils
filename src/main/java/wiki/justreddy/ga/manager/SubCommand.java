package wiki.justreddy.ga.manager;

import org.bukkit.entity.Player;

public interface SubCommand {

    String getName();

    String getDescription();

    String getSyntax();

    void run(Player p, String[] args);

}
