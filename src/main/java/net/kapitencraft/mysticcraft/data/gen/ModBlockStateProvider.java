package net.kapitencraft.mysticcraft.data.gen;

import net.kapitencraft.mysticcraft.MysticcraftMod;
import net.kapitencraft.mysticcraft.block.ModBlockStateProperties;
import net.kapitencraft.mysticcraft.block.gemstone.GemstoneCrystal;
import net.kapitencraft.mysticcraft.block.special.GemstoneSeedBlock;
import net.kapitencraft.mysticcraft.init.BlockRegistryHolder;
import net.kapitencraft.mysticcraft.init.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MysticcraftMod.MOD_ID, exFileHelper);
    }

    public static ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }


    private void blockWithItem(BlockRegistryHolder<?, ?> holder) {
        simpleBlockWithItem(holder.getBlock(), cubeAll(holder.getBlock()));
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.CRIMSONIUM_ORE);

        makeReforgeAnvil();

        ModelFile gemstoneBlock = models().getBuilder("block/gemstone/block")
                .texture("all", MysticcraftMod.res("block/gemstone/block"))
                .parent(coloredBlock())
                .renderType("translucent");
        getVariantBuilder(ModBlocks.GEMSTONE_BLOCK.getBlock())
                .partialState()
                        .setModels(
                                new ConfiguredModel(gemstoneBlock)
                        );
        simpleBlockItem(ModBlocks.GEMSTONE_BLOCK.getBlock(), gemstoneBlock);

        makeGemstoneCrystals();

        makeGemstoneSeeds();
        makeGemstoneSeedItem();

        getVariantBuilder(ModBlocks.FRAGILE_BASALT.get())
                .partialState()
                .setModels(
                        new ConfiguredModel(models().getExistingFile(key(Blocks.BASALT)))
                );

        ResourceLocation WEST_EAST_TEXTURE = MysticcraftMod.res("block/gemstone_grinder_west_east");
        ResourceLocation NORTH_SOUTH_TEXTURE = MysticcraftMod.res("block/gemstone_grinder_north_south");
        getVariantBuilder(ModBlocks.GEMSTONE_GRINDER.getBlock())
                .partialState()
                .setModels(
                        new ConfiguredModel(models().getBuilder("gemstone_grinder")
                                .texture("down", MysticcraftMod.res("block/gemstone_grinder_bottom"))
                                .texture("up", MysticcraftMod.res("block/gemstone_grinder_top"))
                                .texture("north", NORTH_SOUTH_TEXTURE)
                                .texture("east", WEST_EAST_TEXTURE)
                                .texture("south", NORTH_SOUTH_TEXTURE)
                                .texture("west", WEST_EAST_TEXTURE)
                                .texture("particle", NORTH_SOUTH_TEXTURE)
                        )
                );

        getVariantBuilder(ModBlocks.SOUL_CHAIN.getBlock()).forAllStates(state -> {
            Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
            return ConfiguredModel.builder().modelFile(createSoulChainModel())
                    .rotationY(axis == Direction.Axis.X ? 90 : 0)
                    .rotationX(axis == Direction.Axis.Y ? 0 : 90)
                    .build();
        });
        simpleBlockItem(ModBlocks.SOUL_CHAIN.getBlock(), createSoulChainModel());

        ResourceLocation GOLD_BLOCK = new ResourceLocation("block/gold_block");

        slabBlock(ModBlocks.GOLDEN_SLAB.getBlock(), GOLD_BLOCK, GOLD_BLOCK);
        stairsBlock(ModBlocks.GOLDEN_STAIRS.getBlock(), GOLD_BLOCK);
        wallBlock(ModBlocks.GOLDEN_WALL.getBlock(), GOLD_BLOCK);

        ResourceLocation LAPIS_BLOCK = new ResourceLocation("block/lapis_block");
        buttonBlock(ModBlocks.LAPIS_BUTTON.getBlock(), LAPIS_BLOCK);
        simpleBlockItem(ModBlocks.LAPIS_BUTTON.getBlock(), itemModels().button("lapis_button_inventory", LAPIS_BLOCK));

        ResourceLocation OBSIDIAN = new ResourceLocation("block/obsidian");
        pressurePlateBlock(ModBlocks.OBSIDIAN_PRESSURE_PLATE.getBlock(), OBSIDIAN);
        simpleBlockItem(ModBlocks.OBSIDIAN_PRESSURE_PLATE.getBlock(), itemModels().pressurePlate("obsidian_pressure_plate", OBSIDIAN));
    }

    private ModelFile createSoulChainModel() {
        return models().withExistingParent("soul_chain", "chain")
                .texture("particle", MysticcraftMod.res("block/soul_chain"))
                .texture("all", MysticcraftMod.res("block/soul_chain"))
                .renderType("cutout");
    }

    private void makeReforgeAnvil() {
        getVariantBuilder(ModBlocks.REFORGING_ANVIL.getBlock())
                .forAllStates(state -> {
                    Direction direction = state.getValue(AnvilBlock.FACING);
                    int rot = switch (direction) {
                        case NORTH -> 180;
                        case SOUTH -> 0;
                        case WEST -> 90;
                        case EAST -> 270;
                        default -> -1;
                    };
                    return ConfiguredModel.builder()
                            .modelFile(models().getBuilder("reforge_anvil")
                                    .parent(models().getExistingFile(new ResourceLocation("template_anvil")))
                                    .texture("top", MysticcraftMod.res("block/reforging_anvil_top"))
                            )
                            .rotationY(rot)
                            .build();
                });
    }

    private void makeGemstoneCrystals() {
        getVariantBuilder(ModBlocks.GEMSTONE_CRYSTAL.getBlock()).forAllStatesExcept(state -> {
            Direction direction = state.getValue(BlockStateProperties.FACING);
            int xRot = switch (direction) {
                case DOWN -> 180;
                case UP -> 0;
                default -> 90;
            };
            int yRot = switch (direction) {
                case SOUTH -> 180;
                case WEST -> 270;
                case EAST -> 90;
                default -> 0;
            };
            return ConfiguredModel.builder()
                    .modelFile(gemstoneCrystal(state.getValue(GemstoneCrystal.SIZE)))
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        }, ModBlockStateProperties.GEMSTONE_TYPE);
    }

    private ModelFile gemstoneCrystal(GemstoneCrystal.Size size) {
        return this.models().getBuilder("block/gemstone/crystal/" + size.getSerializedName())
                .texture("cross", MysticcraftMod.res("block/gemstone/crystal/" + size.getSerializedName()))
                .parent(tintedCross())
                .renderType("cutout");
    }

    private ModelFile tintedCross() {
        return this.models().getBuilder("tinted_cross")
                .texture("particle", "#cross")
                .element()
                .from(.8f, 0, 8).to(15.2f, 16, 8)
                .rotation().origin(8, 8, 8).axis(Direction.Axis.Y).angle(45).rescale(true).end()
                .shade(false)
                .face(Direction.SOUTH).end().face(Direction.NORTH).end()
                .faces((direction, faceBuilder) -> faceBuilder.texture("#cross").tintindex(0))
                .end()
                .element()
                .from(8, 0, .8f).to(8, 16, 15.2f)
                .rotation().origin(8, 8, 8).axis(Direction.Axis.Y).angle(45).rescale(true).end()
                .shade(false)
                .face(Direction.WEST).end().face(Direction.EAST).end()
                .faces((direction, faceBuilder) -> faceBuilder.texture("#cross").tintindex(0))
                .end();
    }

    private void makeGemstoneSeedItem() {
        ItemModelBuilder builder = itemModels().withExistingParent("gemstone_seed", "block/block");
        for (GemstoneSeedBlock.MaterialType type : GemstoneSeedBlock.MaterialType.values()) {
            builder.override()
                    .predicate(modLoc("material"), (type.ordinal() + 1) * .1f)
                    .model(variantGemstoneSeed(type));
        }
    }

    private void makeGemstoneSeeds() {
        getVariantBuilder(ModBlocks.GEMSTONE_SEED.getBlock()).forAllStatesExcept(state -> {
            Direction direction = state.getValue(BlockStateProperties.FACING);
            GemstoneSeedBlock.MaterialType type = state.getValue(ModBlockStateProperties.STONE_TYPE);
            int yRot = switch (direction) {
                case DOWN:
                case EAST:
                    yield 90;
                case UP:
                case WEST:
                    yield 270;
                case SOUTH:
                    yield 180;
                case NORTH:
                    yield 0;
            };
            return ConfiguredModel.builder()
                    .modelFile(variantGemstoneSeed(type))
                    .rotationY(yRot)
                    .build();
        }, ModBlockStateProperties.GEMSTONE_TYPE);
    }

    private ModelFile variantGemstoneSeed(GemstoneSeedBlock.MaterialType type) {
        return this.models().getBuilder("block/gemstone/seed/" + type.getSerializedName())
                .texture("material", blockTexture(type.getBlock()))
                .parent(baseGemstoneSeed());
    }

    private ModelFile baseGemstoneSeed() {
        return models().getBuilder("block/gemstone/seed/base")
                .parent(models().getExistingFile(mcLoc("block")))
                .renderType("cutout")
                .texture("gemstone", MysticcraftMod.res("block/gemstone/seed"))
                .texture("particle", "#gemstone")
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .allFaces((direction, faceBuilder) -> faceBuilder.texture("#material").cullface(direction))
                .end()
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.NORTH).texture("#gemstone").cullface(Direction.NORTH).tintindex(0).end()
                .end();
    }

    private ModelFile coloredBlock() {
        return models().getBuilder("colored_block").parent(models().getExistingFile(new ResourceLocation("block")))
                .texture("particle", "#all")
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .allFaces((direction, faceBuilder) -> faceBuilder
                        .uvs(0, 0, 16, 16)
                        .texture("#all")
                        .cullface(direction)
                        .tintindex(0)
                )
                .end();
    }
}
