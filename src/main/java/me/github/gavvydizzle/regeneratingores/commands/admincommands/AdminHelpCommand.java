package me.github.gavvydizzle.regeneratingores.commands.admincommands;

import com.github.mittenmc.serverutils.SubCommand;
import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminHelpCommand extends SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Opens this help menu";
    }

    @Override
    public String getSyntax() {
        return "/regenOreAdmin help";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;

        player.sendMessage("-----(RegeneratingOres Admin Commands)-----");
        ArrayList<SubCommand> subCommands = RegeneratingOres.getInstance().getAdminCommandManager().getSubcommands();
        for (SubCommand subCommand : subCommands) {
            player.sendMessage(ChatColor.GOLD + subCommand.getSyntax() + " - " + ChatColor.YELLOW + subCommand.getDescription());
        }
        player.sendMessage("-----(RegeneratingOres Admin Commands)-----");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }

}