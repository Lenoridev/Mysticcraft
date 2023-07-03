package net.kapitencraft.mysticcraft.spell.spells;

import net.kapitencraft.mysticcraft.init.ModEntityTypes;
import net.kapitencraft.mysticcraft.init.ModParticleTypes;
import net.kapitencraft.mysticcraft.misc.damage_source.IndirectAbilityDamageSource;
import net.kapitencraft.mysticcraft.misc.utils.MathUtils;
import net.kapitencraft.mysticcraft.misc.utils.ParticleUtils;
import net.kapitencraft.mysticcraft.spell.Spells;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FireBoltProjectile extends SpellProjectile {
    private static final int ParticleAmount = 20;
    private int inGroundTime = 0;
    private final boolean explosive;
    private final double damage;
    private FireBoltProjectile(Level level, LivingEntity living, boolean explosive, double damage, String name) {
        super(ModEntityTypes.FIRE_BOLD.get(), living, level, name);
        this.inGroundTime = 0;
        this.setInvisible(true);
        this.setNoGravity(true);
        this.explosive = explosive;
        this.damage = damage;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("inGroundTime", this.inGroundTime);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.inGroundTime = tag.getInt("inGroundTime");
    }

    public FireBoltProjectile(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level, Spells.FIRE_BOLT_1.getName());
        this.explosive = false;
        this.damage = 1;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void tick() {
        super.tick();
        ParticleUtils.sendParticles(this.level, (SimpleParticleType) ModParticleTypes.FIRE_NORMAL.get(), true, this.getX() - this.getDeltaMovement().x / 5, this.getY() - this.getDeltaMovement().y / 5, this.getZ() - this.getDeltaMovement().z / 5, ParticleAmount, 0.125, 0.125, 0.125, 0);
        ParticleUtils.sendParticles(this.level, (SimpleParticleType) ModParticleTypes.FIRE_NORMAL.get(), true, this.getX(), this.getY(), this.getZ(), ParticleAmount, 0.125, 0.125, 0.125, 0);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        if (entity instanceof LivingEntity living) {
            damage(living);
        }
        super.onHitEntity(hitResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult hitResult) {
        if (this.explosive) {
            List<LivingEntity> livingEntities = MathUtils.getLivingAround(this, 3);
            for (LivingEntity living : livingEntities) {
                damage(living);
            }
            ParticleUtils.sendParticles(this.level, (SimpleParticleType) ModParticleTypes.FIRE_NORMAL.get(), true, this.getX(), this.getY(), this.getZ(), ParticleAmount * 10, 0.125, 0.125, 0.125, 1.25);
            ParticleUtils.sendParticles(this.level, ParticleTypes.EXPLOSION, true, this.getX(), this.getY(), this.getZ(), ParticleAmount / 2, 0.125, 0.125, 0.125, 0);
            if (this.getOwner() instanceof Player player) {
                this.level.playSound(player, hitResult.getBlockPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);
            }
        }
        super.onHitBlock(hitResult);
    }

    private void damage(LivingEntity living) {
        this.addHitEntity(living);
        float health = living.getHealth();
        living.hurt(new IndirectAbilityDamageSource(this, this.getOwner(), 0.6f, this.name.toLowerCase().replace(" ", "_")), (float) this.damage);
        this.damageInflicted += (health - living.getHealth());
        living.setSecondsOnFire((int) Math.floor(this.damage));
    }

    public static FireBoltProjectile createProjectile(Level level, LivingEntity owner, boolean explosive, double damage, String name) {
        return new FireBoltProjectile(level, owner, explosive, damage, name);
    }
}