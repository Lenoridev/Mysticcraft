package net.kapitencraft.mysticcraft.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.item.gemstone.IGemstoneApplicable;
import net.kapitencraft.mysticcraft.item.item_bonus.FullSetBonus;
import net.kapitencraft.mysticcraft.item.item_bonus.IArmorBonusItem;
import net.kapitencraft.mysticcraft.item.item_bonus.PieceBonus;
import net.kapitencraft.mysticcraft.misc.utils.AttributeUtils;
import net.kapitencraft.mysticcraft.misc.utils.MiscUtils;
import net.kapitencraft.mysticcraft.misc.utils.TextUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class ModArmorItem extends ArmorItem {
    private static final String FULL_SET_ID = "hadFullSet";
    protected String dimension;
    private Entity user;
    protected int fullSetTick = 0;

    public ModArmorItem(ArmorMaterial p_40386_, EquipmentSlot p_40387_, Properties p_40388_) {
        super(p_40386_, p_40387_, p_40388_);
    }
    public boolean equals(ArmorItem item) {
        MysticcraftMod.sendInfo(item.getMaterial().getName());
        return this == item || item.getMaterial() == this.getMaterial();
    }

    public interface Creator {
        ModArmorItem create(EquipmentSlot slot);
    }

    public static HashMap<EquipmentSlot, RegistryObject<Item>> createRegistry(DeferredRegister<Item> register, String registryName, Creator creator) {
        HashMap<EquipmentSlot, RegistryObject<Item>> registry = new HashMap<>();
        for (EquipmentSlot slot : MiscUtils.ARMOR_EQUIPMENT) {
            registry.put(slot, register.register(registryName + "_" + TextUtils.getRegistryNameForSlot(slot), () -> creator.create(slot)));
        }
        return registry;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, @NotNull List<Component> toolTip, @NotNull TooltipFlag p_41424_) {
        if (stack.getItem() instanceof IGemstoneApplicable gemstoneApplicable) {
            gemstoneApplicable.appendDisplay(stack, toolTip);
            toolTip.add(Component.literal(""));
        }
        if (stack.getItem() instanceof IArmorBonusItem bonusItem) {
            bonusItem.addDisplay(toolTip, this.getSlot());
            toolTip.add(Component.literal(""));
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slotID, boolean isSelected) {
        if (entity instanceof LivingEntity living) {
            CompoundTag tag = living.getPersistentData();
            if (this.isFullSetActive(living)) {
                if (this.getSlot() == EquipmentSlot.CHEST) {
                    if (this.fullSetTick == 0) {
                        MysticcraftMod.sendInfo("init Full Set");
                        if (!level.isClientSide) this.initFullSetTick(stack, level, living);
                        tag.putBoolean(FULL_SET_ID, true);
                        tag.putString("lastFullSet", this.getMaterial().getName());
                    }
                    this.fullSetTick++;
                    if (level.isClientSide) {
                        this.clientFullSetTick(stack, level, living);
                    } else {
                        this.fullSetTick(stack, level, living);
                    }
                }
            } else {
                if (living.getPersistentData().getBoolean(FULL_SET_ID)) {
                    MysticcraftMod.sendInfo("post Full Set");
                    if (!level.isClientSide) postFullSetTick(stack, level, living);
                    tag.putBoolean(FULL_SET_ID, false);
                }
                fullSetTick = 0;
            }
        }
        if (stack.getItem() == this) {
            updateDimension(entity.level);
            updateUser(entity);
        }
    }
    public boolean isFullSetActive(LivingEntity living) {
        return isFullSetActive(living, this.getMaterial());
    }

    public static boolean isFullSetActive(LivingEntity living, ArmorMaterial materials) {
        if (living == null) {
            return false;
        }
        ArmorItem head = living.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem armorItem ? armorItem : null;
        Item chestPlate = living.getItemBySlot(EquipmentSlot.CHEST).getItem();
        ArmorItem chest;
        if (chestPlate instanceof ElytraItem || chestPlate instanceof AirItem) {
            return false;
        } else {
            chest = (ArmorItem) living.getItemBySlot(EquipmentSlot.CHEST).getItem();
        }
        ArmorItem legs = living.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem armorItem ? armorItem : null;
        ArmorItem feet = living.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ArmorItem armorItem ? armorItem : null;
        return (head != null && legs != null && feet != null) && (head.getMaterial() == materials && chest.getMaterial() == materials && legs.getMaterial() == materials && feet.getMaterial() == materials);
    }

    protected abstract void fullSetTick(ItemStack stack, Level level, LivingEntity living);
    protected abstract void initFullSetTick(ItemStack stack, Level level, LivingEntity living);
    protected abstract void postFullSetTick(ItemStack stack, Level level, LivingEntity living);
    protected abstract void clientFullSetTick(ItemStack stack, Level level, LivingEntity living);

    public abstract Multimap<Attribute, AttributeModifier> getAttributeMods(EquipmentSlot slot);

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.putAll(super.getDefaultAttributeModifiers(slot));
        if (this.getAttributeMods(slot) != null) {
            builder.putAll(this.getAttributeMods(slot));
        }
        return builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        builder.putAll(this.getDefaultAttributeModifiers(slot));
        if (slot == this.getSlot()) {
            if (this instanceof IArmorBonusItem bonusItem && this.user instanceof LivingEntity living) {
                PieceBonus bonus = bonusItem.getPieceBonusForSlot(this.getSlot());
                if (bonus != null) {
                    Multimap<Attribute, AttributeModifier> bonusMods = bonus.getModifiers(living);
                    if (bonusMods != null ) {
                        builder.putAll(bonusMods);
                    }
                }
                FullSetBonus bonus1 = bonusItem.getFullSetBonus();
                if (bonus1 != null) {
                    Multimap<Attribute, AttributeModifier> bonusMods = bonus1.getModifiers(living);
                    if (this.getSlot() == EquipmentSlot.CHEST && this.isFullSetActive(living) && bonusMods != null) {
                        builder.putAll(bonusMods);
                    }
                }
            }
        }
        if (stack.getTag() != null && stack.getTag().getBoolean("isOP")) {
            return AttributeUtils.increaseByPercent(builder.build(), 300, new AttributeModifier.Operation[]{AttributeModifier.Operation.ADDITION, AttributeModifier.Operation.MULTIPLY_TOTAL, AttributeModifier.Operation.MULTIPLY_BASE}, null);
        }
        return builder.build();
    }

    protected void updateDimension(Level level) {
        this.dimension = MiscUtils.getDimension(level);
    }

    private void updateUser(Entity user) {
        this.user = user;
    }
}