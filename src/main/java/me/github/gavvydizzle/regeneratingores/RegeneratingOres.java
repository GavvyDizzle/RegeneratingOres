package me.github.gavvydizzle.regeneratingores;

import me.github.gavvydizzle.regeneratingores.commands.AdminCommandManager;
import me.github.gavvydizzle.regeneratingores.editing.EditManager;
import me.github.gavvydizzle.regeneratingores.gui.InventoryManager;
import me.github.gavvydizzle.regeneratingores.ore.NodeManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RegeneratingOres extends JavaPlugin {

    public static RegeneratingOres instance;
    public NodeManager nodeManager;
    public EditManager editManager;
    public InventoryManager inventoryManager;
    public AdminCommandManager adminCommandManager;

    @Override
    public void onEnable() {
        instance = this;
        nodeManager = new NodeManager();
        nodeManager.reloadAllNodes();
        editManager = new EditManager();
        inventoryManager = new InventoryManager();

        getServer().getPluginManager().registerEvents(nodeManager, this);
        getServer().getPluginManager().registerEvents(editManager, this);
        getServer().getPluginManager().registerEvents(inventoryManager, this);

        adminCommandManager = new AdminCommandManager();
        Objects.requireNonNull(getCommand("oreadmin")).setExecutor(adminCommandManager);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static RegeneratingOres getInstance() {
        return instance;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public EditManager getEditManager() {
        return editManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public AdminCommandManager getAdminCommandManager() {
        return adminCommandManager;
    }
}
