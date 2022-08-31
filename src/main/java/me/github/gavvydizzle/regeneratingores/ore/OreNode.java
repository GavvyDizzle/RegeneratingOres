package me.github.gavvydizzle.regeneratingores.ore;

import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.ConfigUtils;
import com.github.mittenmc.serverutils.RandomValuePair;
import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import me.github.gavvydizzle.regeneratingores.gui.OreBlockInventory;
import me.github.gavvydizzle.regeneratingores.sorters.OreBlockWeightSorter;
import me.github.gavvydizzle.regeneratingores.utils.RandomSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a block in the world that can generate as multiple types
 */
public class OreNode {

    private final int id;
    private final String displayName, strippedDisplayName;
    private final RandomValuePair respawnTicks;
    private final Material regeneratingMaterial;
    private int taskID;
    private final Random random;
    private final ArrayList<Location> locations;
    private final ArrayList<OreBlock> oreBlockArrayList;
    private final HashMap<Material, OreBlock> oreBlockHashMap;
    private RandomSelector<OreBlock> oreBlockRandomSelector;

    private final OreBlockInventory oreBlockInventory;

    private final File file;
    private final FileConfiguration config;

    /**
     * Generates a new OreNode
     * @param file The file to save to
     * @param config The file to load this node from
     * @param id The id of this note (must be unique) (used in metadata to determine the node broken)
     */
    public OreNode(File file, FileConfiguration config, int id) {
        String strippedDisplayName1;
        this.id = id;
        this.taskID = -1;
        this.random = new Random();
        this.file = file;
        this.config = config;
        oreBlockRandomSelector = null;

        config.options().copyDefaults(true);
        config.addDefault("displayName", "&7" + file.getName().replace(".yml", ""));
        config.addDefault("respawnTicks", "100 200");
        config.addDefault("regeneratingMaterial", "AIR");
        config.addDefault("locations", new ArrayList<>());
        config.addDefault("types", new HashMap<>());

        displayName = Colors.conv(config.getString("displayName"));
        strippedDisplayName1 = Colors.strip(displayName);

        if (!RegeneratingOres.getInstance().getNodeManager().getPattern().matcher(strippedDisplayName1).matches()) {
            strippedDisplayName1 = "" + id;
        }
        strippedDisplayName = strippedDisplayName1;

        respawnTicks = RandomValuePair.getValuePair("respawnTicks", config.getString("respawnTicks"));
        regeneratingMaterial = ConfigUtils.getMaterial(config.getString("regeneratingMaterial"), Material.AIR);

        locations = new ArrayList<>();
        for (Object location : Objects.requireNonNull(config.getList("locations"))) {
            locations.add((Location) location);
        }

        oreBlockArrayList = new ArrayList<>();
        oreBlockHashMap = new HashMap<>();

        if (config.getConfigurationSection("types") != null) {

            for (String str : Objects.requireNonNull(config.getConfigurationSection("types")).getKeys(false)) {
                String path = "types." + str;
                OreBlock oreBlock = new OreBlock(config, path, file.getName(), id);

                if (oreBlock.isActive()) {
                    if (oreBlockHashMap.containsKey(oreBlock.getMaterial())) {
                        RegeneratingOres.getInstance().getLogger().warning("You have defined the material " + oreBlock.getMaterial() + " multiple times in " + file.getName() + ". " +
                                "The repeat occurrence at " + path + " is being ignored!");
                        continue;
                    }
                    oreBlockArrayList.add(oreBlock);
                    oreBlockHashMap.put(oreBlock.getMaterial(), oreBlock);
                }
            }
        }
        else {
            RegeneratingOres.getInstance().getLogger().warning("You have no types defined in " + file.getName());
        }

        try {
            config.save(file);
        } catch (Exception e) {
            RegeneratingOres.getInstance().getLogger().severe("Failed to save default config for " + file.getName());
        }

        if (!oreBlockArrayList.isEmpty()) {
            oreBlockRandomSelector = RandomSelector.weighted(oreBlockArrayList, OreBlock::getWeight);
        }

        placeAllOreBlocks();

        oreBlockArrayList.sort(new OreBlockWeightSorter());
        oreBlockInventory = new OreBlockInventory(this);
    }

    /**
     * Handles when a node is broken by a player.
     * This should be called with high priority because it un-cancels the event
     * @param e The original block break event
     */
    public void onNodeBreak(BlockBreakEvent e) {
        Location location = e.getBlock().getLocation();

        e.setCancelled(false);

        OreBlock oreBlock = getOreBlockByType(e.getBlock().getType());
        if (oreBlock == null) return;

        oreBlock.onBlockMined(e);
        removeBlockData(location);

        setBlockToFiller(location);

        scheduleRegenerateTask(location);
    }

    @Nullable
    private OreBlock getOreBlockByType(Material material) {
        return oreBlockHashMap.get(material);
    }

    /**
     * Sets the mined block to the material when this node is regenerating.
     * If the material provided is air, the block will stay empty while it is regenerating
     * @param location The location
     */
    private void setBlockToFiller(Location location) {
        if (regeneratingMaterial != Material.AIR) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(RegeneratingOres.getInstance(), () -> location.getBlock().setType(regeneratingMaterial));
        }
    }

    /**
     * Schedules this node to place a new OreBlock in the future.
     * This selects a random OreBlock, places it, and allows it to be mined
     */
    private void scheduleRegenerateTask(Location location) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RegeneratingOres.getInstance(), () -> {
            placeRandomOreBlock(location);
        }, respawnTicks.getRandomInt());
    }

    /**
     * Cancels the task (if active) that was going to regenerate this node
     */
    public void cancelRegenerateTask() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = -1;
        }
    }

    /**
     * Places a new ore block down at all locations for this node
     */
    public void placeAllOreBlocks() {
        for (Location location : locations) {
            placeRandomOreBlock(location);
        }
    }

    private void placeRandomOreBlock(Location location) {
        if (oreBlockRandomSelector == null) return;

        OreBlock oreBlock = oreBlockRandomSelector.next(random);
        location.getBlock().setType(oreBlock.getMaterial());
        addBlockData(location);
    }

    private void addBlockData(Location location) {
        location.getBlock().setMetadata("regenerating_ore", new FixedMetadataValue(RegeneratingOres.getInstance(), id));
    }

    private void removeBlockData(Location location) {
        location.getBlock().removeMetadata("regenerating_ore", RegeneratingOres.getInstance());
    }

    public void delete() {

    }

    /**
     * Handles when a location is added to this node in-game.
     * @param location The location to add
     * @param regenerate If the node should immediately regenerate
     */
    public boolean addLocation(Location location, boolean regenerate) {
        if (locations.contains(location)) return false;

        locations.add(location);
        if (regenerate) {
            placeRandomOreBlock(location);
        }
        else {
            scheduleRegenerateTask(location);
        }
        updateConfigLocations();

        return true;
    }

    public boolean removeLocation(Location location) {
        if (locations.remove(location)) {
            removeBlockData(location);
            location.getBlock().setType(Material.AIR);
            updateConfigLocations();
            return true;
        }
        return false;
    }

    private void updateConfigLocations() {
        config.set("locations", locations);
        try {
            config.save(file);
        } catch (Exception e) {
            RegeneratingOres.getInstance().getLogger().severe("Failed to update node locations to " + file.getName());
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStrippedDisplayName() {
        return strippedDisplayName;
    }

    public ArrayList<OreBlock> getOreBlockArrayList() {
        return oreBlockArrayList;
    }

    public OreBlockInventory getOreBlockInventory() {
        return oreBlockInventory;
    }

}
