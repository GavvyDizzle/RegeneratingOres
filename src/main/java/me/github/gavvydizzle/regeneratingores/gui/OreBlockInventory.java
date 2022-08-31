package me.github.gavvydizzle.regeneratingores.gui;

import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import me.github.gavvydizzle.regeneratingores.ore.OreBlock;
import me.github.gavvydizzle.regeneratingores.ore.OreNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class OreBlockInventory implements ClickableGUI {

    private final OreNode oreNode;
    private Inventory inventory;
    private int numOreBlocks;

    public OreBlockInventory(OreNode oreNode) {
        this.oreNode = oreNode;
        generateInventory();
    }

    private void generateInventory() {
        inventory = Bukkit.createInventory(null, 54, "Block List");
        ArrayList<OreBlock> oreBlocks = oreNode.getOreBlockArrayList();
        numOreBlocks = oreBlocks.size();

        for (int i = 0; i < Math.min(54, numOreBlocks); i++) {
            inventory.setItem(i, oreBlocks.get(i).getItemStack());
        }
    }

    @Override
    public void openInventory(Player player) {
        player.openInventory(inventory);
        RegeneratingOres.getInstance().getInventoryManager().setClickableGUI(player, this);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        int maxClick = Math.min(54, numOreBlocks);
        if (e.getSlot() >= maxClick) return;

        oreNode.getOreBlockArrayList().get(e.getSlot()).getBlockReward().getRewardsInventory().openInventory((Player) e.getWhoClicked());
    }

    public OreNode getOreNode() {
        return oreNode;
    }
}
