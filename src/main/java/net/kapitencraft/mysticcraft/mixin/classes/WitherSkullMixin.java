package net.kapitencraft.mysticcraft.mixin.classes;

import net.kapitencraft.mysticcraft.item.material.PrecursorRelicItem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.projectile.WitherSkull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WitherSkull.class)
public abstract class WitherSkullMixin {

    private WitherSkull self() {
        return (WitherSkull) (Object) this;
    }

    @ModifyConstant(method = "onHitEntity")
    private float damage(float value) {
        if (self().getOwner() instanceof WitherBoss boss) {
            PrecursorRelicItem.BossType bossType = PrecursorRelicItem.BossType.fromBoss(boss);
            if (bossType == PrecursorRelicItem.BossType.MAXOR && value == 8) {
                return 24;
            } else if (bossType == PrecursorRelicItem.BossType.STORM && value == 1) {
                return 5;
            }
        }
        return value;
    }
}
