package net.kapitencraft.mysticcraft.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class AimEnchantment extends ModEnchantment {
    public AimEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND}, Type.DAMAGE_CALC);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public double execute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, double damage) {
        return 0;
    }
}
