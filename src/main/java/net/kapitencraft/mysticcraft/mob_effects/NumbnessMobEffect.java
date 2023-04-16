package net.kapitencraft.mysticcraft.mob_effects;

import net.kapitencraft.mysticcraft.init.ModMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class NumbnessMobEffect extends MobEffect {
    public static final String NUMBNESS_ID = "numbnessAmount";
    public NumbnessMobEffect() {
        super(MobEffectCategory.HARMFUL, -10092442);
    }

    @Override
    public boolean isDurationEffectTick(int p_19455_, int p_19456_) {
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity living, AttributeMap attributeMap, int amplifier) {
        MobEffectInstance numbness = living.getEffect(ModMobEffects.NUMBNESS.get());
        if (numbness != null && numbness.getDuration() < 1) {
            living.hurt(new DamageSource("numbness"), living.getPersistentData().getFloat(NUMBNESS_ID));
            living.getPersistentData().putFloat(NUMBNESS_ID, 0);
        }
        super.removeAttributeModifiers(living, attributeMap, amplifier);
    }
}
