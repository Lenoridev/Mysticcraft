package net.kapitencraft.mysticcraft.item.combat.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.helpers.AttributeHelper;
import net.kapitencraft.mysticcraft.init.ModAttributes;
import net.kapitencraft.mysticcraft.init.ModParticleTypes;
import net.kapitencraft.mysticcraft.item.ITieredItem;
import net.kapitencraft.mysticcraft.item.combat.armor.client.NetherArmorItem;
import net.kapitencraft.mysticcraft.item.combat.armor.client.model.CrimsonArmorModel;
import net.kapitencraft.mysticcraft.item.combat.armor.client.renderer.ArmorRenderer;
import net.kapitencraft.mysticcraft.item.data.dungeon.IPrestigeAbleItem;
import net.kapitencraft.mysticcraft.item.data.dungeon.IStarAbleItem;
import net.kapitencraft.mysticcraft.item.item_bonus.ExtraBonus;
import net.kapitencraft.mysticcraft.item.item_bonus.FullSetBonus;
import net.kapitencraft.mysticcraft.item.item_bonus.IArmorBonusItem;
import net.kapitencraft.mysticcraft.item.item_bonus.PieceBonus;
import net.kapitencraft.mysticcraft.item.item_bonus.fullset.CrimsonArmorFullSetBonus;
import net.kapitencraft.mysticcraft.item.misc.creative_tab.ArmorTabGroup;
import net.kapitencraft.mysticcraft.item.misc.creative_tab.TabGroup;
import net.kapitencraft.mysticcraft.item.misc.creative_tab.TabRegister;
import net.kapitencraft.mysticcraft.misc.content.EssenceType;
import net.kapitencraft.mysticcraft.misc.particle_help.ParticleAnimator;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class CrimsonArmorItem extends NetherArmorItem implements IArmorBonusItem {
    public static final ArmorTabGroup CRIMSON_ARMOR_GROUP = new ArmorTabGroup(TabRegister.TabTypes.WEAPONS_AND_TOOLS);
    private static final String helperString = "crimsonParticles";
    private static final FullSetBonus FULL_SET_BONUS = new CrimsonArmorFullSetBonus();
    public CrimsonArmorItem(EquipmentSlot p_40387_) {
        super(ModArmorMaterials.CRIMSON, p_40387_, NETHER_ARMOR_PROPERTIES);
    }

    @Override
    protected void initFullSetTick(ItemStack stack, Level level, LivingEntity living) {
        new ParticleAnimator(helperString, living, ParticleAnimator.Type.ORBIT, ParticleAnimator.createOrbitProperties(0, 1000, 0, 0, 3, (SimpleParticleType) ModParticleTypes.RED_FLAME.get(), 0.75f));
        new ParticleAnimator(helperString, living, ParticleAnimator.Type.ORBIT, ParticleAnimator.createOrbitProperties(0, 1000, 180, 0, 3, (SimpleParticleType) ModParticleTypes.RED_FLAME.get(), 0.75f));

    }

    @Override
    protected void postFullSetTick(ItemStack stack, Level level, LivingEntity living) {
        ParticleAnimator.clearAllHelpers(helperString, living);
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
    public boolean withCustomModel() {
        return true;
    }

    @Override
    protected ArmorRenderer<?> getRenderer(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
        return new ArmorRenderer<>(CrimsonArmorModel::createBodyLayer, CrimsonArmorModel::new);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return makeCustomTextureLocation("crimson_armor");
    }
    @Override
    public TabGroup getGroup() {
        return CRIMSON_ARMOR_GROUP;
    }

    @Override
    public List<ItemStack> getMatCost(ItemStack stack) {
        int stars = IStarAbleItem.getStars(stack) + 5;
        int prestige = ITieredItem.getTier(stack).getNumber();
        return List.of(
                IPrestigeAbleItem.essence(EssenceType.CRIMSON, (int) Math.pow(stars, prestige))
        );
    }

    @Override
    public List<ItemStack> getStarCost(ItemStack stack, int curStars) {
        return null;
    }

    @Override
    public Consumer<Multimap<Attribute, AttributeModifier>> getModifiersForSlot(ItemStack stack, ItemTier tier) {
        return multimap -> {
            multimap.put(ModAttributes.STRENGTH.get(), AttributeHelper.createModifierForSlot("Crimson Armor", AttributeModifier.Operation.ADDITION,
                    3 * this.getMaterial().getDefenseForSlot(this.getSlot()) * tier.getValueMul() , slot));
            multimap.put(ModAttributes.CRIT_DAMAGE.get(), AttributeHelper.createModifierForSlot("Crimson Armor", AttributeModifier.Operation.ADDITION,
                    this.getMaterial().getDefenseForSlot(this.getSlot()) * tier.getValueMul(), slot));
            multimap.put(Attributes.MAX_HEALTH, AttributeHelper.createModifierForSlot("Crimson Armor", AttributeModifier.Operation.ADDITION,
                    this.getMaterial().getDefenseForSlot(this.getSlot()) * 0.4 * tier.getValueMul(), slot));
        };
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeMods(EquipmentSlot slot) {
        HashMultimap<Attribute, AttributeModifier> builder = HashMultimap.create();
        if (slot == this.slot) {
        }
        return builder;
    }

}