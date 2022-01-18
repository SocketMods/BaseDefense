package tk.sciwhiz12.basedefense.datagen;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import tk.sciwhiz12.basedefense.Reference.Blocks;
import tk.sciwhiz12.basedefense.block.LockedDoorBlock;
import tk.sciwhiz12.basedefense.block.PadlockedDoorBlock;

import static tk.sciwhiz12.basedefense.Reference.MODID;
import static tk.sciwhiz12.basedefense.util.Util.appendPath;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        standardCubeAll(Blocks.KEYSMITH_TABLE);
        standardCubeAll(Blocks.LOCKSMITH_TABLE);

        padlockedDoor(Blocks.PADLOCKED_OAK_DOOR);
        padlockedDoor(Blocks.PADLOCKED_BIRCH_DOOR);
        padlockedDoor(Blocks.PADLOCKED_SPRUCE_DOOR);
        padlockedDoor(Blocks.PADLOCKED_JUNGLE_DOOR);
        padlockedDoor(Blocks.PADLOCKED_ACACIA_DOOR);
        padlockedDoor(Blocks.PADLOCKED_DARK_OAK_DOOR);
        padlockedDoor(Blocks.PADLOCKED_CRIMSON_DOOR);
        padlockedDoor(Blocks.PADLOCKED_WARPED_DOOR);
        padlockedDoor(Blocks.PADLOCKED_IRON_DOOR);

        lockedDoor(Blocks.LOCKED_OAK_DOOR);
        lockedDoor(Blocks.LOCKED_BIRCH_DOOR);
        lockedDoor(Blocks.LOCKED_SPRUCE_DOOR);
        lockedDoor(Blocks.LOCKED_JUNGLE_DOOR);
        lockedDoor(Blocks.LOCKED_ACACIA_DOOR);
        lockedDoor(Blocks.LOCKED_DARK_OAK_DOOR);
        lockedDoor(Blocks.LOCKED_CRIMSON_DOOR);
        lockedDoor(Blocks.LOCKED_WARPED_DOOR);
        lockedDoor(Blocks.LOCKED_IRON_DOOR);

        portableSafe();
    }

    void lockedDoor(LockedDoorBlock block) {
        BlockModelBuilder bottomLeft = lockedDoorModel(block, "_bottom");
        BlockModelBuilder bottomRight = lockedDoorModel(block, "_bottom_hinge");
        BlockModelBuilder topLeft = lockedDoorModel(block, "_top");
        BlockModelBuilder topRight = lockedDoorModel(block, "_top_hinge");

        getVariantBuilder(block).forAllStatesExcept(state -> {
            int yRot = ((int) state.getValue(LockedDoorBlock.FACING).toYRot()) + 90;
            boolean rh = state.getValue(LockedDoorBlock.HINGE) == DoorHingeSide.RIGHT;
            boolean open = state.getValue(LockedDoorBlock.OPEN);
            boolean right = rh ^ open;
            if (open) { yRot += 90; }
            if (rh && open) { yRot += 180; }
            yRot %= 360;
            return ConfiguredModel.builder().modelFile(state.getValue(LockedDoorBlock.HALF) == DoubleBlockHalf.LOWER ?
                    (right ? bottomRight : bottomLeft) :
                    (right ? topRight : topLeft)).rotationY(yRot).build();
        }, LockedDoorBlock.LOCKED);
    }

    void padlockedDoor(PadlockedDoorBlock block) {
        String base = block.baseBlock.getRegistryName().getPath();
        ModelFile bottomLeft = models().getExistingFile(mcLoc(base + "_bottom"));
        ModelFile bottomRight = models().getExistingFile(mcLoc(base + "_bottom_hinge"));
        ModelFile topLeft = models().getExistingFile(mcLoc(base + "_top"));
        ModelFile topRight = models().getExistingFile(mcLoc(base + "_top_hinge"));

        getVariantBuilder(block).forAllStatesExcept(state -> {
            int yRot = (int) (state.getValue(PadlockedDoorBlock.FACING).toYRot() + 90) % 360;
            boolean rh = state.getValue(PadlockedDoorBlock.HINGE) == DoorHingeSide.RIGHT;
            return ConfiguredModel.builder().modelFile(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ?
                    (rh ? bottomRight : bottomLeft) :
                    (rh ? topRight : topLeft)).rotationY(yRot).build();
        }, PadlockedDoorBlock.SIDE);
    }

    void portableSafe() {
        BlockModelBuilder particle = models().getBuilder("portable_safe").texture("particle", mcLoc("block/cauldron_inner"));
        VariantBlockStateBuilder builder = getVariantBuilder(Blocks.PORTABLE_SAFE);
        ConfiguredModel.Builder<?> model = ConfiguredModel.builder().modelFile(particle);
        builder.setModels(builder.partialState(), model.buildLast());
    }

    void standardCubeAll(Block b) {
        VariantBlockStateBuilder builder = getVariantBuilder(b);
        ModelFile model = cubeAll(b);
        builder.setModels(builder.partialState(), ConfiguredModel.builder().modelFile(model).build());
        itemModels().getBuilder(b.getRegistryName().toString()).parent(model);
    }

    BlockModelBuilder lockedDoorModel(LockedDoorBlock block, String locAppend) {
        String parent = ModelProvider.BLOCK_FOLDER + "/door/" + block.getRegistryName().getPath() + locAppend;
        ResourceLocation blockTex = blockTexture(block.baseBlock);

        BlockModelBuilder model = models().withExistingParent(parent, modLoc("locked_door" + locAppend));
        model.texture("top", appendPath(blockTex, "_top"));
        model.texture("bottom", appendPath(blockTex, "_bottom"));

        return model;
    }
}
