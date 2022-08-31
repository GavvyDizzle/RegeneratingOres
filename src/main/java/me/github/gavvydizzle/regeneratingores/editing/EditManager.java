package me.github.gavvydizzle.regeneratingores.editing;

import com.github.mittenmc.serverutils.Colors;
import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import me.github.gavvydizzle.regeneratingores.ore.OreNode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class EditManager implements Listener {

    private final HashMap<UUID, OreNode> editingAdmins;
    private final ItemStack editWand;

    public EditManager() {
        editingAdmins = new HashMap<>();

        editWand = new ItemStack(Material.STICK);
        ItemMeta meta = editWand.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv("&b>> &3&lRegenOre Wand &b<<"));
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Use this wand when in edit ore nodes",
                ChatColor.GRAY + "Use '/oreadmin edit' to toggle edit mode",
                "",
                ChatColor.YELLOW + "Left-Click: Remove Node location",
                ChatColor.GOLD + "Right-Click: Add Node location",
                ChatColor.YELLOW + "Shift + Left-Click: Open Node inventory",
                ChatColor.GOLD + "Shift + Right-Click: Select targeted Node"
        ));
        editWand.setItemMeta(meta);
    }

    @EventHandler
    private void onNodeInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) return;

        if (!isInEditMode(e.getPlayer())) return;

        if (!isHoldingEditWand(e.getPlayer())) return;

        e.setCancelled(true);

        if (e.getPlayer().isSneaking()) {
            OreNode oreNode = RegeneratingOres.getInstance().getNodeManager().getOreNode(clickedBlock);
            if (oreNode == null) return;

            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                oreNode.getOreBlockInventory().openInventory(e.getPlayer());
            }
            else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                setEditNode(e.getPlayer(), oreNode);
                e.getPlayer().sendMessage(ChatColor.YELLOW + "You are now editing the node " + oreNode.getDisplayName());
            }
        }
        else {
            OreNode oreNode = editingAdmins.get(e.getPlayer().getUniqueId());
            if (oreNode == null) return;

            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (oreNode.removeLocation(clickedBlock.getLocation())) {
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "Removed this location for " + oreNode.getDisplayName());
                } else {
                    e.getPlayer().sendMessage(ChatColor.RED + "This is not an active location for " + oreNode.getDisplayName());
                }
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (oreNode.addLocation(clickedBlock.getLocation(), true)) {
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "Added this location for " + oreNode.getDisplayName());
                } else {
                    e.getPlayer().sendMessage(ChatColor.RED + "This location is already in use for " + oreNode.getDisplayName());
                }
            }
        }
    }

    @EventHandler
    private void onAdminQuit(PlayerQuitEvent e) {
        editingAdmins.remove(e.getPlayer().getUniqueId());
        removeEditWand(e.getPlayer());
    }

    public boolean isInEditMode(Player player) {
        return editingAdmins.containsKey(player.getUniqueId());
    }

    private boolean isHoldingEditWand(Player player) {
        return player.getInventory().getItemInMainHand().isSimilar(editWand);
    }

    public void toggleEditMode(Player player) {
        if (isInEditMode(player)) {
            editingAdmins.remove(player.getUniqueId());
            removeEditWand(player);
        }
        else {
            editingAdmins.put(player.getUniqueId(), null);
            giveEditWand(player);
        }
    }

    public void setEditNode(Player player, @Nullable OreNode oreNode) {
        editingAdmins.put(player.getUniqueId(), oreNode);
    }

    private void giveEditWand(Player player) {
        if (!player.getInventory().contains(editWand)) {
            player.getInventory().addItem(editWand);
        }
    }

    private void removeEditWand(Player player) {
        player.getInventory().remove(editWand);
    }

}
