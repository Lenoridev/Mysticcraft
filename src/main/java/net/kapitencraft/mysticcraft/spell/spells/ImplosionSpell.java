package net.kapitencraft.mysticcraft.spell.spells;

import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.misc.FormattingCodes;
import net.kapitencraft.mysticcraft.misc.damage_source.AbilityDamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class ImplosionSpell {

    public static void execute(LivingEntity user, ItemStack stack) {
        final Vec3 center = new Vec3((user.getX()), (user.getY()), (user.getZ()));
        List<LivingEntity> entFound = user.level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(5), e -> true).stream().sorted(Comparator.comparingDouble(entCnd -> entCnd.distanceToSqr(center))).toList();
        double damageInflicted = 0; double EnemyHealth; int damaged = 0;
        for (LivingEntity entityIterator : entFound) {
            if (!(entityIterator == user)) {
                EnemyHealth = entityIterator.getHealth();
                entityIterator.hurt(new AbilityDamageSource(user, 0.1f, "implosion"), 5);
                damageInflicted += (EnemyHealth - entityIterator.getHealth());
                damaged++;
            }
        }
        if (!user.level.isClientSide() && user instanceof Player player && damaged > 0) {
            String red = FormattingCodes.RED;
            player.sendSystemMessage(Component.literal("Your Implosion hit " + red + damaged + FormattingCodes.RESET + " Enemies for " + red + MysticcraftMod.MAIN_FORMAT.format(damageInflicted) + " Damage"));
        }
    }
    public static List<Component> getDescription() {
        return List.of(Component.literal("Deals 5 Damage to Enemies 5 Blocks Around you"));
    }
}
