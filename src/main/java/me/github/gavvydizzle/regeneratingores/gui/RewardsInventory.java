package me.github.gavvydizzle.regeneratingores.gui;

import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import me.github.gavvydizzle.regeneratingores.ore.BlockReward;
import me.github.gavvydizzle.regeneratingores.ore.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class RewardsInventory implements ClickableGUI {

    private final BlockReward blockReward;
    private final int oreNodeID;
    private Inventory inventory;

    public RewardsInventory(BlockReward blockReward, int oreNodeID) {
        this.blockReward = blockReward;
        this.oreNodeID = oreNodeID;
        generateInventory();
    }

    private void generateInventory() {
        inventory = Bukkit.createInventory(null, 54, "Rewards List");
        ArrayList<Reward> rewards = blockReward.getRewardArrayList();

        for (int i = 0; i < Math.min(45, rewards.size()); i++) {
            inventory.setItem(i, rewards.get(i).getItemStack());
        }
    }

    @Override
    public void openInventory(Player player) {
        player.openInventory(inventory);
        RegeneratingOres.getInstance().getInventoryManager().setClickableGUI(player, this);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {

    }

    public int getOreNodeID() {
        return oreNodeID;
    }
}