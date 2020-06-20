package sciwhiz12.basedefense.datagen;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;
import sciwhiz12.basedefense.init.ModBlocks;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, BaseDefense.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        standardCubeAll(ModBlocks.TEST_LOCK_BLOCK);
        standardCubeAll(ModBlocks.KEYSMITH_TABLE);
        standardCubeAll(ModBlocks.LOCKSMITH_TABLE);

        final PadlockedDoorBlock[] padlockedDoors = { ModBlocks.PADLOCKED_OAK_DOOR, ModBlocks.PADLOCKED_BIRCH_DOOR,
                ModBlocks.PADLOCKED_SPRUCE_DOOR, ModBlocks.PADLOCKED_JUNGLE_DOOR, ModBlocks.PADLOCKED_ACACIA_DOOR,
                ModBlocks.PADLOCKED_DARK_OAK_DOOR, ModBlocks.PADLOCKED_IRON_DOOR };
        for (PadlockedDoorBlock b : padlockedDoors) { padlockedDoor(b); }

        final LockedDoorBlock[] lockedDoors = { ModBlocks.LOCKED_OAK_DOOR, ModBlocks.LOCKED_BIRCH_DOOR,
                ModBlocks.LOCKED_SPRUCE_DOOR, ModBlocks.LOCKED_JUNGLE_DOOR, ModBlocks.LOCKED_ACACIA_DOOR,
                ModBlocks.LOCKED_DARK_OAK_DOOR, ModBlocks.LOCKED_IRON_DOOR };
        for (LockedDoorBlock b : lockedDoors) { lockedDoor(b); }
    }

    void lockedDoor(LockedDoorBlock block) {
        BlockModelBuilder bottomLeft = lockedDoorModel(block, "_bottom");
        BlockModelBuilder bottomRight = lockedDoorModel(block, "_bottom_hinge");
        BlockModelBuilder topLeft = lockedDoorModel(block, "_top");
        BlockModelBuilder topRight = lockedDoorModel(block, "_top_hinge");

        getVariantBuilder(block).forAllStatesExcept(state -> {
            int yRot = ((int) state.get(LockedDoorBlock.FACING).getHorizontalAngle()) + 90;
            boolean rh = state.get(LockedDoorBlock.HINGE) == DoorHingeSide.RIGHT;
            boolean open = state.get(LockedDoorBlock.OPEN);
            boolean right = rh ^ open;
            if (open) { yRot += 90; }
            if (rh && open) { yRot += 180; }
            yRot %= 360;
            return ConfiguredModel.builder().modelFile(
                state.get(LockedDoorBlock.HALF) == DoubleBlockHalf.LOWER ? (right ? bottomRight : bottomLeft)
                        : (right ? topRight : topLeft)
            ).rotationY(yRot).build();
        }, LockedDoorBlock.LOCKED);
    }

    void padlockedDoor(PadlockedDoorBlock block) {
        final String base = block.baseBlock.getRegistryName().getPath();
        ModelFile bottomLeft = models().getExistingFile(mcLoc(base + "_bottom"));
        ModelFile bottomRight = models().getExistingFile(mcLoc(base + "_bottom_hinge"));
        ModelFile topLeft = models().getExistingFile(mcLoc(base + "_top"));
        ModelFile topRight = models().getExistingFile(mcLoc(base + "_top_hinge"));

        getVariantBuilder(block).forAllStatesExcept(state -> {
            int yRot = (int) (state.get(PadlockedDoorBlock.FACING).getHorizontalAngle() + 90) % 360;
            boolean rh = state.get(PadlockedDoorBlock.HINGE) == DoorHingeSide.RIGHT;
            return ConfiguredModel.builder().modelFile(
                state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? (rh ? bottomRight : bottomLeft)
                        : (rh ? topRight : topLeft)
            ).rotationY(yRot).build();
        }, PadlockedDoorBlock.SIDE);
    }

    void standardCubeAll(Block b) {
        VariantBlockStateBuilder builder = getVariantBuilder(b);
        ModelFile model = cubeAll(b);
        builder.setModels(builder.partialState(), ConfiguredModel.builder().modelFile(model).build());
        itemModels().getBuilder(b.getRegistryName().toString()).parent(model);
    }

    BlockModelBuilder lockedDoorModel(LockedDoorBlock block, String locAppend) {
        final String parent = ModelProvider.BLOCK_FOLDER + "/door/" + block.getRegistryName().getPath() + locAppend;
        final ResourceLocation blockTex = blockTexture(block.baseBlock);

        BlockModelBuilder model = models().withExistingParent(parent, modLoc("locked_door" + locAppend));
        model.texture("top", append(blockTex, "_top"));
        model.texture("bottom", append(blockTex, "_bottom"));

        return model;
    }

    ResourceLocation append(ResourceLocation loc, String append) {
        return new ResourceLocation(loc.getNamespace(), loc.getPath() + append);
    }
}
