package me.github.gavvydizzle.regeneratingores.ore;

import me.github.gavvydizzle.regeneratingores.RegeneratingOres;
import me.github.gavvydizzle.regeneratingores.gui.RewardsInventory;
import me.github.gavvydizzle.regeneratingores.sorters.RewardWeightSorter;
import me.github.gavvydizzle.regeneratingores.utils.RandomSelector;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The rewards given from an OreBlock when mined
 */
public class BlockReward {

    private final int totalWeight;
    private ArrayList<Reward> rewardArrayList;
    private RandomSelector<Reward> rewards;
    private final Random random;
    private RewardsInventory rewardsInventory;

    /**
     * Generates the rewards from the config section types.x.rewards
     * @param config The config file
     * @param typePath The path in the form type.x
     * @param fileName The name of thew file for debugging
     */
    public BlockReward(FileConfiguration config, String typePath, String fileName, int oreNodeID) {
        totalWeight = 0;
        random = new Random();
        rewardsInventory = null;

        String topPath = typePath + ".rewards";
        config.addDefault(topPath, new HashMap<>());

        if (config.getConfigurationSection(topPath) == null) {
            return;
        }

        rewardArrayList = new ArrayList<>();
        for (String str : Objects.requireNonNull(config.getConfigurationSection(topPath)).getKeys(false)) {
            String path = topPath + "." + str;
            Reward reward = new Reward(config, path);
            if (reward.isActive()) {
                rewardArrayList.add(reward);
            }
        }

        try {
            rewards = RandomSelector.weighted(rewardArrayList, Reward::getWeight);
        } catch (Exception e) {
            RegeneratingOres.getInstance().getLogger().warning("Failed to load rewards for in " + fileName + " (" + topPath + ")");
            e.printStackTrace();
            rewards = null;
        }

        rewardArrayList.sort(new RewardWeightSorter());
        rewardsInventory = new RewardsInventory(this, oreNodeID);
    }

    /**
     * Picks a random reward from the list and gives it to the player
     * @param player The player to reward
     */
    public void rewardPlayer(Player player) {
        if (rewards == null) return;

        Reward reward = rewards.next(random);
        reward.sendMessage(player);
        reward.runCommands(player);
    }

    public ArrayList<Reward> getRewardArrayList() {
        return rewardArrayList;
    }

    public RewardsInventory getRewardsInventory() {
        return rewardsInventory;
    }

}
