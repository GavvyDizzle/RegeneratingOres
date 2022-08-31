package me.github.gavvydizzle.regeneratingores.gui;

import me.github.gavvydizzle.regeneratingores.ore.OreNode;

public class InventoryPlayer {

    private final OreNode oreNode;
    private ClickableGUI clickableGUI;

    public InventoryPlayer(OreNode oreNode, ClickableGUI clickableGUI) {
        this.oreNode = oreNode;
        this.clickableGUI = clickableGUI;
    }

    public OreNode getOreNode() {
        return oreNode;
    }

    public ClickableGUI getClickableGUI() {
        return clickableGUI;
    }

    public void setClickableGUI(ClickableGUI clickableGUI) {
        this.clickableGUI = clickableGUI;
    }
}
