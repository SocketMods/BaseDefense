package sciwhiz12.basedefense.datagen;

import static sciwhiz12.basedefense.Reference.MODID;
import static sciwhiz12.basedefense.util.Util.appendPath;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import sciwhiz12.basedefense.Reference.Blocks;
import sciwhiz12.basedefense.block.LockedDoorBlock;
import sciwhiz12.basedefense.block.PadlockedDoorBlock;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        standardCubeAll(Blocks.TEST_LOCK_BLOCK);
        standardCubeAll(Blocks.KEYSMITH_TABLE);
        standardCubeAll(Blocks.LOCKSMITH_TABLE);

        padlockedDoor(Blocks.PADLOCKED_OAK_DOOR);
        padlockedDoor(Blocks.PADLOCKED_BIRCH_DOOR);
        padlockedDoor(Blocks.PADLOCKED_SPRUCE_DOOR);
        padlockedDoor(Blocks.PADLOCKED_JUNGLE_DOOR);
        padlockedDoor(Blocks.PADLOCKED_ACACIA_DOOR);
        padlockedDoor(Blocks.PADLOCKED_DARK_OAK_DOOR);
        padlockedDoor(Blocks.PADLOCKED_IRON_DOOR);

        lockedDoor(Blocks.LOCKED_OAK_DOOR);
        lockedDoor(Blocks.LOCKED_BIRCH_DOOR);
        lockedDoor(Blocks.LOCKED_SPRUCE_DOOR);
        lockedDoor(Blocks.LOCKED_JUNGLE_DOOR);
        lockedDoor(Blocks.LOCKED_ACACIA_DOOR);
        lockedDoor(Blocks.LOCKED_DARK_OAK_DOOR);
        lockedDoor(Blocks.LOCKED_IRON_DOOR);
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
        String base = block.baseBlock.getRegistryName().getPath();
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
        String parent = ModelProvider.BLOCK_FOLDER + "/door/" + block.getRegistryName().getPath() + locAppend;
        ResourceLocation blockTex = blockTexture(block.baseBlock);

        BlockModelBuilder model = models().withExistingParent(parent, modLoc("locked_door" + locAppend));
        model.texture("top", appendPath(blockTex, "_top"));
        model.texture("bottom", appendPath(blockTex, "_bottom"));

        return model;
    }
}
