package net.kapitencraft.mysticcraft.spell.spells;

import net.kapitencraft.mysticcraft.init.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class WitherShieldSpell {

    private static final List<Component> description = List.of(Component.literal("Gain a Absorption Shield of 30% of your Crit Damage."), Component.literal("also, gain 50% of this shield as healing after 5 seconds"));
    public static final String DAMAGE_REDUCTION_TIME = "WS-DamageReductionTime";
    public static final String ABSORPTION_AMOUNT_ID = "WS-AbsorptionAmount";

    public static void execute(LivingEntity user, ItemStack stack) {
        CompoundTag tag = user.getPersistentData();
        tag.putInt(DAMAGE_REDUCTION_TIME, 100);
        float absorption = (float) (user.getAttributeValue(ModAttributes.CRIT_DAMAGE.get()) * 0.3);
        if (tag.getFloat(ABSORPTION_AMOUNT_ID) <= 0 || !tag.contains(ABSORPTION_AMOUNT_ID)) {
            user.setAbsorptionAmount(user.getAbsorptionAmount() + absorption);
        }
        tag.putFloat(ABSORPTION_AMOUNT_ID, absorption);
    }

    public static List<Component> getDescription() {
        return description;
    }
}
