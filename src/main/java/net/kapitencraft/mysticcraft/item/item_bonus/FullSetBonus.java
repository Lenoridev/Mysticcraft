package net.kapitencraft.mysticcraft.item.item_bonus;

import net.kapitencraft.mysticcraft.helpers.MiscHelper;
import net.kapitencraft.mysticcraft.misc.cooldown.Cooldown;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FullSetBonus extends MultiPieceBonus {
    protected FullSetBonus(String name) {
        super(name);
    }

    @Override
    public void onApply(LivingEntity living) {
    }

    @Override
    public void onUse() {
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public void onTick(Level level, @NotNull LivingEntity entity) {
    }

    @Override
    public void onEntityKilled(LivingEntity killed, LivingEntity user, MiscHelper.DamageType type) {
    }

    @Override
    public void onRemove(LivingEntity living) {
    }

    @Override
    public String getSuperName() {
        return "Full Set";
    }
}
