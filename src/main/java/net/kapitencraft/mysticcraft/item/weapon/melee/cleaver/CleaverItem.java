package net.kapitencraft.mysticcraft.item.weapon.melee.cleaver;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.init.ModAttributes;
import net.kapitencraft.mysticcraft.item.weapon.melee.sword.ModdedSwordItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;

public abstract class CleaverItem extends ModdedSwordItem {

    public CleaverItem(Tier p_43269_, int attackDamage, Properties p_43272_) {
        super(p_43269_, attackDamage, -2.8f, p_43272_);
    }

    protected abstract double getArmorShredderBonus();

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.putAll(super.getDefaultAttributeModifiers(slot));
        if (slot == EquipmentSlot.MAINHAND) {
            builder.put(ModAttributes.ARMOR_SHREDDER.get(), new AttributeModifier(MysticcraftMod.ITEM_ATTRIBUTE_MODIFIER_ADD_FOR_SLOT[5], "Shredder Bonus for Cleaver", this.getArmorShredderBonus(), AttributeModifier.Operation.ADDITION));
        }
        return builder.build();
    }
}
