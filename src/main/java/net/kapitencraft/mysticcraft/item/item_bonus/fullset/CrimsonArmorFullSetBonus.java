package net.kapitencraft.mysticcraft.item.item_bonus.fullset;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.entity.CrimsonDeathRayProjectile;
import net.kapitencraft.mysticcraft.item.item_bonus.FullSetBonus;
import net.kapitencraft.mysticcraft.utils.AttributeUtils;
import net.kapitencraft.mysticcraft.utils.MiscUtils;
import net.kapitencraft.mysticcraft.utils.TagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class CrimsonArmorFullSetBonus extends FullSetBonus {
    private static final String COOLDOWN_ID = "DominusCooldown";
    private static final String DOMINUS_ID = "Dominus";
    public CrimsonArmorFullSetBonus() {
        super("Dominus");
    }

    @Override
    public void onEntityKilled(LivingEntity killed, LivingEntity user, MiscUtils.DamageType type) {
        if (type != MiscUtils.DamageType.MELEE) return;
        CompoundTag data = user.getPersistentData();
        if (data.getInt(DOMINUS_ID) < 10) {
            TagUtils.increaseIntegerTagValue(user.getPersistentData(), DOMINUS_ID, 1);
        } else {
            float YRot = user.getYRot();
            for (int i = 0; i < 3; i++) {
                float curRor = YRot + (120 * i);
                CrimsonDeathRayProjectile projectile = CrimsonDeathRayProjectile.createProjectile(user.level, user, curRor);
                user.level.addFreshEntity(projectile);
            }
        }
        data.putInt(COOLDOWN_ID, 120);
    }

    @Override
    public void onTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity) {
        CompoundTag data = entity.getPersistentData();
        int dominusCooldown = data.getInt(COOLDOWN_ID);
        int dominus = data.getInt(DOMINUS_ID);
        if (dominusCooldown-- <= 0 && dominus > 0) {
            dominus--;
            dominusCooldown = 120;
        }
        data.putInt(COOLDOWN_ID, dominusCooldown);
        data.putInt(DOMINUS_ID, dominus);
    }

    @Override
    public Consumer<List<Component>> getDisplay() {
        return list -> list.addAll(
                List.of(Component.literal("every melee kill grants 1 stack of §6Dominus§r."),
                Component.literal(""),
                Component.literal("for every stack of §6Dominus§r gain +0.1 attack range"),
                Component.literal(""),
                Component.literal("when reaching 10 stacks of §6Dominus§r, spawn 3"),
                Component.literal("§4Crimson Death Ray§rs that follow enemies for 4 sec."),
                Component.literal(""),
                Component.literal("lose 1 Dominus after not getting"),
                Component.literal("a stack of §6Dominus§r for 6 seconds.")));
    }

    @Nullable
    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(LivingEntity living) {
        HashMultimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
        multimap.put(ForgeMod.ATTACK_RANGE.get(), AttributeUtils.addLiquidModifier(UUID.randomUUID(), "Dominus Modifier", AttributeModifier.Operation.ADDITION, value -> value.getPersistentData().getInt(DOMINUS_ID) * 0.1, living));
        return super.getModifiers(living);
    }
}
