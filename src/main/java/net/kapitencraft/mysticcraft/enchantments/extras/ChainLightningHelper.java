package net.kapitencraft.mysticcraft.enchantments.extras;

import net.kapitencraft.mysticcraft.helpers.MiscHelper;
import net.minecraft.world.entity.LivingEntity;

public class ChainLightningHelper {

    public static void spawnLightnings(int level, LivingEntity target, LivingEntity owner, float damage) {
        int spawnAmount = (int) (Math.sqrt(level));
        MiscHelper.repeatXTimes(spawnAmount, integer -> {
            new ChainLightning(target, owner, damage * (1 + level * 0.05f));
        });
    }
}
