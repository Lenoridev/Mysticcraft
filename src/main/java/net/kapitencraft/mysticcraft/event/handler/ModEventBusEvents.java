package net.kapitencraft.mysticcraft.event.handler;

import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.event.custom.AddGemstonesToItemEvent;
import net.kapitencraft.mysticcraft.event.custom.RegisterGemstoneTypePlacementsEvent;
import net.kapitencraft.mysticcraft.init.ModEntityTypes;
import net.kapitencraft.mysticcraft.init.ModItems;
import net.kapitencraft.mysticcraft.init.custom.ModRegistryBuilders;
import net.kapitencraft.mysticcraft.item.capability.gemstone.GemstoneSlot;
import net.kapitencraft.mysticcraft.item.capability.gemstone.GemstoneType;
import net.kapitencraft.mysticcraft.item.capability.reforging.Reforges;
import net.kapitencraft.mysticcraft.item.misc.AnvilUses;
import net.kapitencraft.mysticcraft.logging.Markers;
import net.kapitencraft.mysticcraft.networking.ModMessages;
import net.kapitencraft.mysticcraft.potion.ModPotionRecipe;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.NewRegistryEvent;

import static net.kapitencraft.mysticcraft.MysticcraftMod.sendRegisterDisplay;

@Mod.EventBusSubscriber(modid = MysticcraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        sendRegisterDisplay("custom Potion Recipes");
        BrewingRecipeRegistry.addRecipe(new ModPotionRecipe());
        sendRegisterDisplay("Entity World Generation");
        registerSpawnPlacements();
        sendRegisterDisplay("Rarities");
        Reforges.registerRarities();
        sendRegisterDisplay("Reforges");
        Reforges.bootstrap();
        sendRegisterDisplay("Anvil Uses");
        AnvilUses.registerUses();
        sendRegisterDisplay("Packet Handling");
        ModMessages.register();
    }


    private static void registerSpawnPlacements() {
        SpawnPlacements.register(ModEntityTypes.FROZEN_BLAZE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules);
        SpawnPlacements.register(ModEntityTypes.VAMPIRE_BAT.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING,
                    Monster::checkMonsterSpawnRules);
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.create(ModRegistryBuilders.GLYPH_EFFECT_REGISTRY_BUILDER);
        event.create(ModRegistryBuilders.REQUESTABLE_REGISTRY_BUILDER);
        event.create(ModRegistryBuilders.REQUIREMENT_REGISTRY_BUILDER);
        event.create(ModRegistryBuilders.REFORGE_BONUSES_REGISTRY_BUILDER);
        MysticcraftMod.LOGGER.info(Markers.REGISTRY, "Registered custom registries");
    }

    @SubscribeEvent
    public static void registerGemstoneValidations(RegisterGemstoneTypePlacementsEvent event) {
        event.addValidation((biome) -> biome.is(Biomes.OCEAN), GemstoneType.AQUAMARINE, GemstoneType.TURQUOISE);
        event.addValidation((biome) -> biome.is(Biomes.FOREST), GemstoneType.CELESTINE);
        event.addValidation((biome) -> biome.is(Tags.Biomes.IS_MOUNTAIN), GemstoneType.AMETHYST, GemstoneType.SAPPHIRE);
        event.addValidation((biome) -> biome.is(Biomes.END_HIGHLANDS), GemstoneType.ALMANDINE);
        event.addValidation((biome) -> biome.is(Biomes.BASALT_DELTAS), GemstoneType.JASPER);
        event.addValidation((biome) -> biome.is(BiomeTags.IS_OVERWORLD), GemstoneType.RUBY);
    }

    @SubscribeEvent
    public static void registerGemstoneItems(AddGemstonesToItemEvent event) {
        event.registerArmor(ModItems.ENDER_KNIGHT_ARMOR, new GemstoneSlot.Builder(GemstoneSlot.Type.DEFENCE, GemstoneSlot.Type.OFFENCE, GemstoneSlot.Type.COMBAT, GemstoneSlot.Type.COMBAT, GemstoneSlot.Type.STRENGTH));
        event.register(ModItems.VALKYRIE, new GemstoneSlot.Builder(GemstoneSlot.Type.COMBAT, GemstoneSlot.Type.STRENGTH));
        event.register(ModItems.HYPERION, new GemstoneSlot.Builder(GemstoneSlot.Type.COMBAT, GemstoneSlot.Type.INTELLIGENCE));
        event.register(ModItems.SCYLLA, new GemstoneSlot.Builder(GemstoneSlot.Type.COMBAT, GemstoneSlot.Type.COMBAT));
        event.register(ModItems.ASTREA, new GemstoneSlot.Builder(GemstoneSlot.Type.COMBAT, GemstoneSlot.Type.DEFENCE));
        event.register(ModItems.NECRON_SWORD, new GemstoneSlot.Builder(GemstoneSlot.Type.COMBAT));
        event.register(ModItems.LONGBOW, new GemstoneSlot.Builder(GemstoneSlot.Type.OFFENCE, GemstoneSlot.Type.DRAW_SPEED));
        event.register(ModItems.MANA_STEEL_SWORD, new GemstoneSlot.Builder(GemstoneSlot.Type.COMBAT, GemstoneSlot.Type.COMBAT));
    }

}
