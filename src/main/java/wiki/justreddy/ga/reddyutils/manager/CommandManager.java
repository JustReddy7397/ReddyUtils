package wiki.justreddy.ga.reddyutils.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wiki.justreddy.ga.reddyutils.uitl.ChatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JustReddy
 */

public class CommandManager implements CommandExecutor, ChatUtil {

    private final List<SubCommand> subcommands;
    private final List<String> helpMessage;
    private String noPermissionMessage;

    public CommandManager() {
        subcommands = new ArrayList<>();
        helpMessage = new ArrayList<>();
        noPermissionMessage = c("&cYou need the %permission% permission to run this command..");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        for (int i = 0; i < getSubcommands().size(); i++) {
            if (getSubcommands().get(i).isPlayersOnly()) {
                if (sender instanceof Player) {
                    final Player player = (Player) sender;
                    if ((args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) || (getSubcommands().get(i).getAliases() != null && getSubcommands().get(i).getAliases().contains(args[0]))) {
                        if (!getSubcommands().get(i).isPermissionEmpty()) {
                            player.sendMessage(c(noPermissionMessage.replaceAll("%permission%", getSubcommands().get(i).getPermission())));
                            return true;
                        } else {
                            getSubcommands().get(i).onCommand(player, args);
                        }
                    }
                }
            } else {
                if ((args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) || (getSubcommands().get(i).getAliases() != null && getSubcommands().get(i).getAliases().contains(args[0]))) {
                    getSubcommands().get(i).onCommand(sender, args);
                }
            }
        }

        return true;
    }

    public String setNoPermissionMessage(String message) {
        return noPermissionMessage = message;
    }

/*    @Override
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
    }*/

    public List<SubCommand> getSubcommands() {
        return subcommands;
    }

    public void addSubCommand(SubCommand subCommand) {
        subcommands.add(subCommand);
    }

}
