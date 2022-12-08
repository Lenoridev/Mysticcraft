package net.kapitencraft.mysticcraft.misc;

import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.enchantments.ExtendedCalculationEnchantment;
import net.kapitencraft.mysticcraft.enchantments.StatBoostEnchantment;
import net.kapitencraft.mysticcraft.entity.FrozenBlazeEntity;
import net.kapitencraft.mysticcraft.init.ModAttributes;
import net.kapitencraft.mysticcraft.init.ModEnchantments;
import net.kapitencraft.mysticcraft.init.ModMobEffects;
import net.kapitencraft.mysticcraft.item.DropRarity;
import net.kapitencraft.mysticcraft.item.IModItem;
import net.kapitencraft.mysticcraft.item.IRNGDrop;
import net.kapitencraft.mysticcraft.item.bow.TallinBow;
import net.kapitencraft.mysticcraft.item.gemstone.IGemstoneApplicable;
import net.kapitencraft.mysticcraft.item.sword.ManaSteelSword;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber
public class MiscRegister {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void DamageEnchantRegister(LivingDamageEvent event) {
        LivingEntity attacked = event.getEntity();
        if (event.getSource().getEntity() instanceof Arrow arrow) {
            CompoundTag tag = arrow.getPersistentData();
            if (tag.contains("SnipeEnchant", 3) && tag.getInt("SnipeEnchant") > 0) {
                Vec3 launchLoc = new Vec3(tag.getDouble("LaunchX"), tag.getDouble("LaunchY"), tag.getDouble("LaunchZ"));
                Vec3 hitLoc = arrow.position();
                double distance = launchLoc.distanceTo(hitLoc);
                event.setAmount((float) (event.getAmount() * (1 + (float) Math.floor(distance / 10) * 0.05 * tag.getInt("SnipeEnchant"))));
            }
            if (arrow.getOwner() instanceof Player player) {
                double Strength = player.getAttributeValue(ModAttributes.STRENGTH.get());
                event.setAmount((float) (event.getAmount() * (1 + Strength / 100)));
            }
            return;
        }
        @Nullable LivingEntity attacker = MISCTools.getAttacker(event.getSource());
        if (attacker == null) { return; }
        ItemStack stack = attacker.getMainHandItem();
        Map<Enchantment, Integer> enchantments = stack.getAllEnchantments();
        if (attacker instanceof Player player) {
            double Strength = player.getAttributeValue(ModAttributes.STRENGTH.get());
            event.setAmount((float) (event.getAmount() * (1 + Strength / 100)));
        }
        if (enchantments != null) {
            for (Enchantment enchantment : enchantments.keySet()) {
                if (enchantment instanceof ExtendedCalculationEnchantment modEnchantment) {
                    event.setAmount((float) modEnchantment.execute(enchantments.get(enchantment), stack, attacker, attacked, event.getAmount()));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void StatModEnchantmentRegister(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        EquipmentSlot slot = event.getSlotType();
        Map<Enchantment, Integer> enchantments = stack.getAllEnchantments();
        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment instanceof StatBoostEnchantment statBoostEnchantment && statBoostEnchantment.hasModifiersForThatSlot(slot) && !(stack.getItem() instanceof IModItem)) {
                Multimap<Attribute, AttributeModifier> modifiableMap = event.getModifiers();
                for (Attribute attribute : modifiableMap.keys()) {
                    for (AttributeModifier modifier : modifiableMap.get(attribute)) {
                        Multimap<Attribute, AttributeModifier> modifiersMultimap = statBoostEnchantment.getModifiers(enchantments.get(enchantment), stack, slot);
                        for (Attribute attribute1 : modifiersMultimap.keys()) {
                            if (attribute == attribute1) {
                                for (AttributeModifier modifier1 : modifiersMultimap.get(attribute1)) {
                                    if (modifier.getOperation() == modifier1.getOperation()) {
                                        double amount = modifier.getAmount();
                                        event.removeModifier(attribute, modifier);
                                        event.addModifier(attribute, new AttributeModifier(modifier.getId(), modifier.getName(), amount + modifier1.getAmount(), modifier.getOperation()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void HitEffectRegister(LivingDamageEvent event) {
        LivingEntity living = event.getEntity();
        if (living.hasEffect(ModMobEffects.VULNERABILITY.get())) {
            event.setAmount((float) (event.getAmount() * (1 + Objects.requireNonNull(living.getEffect(ModMobEffects.VULNERABILITY.get())).getAmplifier() * 0.05)));
        }
        if (event.getSource().getDirectEntity() instanceof SmallFireball smallFireball) {
            if (smallFireball.getOwner() instanceof FrozenBlazeEntity) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 5));
            }
        }

    }

    @SubscribeEvent
    public static void CritDamageRegister(CriticalHitEvent event) {
        Player attacker = event.getEntity();
        event.setDamageModifier((float) (1 + attacker.getAttributeValue(ModAttributes.CRIT_DAMAGE.get()) / 100));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void FerocityRegister(LivingDamageEvent event) {
        LivingEntity attacked = event.getEntity();
        LivingEntity attacker = MISCTools.getAttacker(event.getSource());
        if (attacker == null) {
            return;
        }
        if (attacker.getAttribute(ModAttributes.FEROCITY.get()) != null) {
            final double ferocity = event.getSource() instanceof FerociousDamageSource damageSource ? damageSource.ferocity : attacker.getAttributeValue(ModAttributes.FEROCITY.get());
            if (ferocity >= (Math.random() * 100)) {
                new Object() {
                    private int ticks = 0;
                    private float waitTicks;

                    public void start(int waitTicks) {
                        this.waitTicks = waitTicks;
                        MinecraftForge.EVENT_BUS.register(this);
                    }
                    @SubscribeEvent
                    public void tick(TickEvent.ServerTickEvent event) {
                        if (event.phase == TickEvent.Phase.END) {
                            this.ticks += 1;
                            if (this.ticks >= this.waitTicks)
                                run();
                        }
                    }
                    private void run() {
                        MinecraftForge.EVENT_BUS.unregister(this);
                        float ferocity_damage = (float) (event.getSource() instanceof FerociousDamageSource ferociousDamageSource ? ferociousDamageSource.damage : event.getSource().getEntity() instanceof AbstractArrow arrow ? arrow.getBaseDamage() : event.getSource().getEntity() instanceof LivingEntity living ? living.getAttributeValue(Attributes.ATTACK_DAMAGE) : 0.0f);
                        attacked.hurt(new FerociousDamageSource("ferocity", attacker, (ferocity - 100), ferocity_damage), ferocity_damage);
                    }
                }.start(40);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void MiscDamageEvents(LivingDamageEvent event) {
        LivingEntity attacker = MISCTools.getAttacker(event.getSource());
        if (attacker == null) {
            return;
        }
        ItemStack mainHand = attacker.getMainHandItem();
        if (mainHand.getItem() instanceof ManaSteelSword) {
            attacker.heal(2f);
        }
    }

    @SubscribeEvent
    public static void damageIndicatorExecuting(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof ArmorStand armorStand) {
            CompoundTag persistentData = armorStand.getPersistentData();
            if (persistentData.contains("isDamageIndicator") && persistentData.getBoolean("isDamageIndicator")) {
                persistentData.putInt("time", (persistentData.getInt("time") + 1));
                @Nullable Entity ignoredTarget = armorStand.level instanceof ServerLevel serverLevel && persistentData.contains("targetUUID") ? serverLevel.getEntity(persistentData.getUUID("targetUUID")) : null;
                if (persistentData.getInt("time") >= 20) {
                    armorStand.kill();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void EntityDamaged(LivingDamageEvent event) {
        LivingEntity attacked = event.getEntity();
        MISCTools.createDamageIndicator(attacked, event.getAmount(), sourceToString(event.getSource()));
    }
    private static String sourceToString(DamageSource source) {
        if (source == DamageSource.DROWN) { return "drown"; }
        else if (source instanceof FerociousDamageSource) { return "ferocity"; }
        else if (source == DamageSource.WITHER) { return "wither"; }
        return "damage_generic";
    }
    @SubscribeEvent
    public static void healthRegenRegister(LivingHealEvent event) {
        if (event.getEntity().getAttribute(ModAttributes.HEALTH_REGEN.get()) != null) {
            double health_regen = event.getEntity().getAttributeValue(ModAttributes.HEALTH_REGEN.get());
            event.setAmount(event.getAmount() * (float) health_regen / 100);
        }
        MISCTools.createDamageIndicator(event.getEntity(), event.getAmount(), "heal");

    }

    @SubscribeEvent
    public static void ArrowRegisterEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            if (arrow.getOwner() instanceof LivingEntity living) {
                ItemStack mainHand = living.getMainHandItem();
                CompoundTag arrowTag = arrow.getPersistentData();
                if (mainHand.getItem() instanceof TallinBow) {
                    arrowTag.putBoolean("CanHitEnderman", true);
                }
                arrowTag.putInt("SnipeEnchant", mainHand.getEnchantmentLevel(ModEnchantments.SNIPE.get()));
                if (arrowTag.getInt("SnipeEnchant") > 0) {
                    arrowTag.putDouble("LaunchX", arrow.getX());
                    arrowTag.putDouble("LaunchY", arrow.getY());
                    arrowTag.putDouble("LaunchZ", arrow.getZ());
                }
                arrowTag.putInt("AimEnchant", mainHand.getEnchantmentLevel(ModEnchantments.AIM.get()));
            }
        }
    }

    @SubscribeEvent
    public static void gemstoneAttributeModifications(ItemAttributeModifierEvent event) {
        ItemStack stack =event.getItemStack();
        Item item = stack.getItem();
        if (item instanceof IGemstoneApplicable applicable) {
            if (item instanceof ArmorItem armorItem) {
                if (event.getSlotType() == armorItem.getSlot() && applicable.getAttributesModified() != null) {
                    for (Attribute attribute : applicable.getAttributesModified()) {
                        event.addModifier(attribute, new AttributeModifier(MysticcraftMod.ITEM_ATTRIBUTE_MODIFIER_ADD_FOR_SLOT[MISCTools.createCustomIndex(armorItem.getSlot())], "Gemstone Modification", applicable.getAttributeModifiers(stack).get(attribute), AttributeModifier.Operation.ADDITION));
                    }
                }
            } else if (item instanceof TieredItem && event.getSlotType() == EquipmentSlot.MAINHAND) {
                if (applicable.getAttributesModified() != null) {
                    for (Attribute attribute : applicable.getAttributesModified()) {
                        event.addModifier(attribute, new AttributeModifier(MysticcraftMod.ITEM_ATTRIBUTE_MODIFIER_ADD_FOR_SLOT[MISCTools.createCustomIndex(EquipmentSlot.MAINHAND)], "Gemstone Modification", applicable.getAttributeModifiers(stack).get(attribute), AttributeModifier.Operation.ADDITION));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void rareDropMessage(LivingDropsEvent event) {
        Collection<ItemEntity> entities = event.getDrops();
        if (event.getSource().getEntity() instanceof Player attacker) {
            for (ItemEntity entity : entities) {
                ItemStack stack = entity.getItem();
                Item item = stack.getItem();
                DropRarity dropRarity = item instanceof IRNGDrop drop ? drop.getRarity() : (item instanceof BlockItem block && block.getBlock() instanceof WitherSkullBlock) ? DropRarity.LEGENDARY : DropRarity.COMMON;
                if (dropRarity.getID() > 1) {
                    attacker.sendSystemMessage(Component.literal(dropRarity.getColor().UNICODE + dropRarity.getDisplayName() + "DROP!" + FormattingCodes.RESET + "(").append(item.getName(entity.getItem())).append(") (" + FormattingCodes.BLUE.UNICODE + "+" + attacker.getAttributeValue(ModAttributes.MAGIC_FIND.get()) + "% Magic Find)"));
                }
                //Telekinesis Register
                if (attacker.getMainHandItem().getEnchantmentLevel(ModEnchantments.TELEKINESIS.get()) > 0) {
                    attacker.getInventory().add(stack);
                    entity.kill();
                }
            }
        }
    }


    @SubscribeEvent
    public static void telekinesisRegister(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getMainHandItem();
        BlockState state = event.getState();
        Block block = state.getBlock();
        Level level = player.level;
        BlockPos pos = event.getPos();
        ServerLevel serverLevel = level instanceof ServerLevel serverLevel1 ? serverLevel1 : null;
        LootContext.Builder context = (new LootContext.Builder(serverLevel)).withRandom(serverLevel.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, mainHandItem).withOptionalParameter(LootContextParams.THIS_ENTITY, player);
        boolean hasTelekinesis = mainHandItem.getEnchantmentLevel(ModEnchantments.TELEKINESIS.get()) > 0;
        boolean hasReplenish = mainHandItem.getEnchantmentLevel(ModEnchantments.REPLENISH.get()) > 0;
        List<ItemStack> drops = state.getDrops(context);
        if (hasReplenish) {
            MISCTools.shrinkDrops(drops, Items.WHEAT_SEEDS, 1);
        }
        if (serverLevel != null && hasTelekinesis) {
            for (ItemStack stack : drops) {
                player.getInventory().add(stack);
            }
            event.setCanceled(true);
            serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        if (hasReplenish && block instanceof CropBlock cropBlock) {
            state.setValue(cropBlock.getAgeProperty(), 0);
                if (!hasTelekinesis) {
                for (ItemStack stack : drops) {
                    level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack));
                }
            }
            BlockState newState = cropBlock.getStateForAge(0);
            event.setCanceled(true);
            level.setBlock(pos, newState, 3);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void manaRegister(RenderGuiOverlayEvent.Pre event) {
        int w = event.getWindow().getGuiScaledWidth();
        int h = event.getWindow().getGuiScaledHeight();
        int posX = w / 2;
        int posY = h / 2;
        Player entity = Minecraft.getInstance().player;
        if (entity != null) {
            String builder = FormattingCodes.BLUE.UNICODE + MISCTools.round(entity.getAttributeValue(ModAttributes.MANA.get()), 1) + FormattingCodes.RESET + " / " + FormattingCodes.DARK_BLUE.UNICODE + entity.getAttributeValue(ModAttributes.MAX_MANA.get());
            Minecraft.getInstance().font.draw(event.getPoseStack(), builder , posX - 203, posY + 93, -1);
        }
    }
}
