package me.github.gavvydizzle.regeneratingores.sorters;

import me.github.gavvydizzle.regeneratingores.ore.OreBlock;

import java.util.Comparator;

public class OreBlockWeightSorter implements Comparator<OreBlock> {

    @Override
    public int compare(OreBlock o1, OreBlock o2) {
        if (o1.getWeight() == o2.getWeight()) {
            return o2.getMaterial().toString().compareTo(o1.getMaterial().toString());
        }
        return Double.compare(o2.getWeight(), o1.getWeight());
    }
}