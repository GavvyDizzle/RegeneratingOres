package me.github.gavvydizzle.regeneratingores.ore;

import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class NodeManager implements Listener {

    private final Pattern pattern = Pattern.compile("[\\w]*");
    private final HashMap<Integer, OreNode> oreNodeIDHashMap;
    private final HashMap<String, OreNode> oreNodeNameHashMap;

    public NodeManager() {
        oreNodeIDHashMap = new HashMap<>();
        oreNodeNameHashMap = new HashMap<>();
    }

    public void reloadAllNodes() {
        File folder = new File(RegeneratingOres.getInstance().getDataFolder(), "nodes");
        folder.mkdir();

        for (OreNode oreNode : oreNodeIDHashMap.values()) {
            oreNode.cancelRegenerateTask();
        }

        oreNodeIDHashMap.clear();
        oreNodeNameHashMap.clear();
        parseFolderForNodes(folder);
    }

    private void parseFolderForNodes(final File folder) {
        int nodeId = 1;
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                parseFolderForNodes(fileEntry);
            }
            else {
                if (!fileEntry.getName().endsWith(".yml")) continue;

                try {
                    final FileConfiguration config = YamlConfiguration.loadConfiguration(fileEntry);
                    OreNode oreNode = new OreNode(fileEntry, config, nodeId);
                    oreNodeIDHashMap.put(nodeId, oreNode);
                    oreNodeNameHashMap.put(oreNode.getStrippedDisplayName(), oreNode);
                    nodeId++;

                } catch (Exception e) {
                    RegeneratingOres.getInstance().getLogger().severe("Failed to load node: " + fileEntry.getName() + "!");
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onNodeBlockBreak(BlockBreakEvent e) {
        OreNode oreNode = getOreNode(e.getBlock());
        if (oreNode == null) return;

        oreNode.onNodeBreak(e);
    }

    /**
     * Gets the OreNode belonging to this block
     * @param block The block
     * @return The OreNode, or null if this block is not an OreNode
     */
    @Nullable
    public OreNode getOreNode(Block block) {
        if (!block.hasMetadata("regenerating_ore")) return null;
        System.out.println(block.getMetadata("regenerating_ore").get(0).asInt()); //TODO - Remove
        return oreNodeIDHashMap.get(block.getMetadata("regenerating_ore").get(0).asInt());
    }

    @Nullable
    public OreNode getOreNode(String name) {
        return oreNodeNameHashMap.get(name);
    }

    @Nullable
    public OreNode getOreNode(int id) {
        return oreNodeIDHashMap.get(id);
    }

    public Set<String> getOreNodeNames() {
        return oreNodeNameHashMap.keySet();
    }

    public Pattern getPattern() {
        return pattern;
    }

}
