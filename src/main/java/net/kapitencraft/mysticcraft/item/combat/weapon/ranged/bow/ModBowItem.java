package net.kapitencraft.mysticcraft.item.combat.weapon.ranged.bow;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.helpers.MiscHelper;
import net.kapitencraft.mysticcraft.init.ModAttributes;
import net.kapitencraft.mysticcraft.init.ModEnchantments;
import net.kapitencraft.mysticcraft.item.data.gemstone.IGemstoneApplicable;
import net.kapitencraft.mysticcraft.item.misc.IModItem;
import net.kapitencraft.mysticcraft.item.misc.creative_tab.TabGroup;
import net.kapitencraft.mysticcraft.item.misc.creative_tab.TabRegister;
import net.kapitencraft.mysticcraft.misc.FormattingCodes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ModBowItem extends BowItem implements IModItem {
    public static final TabGroup BOW_GROUP = new TabGroup(TabRegister.TabTypes.WEAPONS_AND_TOOLS);

    @Override
    public TabGroup getGroup() {
        return BOW_GROUP;
    }

    public abstract double getDivider();

    public abstract int getKB();

    public ModBowItem(Item.Properties p_40660_) {
        super(p_40660_);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack bow, @NotNull Level world, @NotNull LivingEntity archer, int timeLeft) {
        if (archer instanceof Player player) {
            boolean flag = player.getAbilities().instabuild || bow.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0;
            ItemStack itemstack = player.getProjectile(bow);
            int i = this.getUseDuration(bow) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(bow, world, player, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;
            if (flag) itemstack = new ItemStack(Items.ARROW);
            if (!itemstack.isEmpty()) {
                float f = getPowerForTime(i);
                if (!(f < 0.1f)) {
                    boolean flag1 = player.getAbilities().instabuild || (itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, bow, player));
                    if (!world.isClientSide) {
                        final int mul = 3;
                        ArrowItem arrowitem = (ArrowItem)(itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(world, itemstack, player);
                        double fbSpeedMul = LongBowItem.ARROW_SPEED_MUL / mul;
                        double speedMul = archer.getAttributeValue(ModAttributes.ARROW_SPEED.get());
                        abstractarrow.setBaseDamage(player.getAttributeValue(ModAttributes.RANGED_DAMAGE.get()));
                        abstractarrow.setKnockback(2);
                        if (f == 1.0F) {
                            abstractarrow.setCritArrow(true);
                        }
                        registerEnchant(bow, abstractarrow);
                        bow.hurtAndBreak(1, player, (p_40665_) -> p_40665_.broadcastBreakEvent(player.getUsedItemHand()));
                        if (flag1 || player.getAbilities().instabuild && (itemstack.is(Items.SPECTRAL_ARROW) || itemstack.is(Items.TIPPED_ARROW))) {
                            abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }
                        createLegolasExtraArrows(bow, archer, this.getKB());
                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, (float) (f * mul * (1+ getSpeedMul(abstractarrow, fbSpeedMul, speedMul))), 1.0F);
                        world.addFreshEntity(abstractarrow);
                    }
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    public static void createLegolasExtraArrows(@NotNull ItemStack bow, @NotNull LivingEntity archer, int kb) {
        int legolasLevel = bow.getEnchantmentLevel(ModEnchantments.LEGOLAS_EMULATION.get());
        for (int j = 0; j < legolasLevel; j++) {
            float yChange = (float) (Math.random() * (5 - legolasLevel) - (5 - legolasLevel) / 2);
            float xChange = (float) (Math.random() * (5 - legolasLevel) - (5 - legolasLevel) / 2);
            createArrowProperties(archer, bow, kb, archer.getXRot() + xChange, archer.getYRot() + yChange);
        }
    }

    public static AbstractArrow createArrowProperties(LivingEntity archer, ItemStack bow, int kb, float rotX, float rotY) {
        Level world = archer.level;
        ItemStack arrowStack = archer.getProjectile(bow);
        if (!arrowStack.isEmpty() && arrowStack.getItem() instanceof ArrowItem arrowItem) {
            AbstractArrow arrow = arrowItem.createArrow(world, arrowStack, archer);
            arrow.shootFromRotation(archer, rotX, rotY, 0.0F, (float) (5 + archer.getAttributeValue(ModAttributes.ARROW_SPEED.get()) * 0.02), 1.0F);
            arrow.setBaseDamage(archer.getAttributeValue(ModAttributes.RANGED_DAMAGE.get()));
            arrow.setKnockback(kb);
            arrow.setCritArrow(true);
            registerEnchant(bow, arrow);
            bow.hurtAndBreak(1, archer, (p_40665_) -> p_40665_.broadcastBreakEvent(archer.getUsedItemHand()));
            world.addFreshEntity(arrow);
            if (!(archer instanceof Player player && player.getAbilities().instabuild) || bow.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0) {
                arrowStack.shrink(1);
            }
            world.playSound(null, archer.getX(), archer.getY(), archer.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            return arrow;
        } else { return null;}
    }



    protected static AbstractArrow registerEnchant(ItemStack bow, AbstractArrow arrow) {
        int j = bow.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        if (j > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + (double)j * 0.5D + 0.5D);
        }

        int k = bow.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
        if (k > 0) {
            arrow.setKnockback(arrow.getKnockback() + k);
        }

        if (bow.getEnchantmentLevel(Enchantments.FLAMING_ARROWS) > 0) {
            arrow.setSecondsOnFire(100);
        }
        return arrow;
    }
    protected double getSpeedMul(AbstractArrow arrow, double mul1, double mul2) {
        return arrow.getDeltaMovement().x * mul1 * (1 + mul2 * 0.1);
    }

    @Override
    public @NotNull Rarity getRarity(ItemStack p_41461_) {
        if (!p_41461_.isEnchanted()) {
            return super.getRarity(p_41461_);
        } else {
            final Rarity rarity = MiscHelper.getItemRarity(this);
            if (rarity == Rarity.COMMON) {
                return Rarity.UNCOMMON;
            } else if (rarity == Rarity.UNCOMMON) {
                return Rarity.RARE;
            } else if (rarity == Rarity.RARE) {
                return Rarity.EPIC;
            } else if (rarity == Rarity.EPIC) {
                return FormattingCodes.LEGENDARY;
            } else if (rarity == FormattingCodes.LEGENDARY) {
                return FormattingCodes.MYTHIC;
            } else if (rarity == FormattingCodes.MYTHIC) {
                return FormattingCodes.DIVINE;
            } else {
                return Rarity.COMMON;
            }
        }
    }

    public abstract double getDamage();

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slot) {
        HashMultimap<Attribute, AttributeModifier> builder = HashMultimap.create();
        if (slot == EquipmentSlot.MAINHAND) {
            builder.put(ModAttributes.RANGED_DAMAGE.get(), new AttributeModifier(MysticcraftMod.ITEM_ATTRIBUTE_MODIFIER_ADD_FOR_SLOT[5], "Damage Modifier", this.getDamage(), AttributeModifier.Operation.ADDITION));
        }
        return builder;
    }

    @Override
    public void appendHoverTextWithPlayer(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag, Player player) {
        if (stack.getItem() instanceof IGemstoneApplicable gemstoneApplicable) {
            gemstoneApplicable.appendDisplay(stack, list);
        }
    }
}