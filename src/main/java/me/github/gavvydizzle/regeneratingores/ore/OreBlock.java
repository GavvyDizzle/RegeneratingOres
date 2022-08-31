package me.github.gavvydizzle.regeneratingores.ore;
import com.github.mittenmc.serverutils.ConfigUtils;
import me.github.gavvydizzle.regeneratingores.gui.OreBlockInventory;
import me.github.gavvydizzle.regeneratingores.gui.RewardsInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Represents a single ore block in an ore node
 */
public class OreBlock {

    private final double weight;
    private final Material material;
    private final boolean isActive, dropMinedBlock;
    private final BlockReward blockReward;

    /**
     * Generates an OreBlock from the config given the path
     * @param config The config file
     * @param path The path in the form type.x
     * @param fileName The name of thew file for debugging
     */
    public OreBlock(FileConfiguration config, String path, String fileName, int oreNodeID) {
        config.addDefault(path + ".isActive", true);
        config.addDefault(path + ".weight", 1.0);
        config.addDefault(path + ".material", "COAL_ORE");
        config.addDefault(path + ".dropMinedBlock", false);

        isActive = config.getBoolean(path + ".isActive");
        weight = config.getDouble(path + ".weight");
        material = ConfigUtils.getMaterial(config.getString(path + ".material"), Material.BEDROCK);
        dropMinedBlock = config.getBoolean(path + ".dropMinedBlock");

        blockReward = new BlockReward(config, path, fileName, oreNodeID);
    }

    /**
     * Handles when this block is mined by a player
     * @param e The block break event
     */
    public void onBlockMined(BlockBreakEvent e) {
        blockReward.rewardPlayer(e.getPlayer());

        // Don't drop the block in the case that the block got replaced with different block
        boolean shouldDropBlock = dropMinedBlock && (e.getBlock().getType() == material);
        e.setDropItems(shouldDropBlock);
    }

    /**
     * Gets the item to display in the editor menu
     * @return The item
     */
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Weight: " + weight);
        lore.add(ChatColor.GRAY + "Dropping Self? " + dropMinedBlock);
        lore.add("");
        lore.add(ChatColor.GOLD + "Click to open the rewards for this OreBlock");

        assert meta != null;
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public double getWeight() {
        return weight;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isActive() {
        return isActive;
    }

    public BlockReward getBlockReward() {
        return blockReward;
    }
}
