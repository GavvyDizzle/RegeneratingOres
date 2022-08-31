package me.github.gavvydizzle.regeneratingores.commands.admincommands;

import com.github.mittenmc.serverutils.SubCommand;
import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import me.github.gavvydizzle.regeneratingores.ore.OreNode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminSelectOreNode extends SubCommand {

    @Override
    public String getName() {
        return "selectNode";
    }

    @Override
    public String getDescription() {
        return "Select a node to edit";
    }

    @Override
    public String getSyntax() {
        return "/regenOreAdmin editNode <node>";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        if (!(sender instanceof Player)) return;

        OreNode oreNode = RegeneratingOres.getInstance().getNodeManager().getOreNode(args[1]);
        if (oreNode == null) {
            sender.sendMessage(ChatColor.RED + "Invalid OreNode: " + args[1]);
            return;
        }

        if (RegeneratingOres.getInstance().getEditManager().isInEditMode((Player) sender)) {
            RegeneratingOres.getInstance().getEditManager().setEditNode((Player) sender, oreNode);
            sender.sendMessage(ChatColor.GREEN + "Selected the node: " + oreNode.getDisplayName());
        }
        else {
            sender.sendMessage(ChatColor.RED + "You must be in edit mode to select a Node");
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], RegeneratingOres.getInstance().getNodeManager().getOreNodeNames(), list);
        }
        return list;
    }

}