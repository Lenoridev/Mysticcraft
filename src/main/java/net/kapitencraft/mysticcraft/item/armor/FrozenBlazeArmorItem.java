package net.kapitencraft.mysticcraft.item.armor;

import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.item.armor.client.FrozenBlazeRenderer;
import net.kapitencraft.mysticcraft.item.item_bonus.ExtraBonus;
import net.kapitencraft.mysticcraft.item.item_bonus.FullSetBonus;
import net.kapitencraft.mysticcraft.item.item_bonus.IArmorBonusItem;
import net.kapitencraft.mysticcraft.item.item_bonus.PieceBonus;
import net.kapitencraft.mysticcraft.item.item_bonus.fullset.FrozenBlazeFullSetBonus;
import net.kapitencraft.mysticcraft.item.item_bonus.fullset.SoulMageArmorFullSetBonus;
import net.kapitencraft.mysticcraft.misc.FormattingCodes;
import net.kapitencraft.mysticcraft.misc.damage_source.FrozenDamageSource;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FrozenBlazeArmorItem extends ModArmorItem implements GeoItem, IArmorBonusItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final FrozenBlazeFullSetBonus FULL_SET_BONUS = new FrozenBlazeFullSetBonus();
    public FrozenBlazeArmorItem(EquipmentSlot p_40387_) {
        super(ModArmorMaterials.FROZEN_BLAZE, p_40387_, new Item.Properties().rarity(FormattingCodes.LEGENDARY).fireResistant());
    }

    public static HashMap<EquipmentSlot, RegistryObject<Item>> createRegistry(DeferredRegister<Item> register, String registryName) {
        HashMap<EquipmentSlot, RegistryObject<Item>> registry = new HashMap<>();
        registry.put(EquipmentSlot.HEAD, register.register(registryName + "_helmet", ()-> new FrozenBlazeArmorItem(EquipmentSlot.HEAD)));
        registry.put(EquipmentSlot.CHEST, register.register(registryName + "_chestplate", ()-> new FrozenBlazeArmorItem(EquipmentSlot.CHEST)));
        registry.put(EquipmentSlot.LEGS, register.register(registryName + "_leggings", ()-> new FrozenBlazeArmorItem(EquipmentSlot.LEGS)));
        registry.put(EquipmentSlot.FEET, register.register(registryName + "_boots", ()-> new FrozenBlazeArmorItem(EquipmentSlot.FEET)));
        return registry;
    }

    @Override
    public FullSetBonus getFullSetBonus() {
        return FULL_SET_BONUS;
    }

    @Override
    public PieceBonus getPieceBonusForSlot(EquipmentSlot slot) {
        return null;
    }

    @Nullable
    @Override
    public ExtraBonus getExtraBonus(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void fullSetTick(ItemStack stack, Level level, LivingEntity living) {
        List<LivingEntity> entityList = level.getEntitiesOfClass(LivingEntity.class, living.getBoundingBox().inflate(5), new Predicate<LivingEntity>() {
            @Override
            public boolean test(LivingEntity living) {
                return true;
            }
        });
        for (LivingEntity livingEntity : entityList) {
            if (livingEntity != living) {
                livingEntity.hurt(new FrozenDamageSource(living), 2);
            }
        }
    }

    @Override
    protected void initFullSetTick(ItemStack stack, Level level, LivingEntity living) {
    }

    @Override
    protected void postFullSetTick(ItemStack stack, Level level, LivingEntity living) {

    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private FrozenBlazeRenderer renderer;
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new FrozenBlazeRenderer();
                }
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeMods(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
