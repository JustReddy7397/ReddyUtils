package wiki.justreddy.ga.reddyutils.manager;

import org.bukkit.entity.Player;

import java.util.List;

public interface SubCommand {

    String getName();

    String getDescription();

    String getSyntax();

    List<String> getAliases();

    void run(Player p, String[] args);

}
