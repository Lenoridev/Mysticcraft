package net.kapitencraft.mysticcraft.enchantments;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.enchantments.abstracts.StatBoostEnchantment;
import net.kapitencraft.mysticcraft.init.ModAttributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ElvishMasteryEnchantment extends StatBoostEnchantment implements IWeaponEnchantment {
    public ElvishMasteryEnchantment() {
        super(Enchantment.Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(int level, ItemStack enchanted, EquipmentSlot slot) {
        return new ImmutableMultimap.Builder<Attribute, AttributeModifier>().put(ModAttributes.DRAW_SPEED.get(), MysticcraftMod.createModifier(AttributeModifier.Operation.MULTIPLY_TOTAL, 0.1*level, EquipmentSlot.MAINHAND)).build();
    }
    @Override
    public boolean hasModifiersForThatSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND;
    }
}
