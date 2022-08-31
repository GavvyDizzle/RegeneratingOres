package me.github.gavvydizzle.regeneratingores.sorters;

import me.github.gavvydizzle.regeneratingores.ore.OreNode;

import java.util.Comparator;

public class OreNodeSorter implements Comparator<OreNode> {

    @Override
    public int compare(OreNode o1, OreNode o2) {
        return o2.getDisplayName().compareTo(o1.getDisplayName());
    }
}
