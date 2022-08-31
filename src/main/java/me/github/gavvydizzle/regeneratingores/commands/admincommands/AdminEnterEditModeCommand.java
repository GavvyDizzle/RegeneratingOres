package me.github.gavvydizzle.regeneratingores.commands.admincommands;

import com.github.mittenmc.serverutils.SubCommand;
import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminEnterEditModeCommand extends SubCommand {

    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public String getDescription() {
        return "Toggles edit mode";
    }

    @Override
    public String getSyntax() {
        return "/regenOreAdmin edit";
    }

    @Override
    public String getColoredSyntax() {
        return ChatColor.YELLOW + "Usage: " + getSyntax();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            RegeneratingOres.getInstance().getEditManager().toggleEditMode(player);

            if (RegeneratingOres.getInstance().getEditManager().isInEditMode(player)) {
                sender.sendMessage(ChatColor.GREEN + "[RegeneratingOres] Turned on edit mode");
            }
            else {
                sender.sendMessage(ChatColor.RED + "[RegeneratingOres] Turned off edit mode");
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return new ArrayList<>();
    }

}