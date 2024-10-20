package net.kapitencraft.mysticcraft.mixin.classes.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.kapitencraft.mysticcraft.enchantments.abstracts.IUltimateEnchantment;
import net.kapitencraft.mysticcraft.enchantments.extras.EnchantmentDescriptionManager;
import net.kapitencraft.mysticcraft.gui.IGuiHelper;
import net.kapitencraft.mysticcraft.helpers.*;
import net.kapitencraft.mysticcraft.init.ModAttributes;
import net.kapitencraft.mysticcraft.init.ModGlyphEffects;
import net.kapitencraft.mysticcraft.item.capability.CapabilityHelper;
import net.kapitencraft.mysticcraft.item.capability.ITieredItem;
import net.kapitencraft.mysticcraft.item.capability.dungeon.IStarAbleItem;
import net.kapitencraft.mysticcraft.item.capability.elytra.ElytraData;
import net.kapitencraft.mysticcraft.item.capability.gemstone.GemstoneHelper;
import net.kapitencraft.mysticcraft.item.capability.reforging.Reforge;
import net.kapitencraft.mysticcraft.item.capability.spell.ISpellItem;
import net.kapitencraft.mysticcraft.item.combat.spells.SpellItem;
import net.kapitencraft.mysticcraft.item.combat.spells.SpellScrollItem;
import net.kapitencraft.mysticcraft.item.combat.weapon.melee.sword.LongSwordItem;
import net.kapitencraft.mysticcraft.item.item_bonus.IItemBonusItem;
import net.kapitencraft.mysticcraft.item.misc.CreativeItems;
import net.kapitencraft.mysticcraft.item.misc.IModItem;
import net.kapitencraft.mysticcraft.item.misc.SoulbindHelper;
import net.kapitencraft.mysticcraft.tags.ModItemTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.util.*;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

    @Shadow public abstract String toString();

    @SuppressWarnings("all")
    @Shadow
    private static @NotNull Collection<Component> expandBlockState(String p_41762_) {
        return null;
    }

    @Shadow public abstract boolean hasCustomHoverName();

    @Shadow protected abstract int getHideFlags();

    @Shadow @Final private Item item;

    @Shadow public abstract ItemStack copy();

    @Unique
    private static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);

    @SuppressWarnings("ALL")
    private ItemStack self() {
        return (ItemStack) (Object) this;
    }

    /**
     * @reason custom attribute and enchantment description
     * @author Kapitencraft
     */
    @Overwrite
    public List<Component> getTooltipLines(@Nullable Player player, TooltipFlag tooltipFlag) {
        List<Component> list = Lists.newArrayList();
        CompoundTag tag = self().getOrCreateTag();
        MutableComponent name = Component.empty().append(self().getHoverName()).withStyle(self().getRarity().getStyleModifier());
        if (hasCustomHoverName()) {
            name.withStyle(ChatFormatting.ITALIC);
        }
        list.add(name);
        GemstoneHelper.getCapability(self(), iGemstoneHandler -> iGemstoneHandler.getDisplay(list));
        if (!tooltipFlag.isAdvanced() && !self().hasCustomHoverName() && self().is(Items.FILLED_MAP)) {
            Integer integer = MapItem.getMapId(self());
            if (integer != null) {
                list.add(Component.literal("#" + integer).withStyle(ChatFormatting.GRAY));
            }
        }

        int j = getHideFlags();
        if (shouldShowInTooltip(j, ItemStack.TooltipPart.ADDITIONAL)) {
            if (item instanceof IModItem modItem) {
                modItem.appendHoverTextWithPlayer(self(), player == null ? null : player.level, list, tooltipFlag, player);
            } else {
                item.appendHoverText(self(), player == null ? null : player.level, list, tooltipFlag);
            }
        }
        if (self().is(ModItemTags.ENDER_HITTABLE)) {
            list.add(Component.translatable("tooltip.can_hit_enderman").withStyle(ChatFormatting.RED));
            list.add(CommonComponents.EMPTY);
        }
        if (item instanceof ISpellItem spellItem) {
            spellItem.appendDisplay(list, self(), player);
        }
        if (item instanceof IItemBonusItem bonusItem) {
            bonusItem.addDisplay(list, MiscHelper.getSlotForStack(self()));
            list.add(Component.literal(""));
        }
        if (shouldShowInTooltip(j, ItemStack.TooltipPart.ENCHANTMENTS)) {
            ListTag enchantmentTags = self().getEnchantmentTags();
            if (!enchantmentTags.isEmpty() && !Screen.hasShiftDown() && !EnchantmentDescriptionManager.fromBook(item) && tooltipFlag.isAdvanced()) list.add(Component.translatable("mysticcraft.ench_desc.shift").withStyle(ChatFormatting.DARK_GRAY));
            for(int i = 0; i < enchantmentTags.size(); ++i) {
                CompoundTag compoundtag = enchantmentTags.getCompound(i);
                Optional<Enchantment> enchantmentOptional = BuiltInRegistries.ENCHANTMENT.getOptional(EnchantmentHelper.getEnchantmentId(compoundtag));
                if (enchantmentOptional.isPresent()) {
                    Enchantment enchantment = enchantmentOptional.get();
                    int level = self().getEnchantmentLevel(enchantment);
                    MutableComponent component = (MutableComponent) enchantment.getFullname(EnchantmentHelper.getEnchantmentLevel(compoundtag));
                    if (!(enchantment.isCurse())) {
                        if (enchantment instanceof IUltimateEnchantment) {
                            component.withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD);
                        } else if (level == enchantment.getMaxLevel()) {
                            component.withStyle(ChatFormatting.GOLD);
                        } else if (level > enchantment.getMaxLevel()) {
                            component.setStyle(MiscHelper.withSpecial(component.getStyle(), ModGlyphEffects.RAINBOW.get()));
                        }
                    }
                    list.add(component);
                    if (Screen.hasShiftDown()) EnchantmentDescriptionManager.addTooltipForEnchant(self(), list, enchantment, player);
                }
            }
        }
        if (tag.contains("display", 10)) {
            CompoundTag compoundtag = tag.getCompound("display");
            if (shouldShowInTooltip(j, ItemStack.TooltipPart.DYE) && compoundtag.contains("color", 99)) {
                if (tooltipFlag.isAdvanced()) {
                    list.add(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", compoundtag.getInt("color"))).withStyle(ChatFormatting.GRAY));
                } else {
                    list.add(Component.translatable("item.dyed").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                }
            }
            if (compoundtag.getTagType("Lore") == 9) {
                ListTag listtag = compoundtag.getList("Lore", 8);
                for(int i = 0; i < listtag.size(); ++i) {
                    String s = listtag.getString(i);
                    try {
                        MutableComponent mutablecomponent1 = Component.Serializer.fromJson(s);
                        if (mutablecomponent1 != null) {
                            list.add(ComponentUtils.mergeStyles(mutablecomponent1, LORE_STYLE));
                        }
                    } catch (Exception exception) {
                        compoundtag.remove("Lore");
                    }
                }
            }
        }
        CapabilityHelper.exeCapability(self(), CapabilityHelper.ELYTRA, data1 -> {
            if (Screen.hasShiftDown()) {
                ElytraData data = data1.getData();
                String id = "elytra_data." + data.getSerializedName();
                list.add(Component.translatable(id).withStyle(ChatFormatting.GREEN).append(" ").append(Component.translatable("enchantment.level." + data1.getLevel())));
                list.addAll(TextHelper.getAllMatchingFilter(i -> i == 0 ? id + ".desc" : id + ".desc" + i, component -> component.withStyle(ChatFormatting.YELLOW)));
            } else {
                list.add(Component.translatable("elytra_data.tooltip").withStyle(ChatFormatting.YELLOW));
            }
            TextHelper.addEmpty(list);
        });
        ClientHelper.addReqContent(list::add, self().getItem(), player);
        if (shouldShowInTooltip(j, ItemStack.TooltipPart.MODIFIERS)) {
            Reforge reforge = Reforge.getFromStack(self());
            for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                Multimap<Attribute, AttributeModifier> multimap = self().getAttributeModifiers(equipmentslot);
                if (!multimap.isEmpty()) {
                    list.add(CommonComponents.EMPTY);
                    list.add(Component.translatable("item.modifiers." + equipmentslot.getName()).withStyle(ChatFormatting.GRAY));
                    multimap = CollectionHelper.sortMap(multimap, null, Comparator.comparingInt(value -> value.getOperation().toValue()));
                    for(Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
                        AttributeModifier modifier = entry.getValue();
                        double d0 = modifier.getAmount();
                        boolean flag = false;
                        if (player != null) {
                            if (SpellItem.ATTACK_DAMAGE_UUID.equals(modifier.getId())) {
                                d0 += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                                d0 += EnchantmentHelper.getDamageBonus(self(), MobType.UNDEFINED);
                                flag = true;
                            } else if (modifier.getId() == SpellItem.ATTACK_SPEED_UUID) {
                                d0 += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                                flag = true;
                            } else if (modifier.getId() == LongSwordItem.ATTACK_RANGE_ID) {
                                d0 += player.getAttributeBaseValue(ForgeMod.ATTACK_RANGE.get());
                                flag = true;
                            } else if (modifier.getId() == AttributeHelper.getByName("Digger Item Speed Modifier")) {
                                d0 += player.getAttributeBaseValue(ModAttributes.MINING_SPEED.get());
                                flag = true;
                            }
                        }
                        double d1;
                        switch (modifier.getOperation()) {
                            case ADDITION -> {
                                if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                                    d1 = d0 * 10.0D;
                                } else {
                                    d1 = d0;
                                }
                            }
                            case MULTIPLY_BASE -> d1 = d0 * 100;
                            default -> d1 = d0;
                        }
                        MutableComponent toAppend = Component.empty();
                        if (flag) {
                            //Base Values
                            toAppend.append(Component.literal(" ").append(Component.translatable("attribute.modifier.equals." + modifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                        } else if (d0 > 0.0D || d0 < 0.0D) {
                            if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                                d1+=1;
                                toAppend.append(MiscHelper.buildComponent(Component.literal(String.valueOf(MathHelper.defRound(d1))), Component.literal("x "), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                            } else {
                                String id = "plus";
                                ChatFormatting formatting = ChatFormatting.BLUE;
                                if (d0 < 0.0D) {
                                    d1 *= -1;
                                    id = "take";
                                    formatting = ChatFormatting.RED;
                                }
                                toAppend.append(Component.translatable("attribute.modifier." + id + "." + modifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(formatting));
                            }
                        }
                        if (modifier.getOperation() == AttributeModifier.Operation.ADDITION || (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE && (entry.getKey() == Attributes.MOVEMENT_SPEED || entry.getKey() == ForgeMod.SWIM_SPEED.get()))) {
                            if (reforge != null && reforge.hasModifier(entry.getKey())) {
                                Double reforgeValue = reforge.applyModifiers(self().getRarity()).get(entry.getKey());
                                String reforgeStringValue = reforgeValue > 0 ? "+" + ATTRIBUTE_MODIFIER_FORMAT.format(reforgeValue) : ATTRIBUTE_MODIFIER_FORMAT.format(reforgeValue);
                                toAppend.append(Component.literal(" (" + reforgeStringValue + ")").withStyle(ChatFormatting.GREEN));
                            }
                            GemstoneHelper.getCapability(self(), iGemstoneHandler -> {
                                HashMap<Attribute, AttributeModifier> mods = iGemstoneHandler.getAttributeModifiers(equipmentslot, self());
                                if (mods.containsKey(entry.getKey())) {
                                    double value = mods.get(entry.getKey()).getAmount();
                                    double rounded = MathHelper.defRound(value);
                                    toAppend.append(Component.literal(" (+" + rounded + ")").withStyle(ChatFormatting.LIGHT_PURPLE));
                                }
                            });
                        }
                        list.add(toAppend);
                    }
                }
            }
        }
        TextHelper.addEmpty(list);
        if (self().hasTag()) {
            if (shouldShowInTooltip(j, ItemStack.TooltipPart.UNBREAKABLE)) {
                if (tag.getBoolean("Unbreakable")) list.add(TextHelper.wrapInObfuscation(Component.translatable("item.unbreakable"), true).withStyle(ChatFormatting.BLUE));
                if (tag.getBoolean(SoulbindHelper.SOULBOUND_TAG_ID)) {
                    list.add(TextHelper.wrapInObfuscation(Component.translatable("item.soulbound"), true).withStyle(ChatFormatting.BLUE));
                }
            }

            if (shouldShowInTooltip(j, ItemStack.TooltipPart.CAN_DESTROY) && tag.contains("CanDestroy", 9)) {
                ListTag listtag1 = tag.getList("CanDestroy", 8);
                if (!listtag1.isEmpty()) {
                    list.add(CommonComponents.EMPTY);
                    list.add(Component.translatable("item.canBreak").withStyle(ChatFormatting.GRAY));

                    for(int k = 0; k < listtag1.size(); ++k) {
                        list.addAll(expandBlockState(listtag1.getString(k)));
                    }
                }
            }

            if (shouldShowInTooltip(j, ItemStack.TooltipPart.CAN_PLACE) && tag.contains("CanPlaceOn", 9)) {
                ListTag listtag2 = tag.getList("CanPlaceOn", 8);
                if (!listtag2.isEmpty()) {
                    list.add(CommonComponents.EMPTY);
                    list.add(Component.translatable("item.canPlace").withStyle(ChatFormatting.GRAY));

                    for(int l = 0; l < listtag2.size(); ++l) {
                        list.addAll(expandBlockState(listtag2.getString(l)));
                    }
                }
            }
        }
        if (tooltipFlag.isAdvanced()) {
            if (self().isDamaged()) {
                list.add(Component.translatable("item.durability", self().getMaxDamage() - self().getDamageValue(), self().getMaxDamage()));
            }

            list.add(Component.literal(BuiltInRegistries.ITEM.getKey(item).toString()).withStyle(ChatFormatting.DARK_GRAY));
            if (self().hasTag()) {
                list.add(Component.translatable("item.nbt_tags", tag.getAllKeys().size()).withStyle(ChatFormatting.DARK_GRAY));
                if (Screen.hasAltDown() && self().getTag() != null) {
                    list.add(NbtUtils.toPrettyComponent(self().getOrCreateTag()));
                }
            }
        }
        if (player != null && !item.isEnabled(player.getLevel().enabledFeatures())) {
            list.add(Component.translatable("item.disabled").withStyle(ChatFormatting.RED));
        }
        if (CreativeItems.contains(item)) {
            list.add(Component.translatable("item.creative_only").withStyle(ChatFormatting.RED));
        }
        ForgeEventFactory.onItemTooltip(self(), player, list, tooltipFlag);
        if (!(item instanceof IGuiHelper)) {
            Rarity rarity = item.getRarity(self());
            boolean flag = rarity != MiscHelper.getItemRarity(item) && !(item instanceof SpellScrollItem);
            list.add(Component.literal(""));
            list.add(TextHelper.wrapInObfuscation(createNameMod(self()), flag).withStyle(rarity.getStyleModifier()).withStyle(ChatFormatting.BOLD));
        }
        TextHelper.removeUnnecessaryEmptyLines(list);
        return list;
    }

    @SuppressWarnings("all")
    private static MutableComponent createNameMod(ItemStack stack) {
        MutableComponent component = Component.empty();
        Rarity rarity = stack.getItem().getRarity(stack);

        component.append(Component.literal(rarity + " "));
        //component.append(ModRegistries.LORE_CATEGORY_REGISTRY.getValues().stream()
        //        .sorted(Comparator.comparingInt(ItemLoreCategory::getPriority))
        //        .map(CollectionHelper.biMap(stack, ItemLoreCategory::apply))
        //        .filter(Objects::nonNull)
        //        .collect(CollectorHelper.joinComponent(Component.literal(" "))));
        return component;
    }

    @SuppressWarnings("all")
    private static boolean shouldShowInTooltip(int p_41627_, ItemStack.TooltipPart p_41628_) {
        return (p_41627_ & p_41628_.getMask()) == 0;
    }

    /**
     * @reason reforge name
     * @author Kapitencraft
     */
    @Overwrite
    public Component getHoverName() {
        MutableComponent name = Component.empty();
        Component component = null;
        Item item = self().getItem();
        Reforge reforge = Reforge.getFromStack(self());
        if (reforge != null) {
            name.append(reforge.getName());
            name.append(" ");
        }

        if (item instanceof ITieredItem) {
            ITieredItem.ItemTier tier = ITieredItem.getTier(self());
            if (tier != ITieredItem.ItemTier.DEFAULT) {
                name.append(tier.getName());
                name.append(" ");
            }

        }
        CompoundTag compoundtag = self().getTagElement("display");
        if (compoundtag != null && compoundtag.contains("Name", 8)) {
            try {
                component = Component.Serializer.fromJson(compoundtag.getString("Name"));
                compoundtag.remove("Name");
            } catch (Exception exception) {
                compoundtag.remove("Name");
            }
        }
        if (component == null) {
            component = self().getItem().getName(self());
        }
        name.append(component);
        if (item instanceof IStarAbleItem && IStarAbleItem.hasStars(self())) {
            name.append(" ");
            name.append(IStarAbleItem.getStarDisplay(self()));
        }
        return name;
    }
}