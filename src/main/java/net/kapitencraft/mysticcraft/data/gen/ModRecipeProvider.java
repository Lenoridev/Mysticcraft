package net.kapitencraft.mysticcraft.data.gen;

import net.kapitencraft.mysticcraft.block.entity.crafting.serializers.UpgradeItemRecipe;
import net.kapitencraft.mysticcraft.data.recipe.ArmorRecipeBuilder;
import net.kapitencraft.mysticcraft.data.recipe.UpgradeRecipeBuilder;
import net.kapitencraft.mysticcraft.init.ModBlocks;
import net.kapitencraft.mysticcraft.init.ModItems;
import net.kapitencraft.mysticcraft.item.capability.gemstone.GemstoneType;
import net.kapitencraft.mysticcraft.item.capability.gemstone.IGemstoneItem;
import net.kapitencraft.mysticcraft.item.material.PrecursorRelicItem;
import net.kapitencraft.mysticcraft.tags.ModItemTags;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("SameParameterValue")
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ArmorRecipeBuilder.create(ModItems.CRIMSON_ARMOR).material(ModItems.CRIMSON_STEEL_INGOT).save(consumer, "mysticcraft:crimson_armor");
        ArmorRecipeBuilder.create(ModItems.FROZEN_BLAZE_ARMOR).material(StrictNBTIngredient.of(new ItemStack(ModItems.FROZEN_BLAZE_ROD.get(), 12))).save(consumer, "mysticcraft:frozen_blaze_armor");
        ArmorRecipeBuilder.create(ModItems.SOUL_MAGE_ARMOR).material(ModBlocks.SOUL_CHAIN.item()).save(consumer, "mysticcraft:soul_mage_armor");

        itemUpgrade(consumer, RecipeCategory.COMBAT, UpgradeItemRecipe.CraftType.EIGHT, ModItems.ASTREA, ModItems.NECRON_SWORD, ModItems.PRECURSOR_RELICTS.get(PrecursorRelicItem.BossType.GOLDOR));
        itemUpgrade(consumer, RecipeCategory.COMBAT, UpgradeItemRecipe.CraftType.EIGHT, ModItems.HYPERION, ModItems.NECRON_SWORD, ModItems.PRECURSOR_RELICTS.get(PrecursorRelicItem.BossType.STORM));
        itemUpgrade(consumer, RecipeCategory.COMBAT, UpgradeItemRecipe.CraftType.EIGHT, ModItems.SCYLLA, ModItems.NECRON_SWORD, ModItems.PRECURSOR_RELICTS.get(PrecursorRelicItem.BossType.MAXOR));
        itemUpgrade(consumer, RecipeCategory.COMBAT, UpgradeItemRecipe.CraftType.EIGHT, ModItems.VALKYRIE, ModItems.NECRON_SWORD, ModItems.PRECURSOR_RELICTS.get(PrecursorRelicItem.BossType.NECRON));

        itemUpgrade(consumer, RecipeCategory.COMBAT, UpgradeItemRecipe.CraftType.EIGHT, ModItems.SHADOW_DAGGER, ModItems.DARK_DAGGER, ModItems.SHADOW_CRYSTAL);

        craftHammer(consumer, Items.STONE, ModItems.STONE_HAMMER);
        craftHammer(consumer, Items.IRON_INGOT, ModItems.IRON_HAMMER);
        craftHammer(consumer, Items.DIAMOND, ModItems.DIAMOND_HAMMER);
        netheriteSmithing(consumer, ModItems.DIAMOND_HAMMER.get(), RecipeCategory.TOOLS, ModItems.NETHERITE_HAMMER.get());

        hammerCrushing(consumer, ModItemTags.TIER_1_HAMMER, ModItems.CRIMSONIUM_INGOT, ModItems.CRIMSONIUM_DUST, 1);
        hammerCrushing(consumer, ModItemTags.DEFAULT_HAMMER, ModItems.CRIMSONITE_CLUSTER, ModItems.CRIMSONITE_DUST, 2);
        hammerCrushing(consumer, ModItemTags.DEFAULT_HAMMER, ModItems.RAW_CRIMSONIUM, ModItems.RAW_CRIMSONIUM_DUST, 2);

        unlock(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CRIMSON_STEEL_DUST.get(), 4)
                        .group("crimson")
                        .requires(StrictNBTIngredient.of(new ItemStack(ModItems.CRIMSONIUM_DUST.get(), 2)))
                        .requires(StrictNBTIngredient.of(new ItemStack(ModItems.CRIMSONITE_DUST.get(), 2))),
                ModItems.CRIMSONIUM_DUST.get()
        ).save(consumer);

        oreSmelting(consumer, List.of(ModItems.RAW_CRIMSONIUM.get(), ModItems.RAW_CRIMSONIUM_DUST.get()), RecipeCategory.MISC, ModItems.CRIMSONIUM_INGOT.get(), 1.2f, 200, "crimson");
        oreBlasting(consumer, List.of(ModItems.RAW_CRIMSONIUM.get(), ModItems.RAW_CRIMSONIUM_DUST.get()), RecipeCategory.MISC, ModItems.CRIMSONIUM_INGOT.get(), 1.2f, 100, "crimson");

        oreSmelting(consumer, List.of(ModItems.CRIMSON_STEEL_DUST.get()), RecipeCategory.MISC, ModItems.CRIMSON_STEEL_INGOT.get(), 1.3f, 400, "crimson");
        oreBlasting(consumer, List.of(ModItems.CRIMSON_STEEL_DUST.get()), RecipeCategory.MISC, ModItems.CRIMSON_STEEL_INGOT.get(), 1.3f, 200, "crimson");

        makeModel(SLAB_CONSUMER, ModBlocks.GOLDEN_SLAB.getItem(), Items.GOLD_BLOCK, consumer);
        makeModel(WALL_CONSUMER, ModBlocks.GOLDEN_WALL.getItem(), Items.GOLD_BLOCK, consumer);

        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_SLAB.getItem(), Items.GOLD_BLOCK, 2);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_WALL.getItem(), Items.GOLD_BLOCK);
        stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_STAIRS.getItem(), Items.GOLD_BLOCK);

        //mana steel sword
        {
            unlock(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MS_HANDLE.get())
                            .pattern("  *")
                            .pattern("** ")
                            .pattern("+* ")
                            .define('*', ModItems.MANA_STEEL_INGOT.get())
                            .define('+', StrictNBTIngredient.of(IGemstoneItem.createData(GemstoneType.Rarity.PERFECT, GemstoneType.RUBY, ModItems.GEMSTONE))),
                    ModItems.MANA_STEEL_INGOT.get()
            ).save(consumer);
            unlock(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MS_UPPER_BlADE.get())
                            .pattern(" * ")
                            .pattern(" * ")
                            .pattern("*+*")
                            .define('*', ModItems.MANA_STEEL_INGOT.get())
                            .define('+', StrictNBTIngredient.of(IGemstoneItem.createData(GemstoneType.Rarity.PERFECT, GemstoneType.RUBY, ModItems.GEMSTONE))),
                    ModItems.MANA_STEEL_INGOT.get()
            ).save(consumer);
            unlock(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MS_DOWN_BlADE.get())
                            .pattern(" * ")
                            .pattern("*+*")
                            .pattern("*+*")
                            .define('*', ModItems.MANA_STEEL_INGOT.get())
                            .define('+', StrictNBTIngredient.of(IGemstoneItem.createData(GemstoneType.Rarity.PERFECT, GemstoneType.JASPER, ModItems.GEMSTONE))),
                    ModItems.MANA_STEEL_INGOT.get()
            ).save(consumer);
            unlock(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MANA_STEEL_SWORD.get())
                            .pattern("  +")
                            .pattern(" * ")
                            .pattern("#  ")
                            .define('+', ModItems.MS_UPPER_BlADE.get())
                            .define('*', ModItems.MS_DOWN_BlADE.get())
                            .define('#', ModItems.MS_HANDLE.get()),
                    ModItems.MANA_STEEL_INGOT.get()
            ).save(consumer);
        }

        unlock(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.NECRON_SWORD.get())
                        .pattern("*")
                        .pattern("*")
                        .pattern("+")
                        .define('*', StrictNBTIngredient.of(new ItemStack(Items.WITHER_SKELETON_SKULL, 12)))
                        .define('+', Items.STICK),
                Items.WITHER_SKELETON_SKULL
        ).save(consumer);
    }

    private static final BuilderConsumer SLAB_CONSUMER = (like, ingredient) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, like, ingredient);
    private static final BuilderConsumer WALL_CONSUMER = (like, ingredient) -> wallBuilder(RecipeCategory.BUILDING_BLOCKS, like, ingredient);

    private static void makeModel(BuilderConsumer consumer, Item result, Item material, Consumer<FinishedRecipe> finishedRecipeConsumer) {
        unlock(consumer.use(result, Ingredient.of(material)), material).save(finishedRecipeConsumer);
    }

    private static void itemUpgrade(Consumer<FinishedRecipe> consumer, RecipeCategory category, UpgradeItemRecipe.CraftType craftType, RegistryObject<? extends Item> result, RegistryObject<? extends Item> source, RegistryObject<? extends Item> material) {
        UpgradeRecipeBuilder.create(category, craftType, result.get()).source(source).material(material).save(consumer);
    }

    private interface BuilderConsumer {
        RecipeBuilder use(ItemLike like, Ingredient ingredient);
    }

    private static RecipeBuilder unlock(RecipeBuilder recipeBuilder, Item material) {
        return recipeBuilder.unlockedBy(getHasName(recipeBuilder.getResult()), has(material));
    }

    private static void createArmor(Consumer<FinishedRecipe> recipe, Map<EquipmentSlot, RegistryObject<? extends Item>> armor, RegistryObject<? extends Item> material) {
        createHelmet(recipe, material, armor.get(EquipmentSlot.HEAD));
        createChestplate(recipe, material, armor.get(EquipmentSlot.CHEST));
        createLeggings(recipe, material, armor.get(EquipmentSlot.LEGS));
        createBoots(recipe, material, armor.get(EquipmentSlot.FEET));
    }
    private static void createHelmet(Consumer<FinishedRecipe> recipe, RegistryObject<? extends Item> material, RegistryObject<? extends Item> result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result.get())
                .pattern("***")
                .pattern("* *")
                .define('*', material.get()).unlockedBy(getHasName(result.get()), has(material.get())).save(recipe);
    }
    private static void createChestplate(Consumer<FinishedRecipe> recipe, RegistryObject<? extends Item> material, RegistryObject<? extends Item> result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result.get())
                .pattern("* *")
                .pattern("***")
                .pattern("***")
                .define('*', material.get()).unlockedBy(getHasName(result.get()), has(material.get())).save(recipe);
    }
    private static void createLeggings(Consumer<FinishedRecipe> recipe, RegistryObject<? extends Item> material, RegistryObject<? extends Item> result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result.get())
                .pattern("***")
                .pattern("* *")
                .pattern("* *")
                .define('*', material.get()).unlockedBy(getHasName(result.get()), has(material.get())).save(recipe);
    }
    private static void createBoots(Consumer<FinishedRecipe> recipe, RegistryObject<? extends Item> material, RegistryObject<? extends Item> result) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result.get())
                .pattern("* *")
                .pattern("* *")
                .define('*', material.get()).unlockedBy(getHasName(result.get()), has(material.get())).save(recipe);
    }

    private static void hammerCrushing(Consumer<FinishedRecipe> consumer, TagKey<Item> hammerTier, RegistryObject<? extends Item> material, RegistryObject<? extends Item> result, int amount) {
        unlock(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), amount).requires(hammerTier).requires(material.get()), material.get()).save(consumer);
    }

    private static void craftHammer(Consumer<FinishedRecipe> consumer, Item material, RegistryObject<? extends Item> result) {
        unlock(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result.get()).pattern("***").pattern("***").pattern(" ! ").define('*', material).define('!', Items.STICK), material).save(consumer);
    }

    private static void craftBow(Consumer<FinishedRecipe> consumer, Item string, Item handle, RegistryObject<? extends Item> result) {
        unlock(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result.get()).pattern(" *#").pattern("* #").pattern(" *#").define('*', string).define('#', handle), handle).save(consumer);
    }
}
