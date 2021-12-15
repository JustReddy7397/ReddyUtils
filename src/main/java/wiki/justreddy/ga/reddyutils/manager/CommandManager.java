package wiki.justreddy.ga.reddyutils.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JustReddy
 */

public class CommandManager implements CommandExecutor {

    private final List<SubCommand> subcommands;
    private final List<String> helpMessage;

    public CommandManager() {
        subcommands = new ArrayList<>();
        helpMessage = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                for (int i = 0; i < getSubcommands().size(); i++) {
                    if ((args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) || (getSubcommands().get(i).getAliases() != null && getSubcommands().get(i).getAliases().contains(args[0]))) {
                        getSubcommands().get(i).run(p, args);
                    }
                }
            } else if (args.length == 0) {
                if (helpMessage.isEmpty()) {
                    // Will Send Nothing
                } else {
                    for (int i = 0; i < getHelpMessage().size(); i++) {
                        for (int j = 0; j < getSubcommands().size(); j++) {
                            p.sendMessage(getHelpMessage().get(i).replace("%name%", getSubcommands().get(j).getName()).replace("%description%", getSubcommands().get(j).getDescription()).replace("%syntax%", getSubcommands().get(j).getSyntax()));
                        }
                    }
                }
            }
        }
        return true;
    }

    public List<SubCommand> getSubcommands() {
        return subcommands;
    }

    public void addSubCommand(SubCommand subCommand) {
        subcommands.add(subCommand);
    }

    public void addHelpMessage(String msg) {
        helpMessage.add(msg);
    }

    private List<String> getHelpMessage() {
        return helpMessage;
    }
}
