package me.github.gavvydizzle.regeneratingores.gui;

import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class InventoryManager implements Listener {

    private final HashMap<UUID, ClickableGUI> playersInInventory;

    public InventoryManager() {
        playersInInventory = new HashMap<>();
        reload();
    }

    public void reload() {

    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e) {
        if (playersInInventory.containsKey(e.getPlayer().getUniqueId())) {
            ClickableGUI clickableGUI = playersInInventory.get(e.getPlayer().getUniqueId());
            if (clickableGUI instanceof RewardsInventory) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(RegeneratingOres.getInstance(), () -> {
                    try {
                        Objects.requireNonNull(RegeneratingOres.getInstance().getNodeManager().getOreNode(((RewardsInventory) clickableGUI).getOreNodeID())).getOreBlockInventory().openInventory((Player) e.getPlayer());
                    } catch (Exception ex) {
                        removePlayerFromGUI((Player) e.getPlayer());
                    }
                });
            }
            else {
                removePlayerFromGUI((Player) e.getPlayer());
            }
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (playersInInventory.containsKey(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == e.getView().getTopInventory()) {
                playersInInventory.get(e.getWhoClicked().getUniqueId()).handleClick(e);
            }
        }
    }

    public void setClickableGUI(Player player, ClickableGUI clickableGUI) {
        playersInInventory.put(player.getUniqueId(), clickableGUI);
    }

    public void removePlayerFromGUI(Player player) {
        playersInInventory.remove(player.getUniqueId());
    }

}
