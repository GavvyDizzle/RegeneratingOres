package me.github.gavvydizzle.regeneratingores.ore;

import com.github.mittenmc.serverutils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single reward in a BlockReward
 */
public class Reward {

    private final double weight;
    private final String message;
    private final List<String> commands;
    private final boolean isActive;

    public Reward(FileConfiguration config, String path) {
        config.addDefault(path + ".isActive", true);
        config.addDefault(path + ".weight", 1.0);
        config.addDefault(path + ".message", "");
        config.addDefault(path + ".commands", new ArrayList<>());

        isActive = config.getBoolean(path + ".isActive");
        weight = config.getDouble(path + ".weight");
        message = Colors.conv(config.getString(path + ".message"));
        commands = config.getStringList(path + ".commands");
    }

    public void sendMessage(Player player) {
        if (!message.trim().isEmpty()) {
            player.sendMessage(message);
        }
    }

    public void runCommands(Player player) {
        for (String cmd : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player_name}", player.getName()));
        }
    }

    /**
     * Gets the item to display in the editor menu
     * @return The item
     */
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = itemStack.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Weight: " + weight);
        lore.add(ChatColor.GRAY + "Message: " + message);
        lore.add(ChatColor.GRAY + "Commands:");
        for (String str : commands) {
            lore.add(ChatColor.GRAY + " - /" + str);
        }
        lore.add("");

        assert meta != null;
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isActive() {
        return isActive;
    }
}
