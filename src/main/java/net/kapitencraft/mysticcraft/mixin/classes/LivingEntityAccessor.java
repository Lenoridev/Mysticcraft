package net.kapitencraft.mysticcraft.mixin.classes;


import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Accessor
    boolean getJumping();

    @Accessor
    int getNoJumpDelay();

    @Accessor
    void setNoJumpDelay(int delay);
}
