package me.github.gavvydizzle.regeneratingores.sorters;

import me.github.gavvydizzle.regeneratingores.ore.Reward;

import java.util.Comparator;

public class RewardWeightSorter implements Comparator<Reward> {

    @Override
    public int compare(Reward o1, Reward o2) {
        return Double.compare(o2.getWeight(), o1.getWeight());
    }
}