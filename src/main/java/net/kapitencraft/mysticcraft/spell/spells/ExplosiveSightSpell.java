package net.kapitencraft.mysticcraft.spell.spells;

import net.kapitencraft.mysticcraft.misc.MISCTools;
import net.kapitencraft.mysticcraft.spell.Spell;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ExplosiveSightSpell extends Spell {

    private static final Component[] description = new Component[] {Component.literal("Explodes in a range of 150")};
    public ExplosiveSightSpell() {
        super(150, "Explosive Sight", "1010110", Type.RELEASE, Rarity.UNCOMMON);
    }

    public static void execute(LivingEntity user, ItemStack stack) {
        ArrayList<Vec3> sight = MISCTools.LineOfSight(user, 150, 0.5);
        for (Vec3 vec3 : sight) {
            if ((user.level.getBlockState(new BlockPos(vec3)).canOcclude())) {
                if (user.level.isClientSide()) {

                    break;
                }
            }
        }
    }

    public static List<Component> getDescription() {
        return List.of(description);
    }
}
