package net.kapitencraft.mysticcraft.item.combat.weapon.melee.dagger;

import net.kapitencraft.mysticcraft.helpers.MiscHelper;
import net.kapitencraft.mysticcraft.item.combat.weapon.melee.sword.ModSwordItem;
import net.kapitencraft.mysticcraft.mixin.duck.IAttacker;
import net.kapitencraft.mysticcraft.networking.ModMessages;
import net.kapitencraft.mysticcraft.networking.packets.S2C.SwingPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;

public abstract class ModDaggerItem extends ModSwordItem {
    public ModDaggerItem(Tier p_43269_, int attackDamage, Properties p_43272_) {
        super(p_43269_, attackDamage, -1.8f, p_43272_);
    }

    /**
     * code to attack the target with the offhand if player was
     */
    @Override
    public boolean hurtEnemy(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        super.hurtEnemy(pStack, pTarget, pAttacker);
        ItemStack offhand = pAttacker.getOffhandItem();
        if (offhand.getItem() == this && pAttacker instanceof Player player && pTarget.isAlive()) {
            MiscHelper.delayed(10, ()-> {
                if (player.canHit(pTarget, 0) && !IAttacker.of(player).isOffhandAttack()) {
                    MiscHelper.swapHands(pAttacker);
                    IAttacker.of(player).setOffhandAttack();
                    player.attack(pTarget);
                    ModMessages.sendToAllConnectedPlayers(player1 -> new SwingPacket(InteractionHand.OFF_HAND, player.getId()), (ServerLevel) player.level);
                    IAttacker.of(player).setMainhandAttack();
                    MiscHelper.swapHands(pAttacker);
                }
            });
        }
        return true;
    }
}
