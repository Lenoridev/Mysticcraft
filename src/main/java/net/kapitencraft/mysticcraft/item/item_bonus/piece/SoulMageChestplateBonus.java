package net.kapitencraft.mysticcraft.item.item_bonus.piece;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.init.ModAttributes;
import net.kapitencraft.mysticcraft.item.item_bonus.PieceBonus;
import net.kapitencraft.mysticcraft.misc.utils.MiscUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulMageChestplateBonus extends PieceBonus {
    public SoulMageChestplateBonus() {
        super("More Mana!");
    }

    @Override
    public void onEntityKilled(LivingEntity killed, LivingEntity user, MiscUtils.DamageType type) {

    }

    @Nullable
    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(LivingEntity living) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.put(ModAttributes.MAX_MANA.get(), new AttributeModifier("Chest bonus", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return builder.build();
    }

    @Override
    public void onTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slotID, boolean isSelected, int ticks) {

    }

    @Override
    public List<Component> getDisplay() {
        List<Component> components = super.getDisplay();
        components.add(Component.literal("Increases your maximum Mana by 20%"));
        return components;
    }
}
