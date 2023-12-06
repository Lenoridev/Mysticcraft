package net.kapitencraft.mysticcraft.enchantments.armor;

import net.kapitencraft.mysticcraft.enchantments.abstracts.ExtendedCalculationEnchantment;
import net.kapitencraft.mysticcraft.enchantments.abstracts.IArmorEnchantment;
import net.kapitencraft.mysticcraft.enchantments.abstracts.IUltimateEnchantment;
import net.kapitencraft.mysticcraft.helpers.MathHelper;
import net.kapitencraft.mysticcraft.helpers.ParticleHelper;
import net.kapitencraft.mysticcraft.misc.cooldown.Cooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class BonkEnchantment extends ExtendedCalculationEnchantment implements IUltimateEnchantment, IArmorEnchantment {
    public static final String BONK_ID = "Bonk Enchantment";

    public BonkEnchantment() {
        super(DEFAULT_RARITY, EnchantmentCategory.ARMOR, DEFAULT_SLOTS, CalculationType.ALL, ProcessPriority.LOWEST);
    }

    @Override
    protected double execute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, double damage, DamageSource source) {
        CompoundTag tag = attacked.getPersistentData();
        CompoundTag tag1 = tag.getCompound(BONK_ID);
        EquipmentSlot slot = enchanted.getItem() instanceof ArmorItem armorItem ? armorItem.getSlot() : null;
        if (slot != null) {
            String name = slot.getName();
            boolean isActive = tag1.getBoolean(name);
            if (isActive) {
                ParticleHelper.sendParticles(attacked.level, ParticleTypes.EXPLOSION, false, MathHelper.getPosition(attacked), 2, 0, 0, 0, 0);
                Cooldowns.BONK_ENCHANTMENT(slot).applyCooldown(attacked, true);
                return 0;
            }
        }
        return damage;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment ench) {
        return !(ench instanceof IUltimateEnchantment);
    }

    @Override
    public String[] getDescriptionMods(int level) {
        return new String[] {};
    }
}
