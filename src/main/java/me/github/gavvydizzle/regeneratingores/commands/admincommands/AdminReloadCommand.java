package me.github.gavvydizzle.regeneratingores.commands.admincommands;

import com.github.mittenmc.serverutils.SubCommand;
import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminReloadCommand extends SubCommand {

    private final List<String> subReloadList = Collections.singletonList("nodes");

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads this plugin";
    }

    @Override
    public String getSyntax() {
        return "/regenOreAdmin reload";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("nodes")) {
                try {
                    reloadNodes();
                    sender.sendMessage(ChatColor.GREEN + "[RegeneratingOres] Reloaded nodes");
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "An error occurred when reloading nodes. Please check the console");
                    e.printStackTrace();
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "[RegeneratingOres] Invalid sub-argument. Nothing was reloaded");
            }
        }
        try {
            RegeneratingOres.getInstance().reloadConfig();
            reloadNodes();
            sender.sendMessage(ChatColor.GREEN + "[RegeneratingOres] Reloaded");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "[RegeneratingOres] An error occurred when reloading! Please check the console");
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], subReloadList, list);
        }
        return list;
    }

    private void reloadNodes() {
        RegeneratingOres.getInstance().getNodeManager().reloadAllNodes();
    }

}
