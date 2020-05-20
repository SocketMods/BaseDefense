package sciwhiz12.basedefense.datagen;

import java.util.function.BiFunction;

import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import sciwhiz12.basedefense.BaseDefense;
import sciwhiz12.basedefense.block.LockableDoorBlock;
import sciwhiz12.basedefense.init.ModBlocks;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, BaseDefense.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // BlockModelBuilder door_bottom =
        // models().getBuilder("block/door/door_bottom");
        // setupDoor(door_bottom, "#bottom", Direction.DOWN, false);
        // BlockModelBuilder door_top = models().getBuilder("block/door/door_top");
        // setupDoor(door_top, "#top", Direction.UP, false);
        // BlockModelBuilder door_bottom_rh =
        // models().getBuilder("block/door/door_bottom_rh");
        // setupDoor(door_bottom_rh, "#bottom", Direction.DOWN, true);
        // BlockModelBuilder door_top_rh =
        // models().getBuilder("block/door/door_top_rh");
        // setupDoor(door_top_rh, "#top", Direction.UP, true);
        ModelFile door_bottom = new ModelFile.UncheckedModelFile(
                modLoc("block/door/lockable_door_bottom")
        );
        ModelFile door_bottom_rh = new ModelFile.UncheckedModelFile(
                modLoc("block/door/lockable_door_bottom_rh")
        );
        ModelFile door_top = new ModelFile.UncheckedModelFile(
                modLoc("block/door/lockable_door_top")
        );
        ModelFile door_top_rh = new ModelFile.UncheckedModelFile(
                modLoc("block/door/lockable_door_top_rh")
        );

        VariantBlockStateBuilder bld = getVariantBuilder(ModBlocks.LOCK_DOOR_BLOCK.get());

        registerParts(bld, DoubleBlockHalf.UPPER, createTriFunc(door_top_rh, door_top));
        registerParts(bld, DoubleBlockHalf.LOWER, createTriFunc(door_bottom_rh, door_bottom));
    }

    private static BiFunction<DoorHingeSide, Boolean, ModelFile> createTriFunc(ModelFile xorTrue,
            ModelFile xorFalse) {
        return (hinge, open) -> {
            boolean a = (hinge == DoorHingeSide.RIGHT) ? true : false;
            boolean res = a ^ open.booleanValue();
            return res ? xorTrue : xorFalse;
        };
    }

    private void registerParts(VariantBlockStateBuilder bld, DoubleBlockHalf half,
            BiFunction<DoorHingeSide, Boolean, ModelFile> consumer) {
        for (Direction dir : Direction.values()) {
            if (dir.getHorizontalIndex() == -1) continue;
            for (DoorHingeSide hinge : DoorHingeSide.values()) {
                registerPart(bld, consumer.apply(hinge, true), half, hinge, dir, true);
                registerPart(bld, consumer.apply(hinge, false), half, hinge, dir, false);
            }
        }
    }

    private void registerPart(VariantBlockStateBuilder bld, ModelFile modelFile,
            DoubleBlockHalf half, DoorHingeSide hinge, Direction dir, boolean open) {
        PartialBlockstate state = bld.partialState().with(LockableDoorBlock.FACING, dir).with(
                LockableDoorBlock.HALF, half
        ).with(LockableDoorBlock.HINGE, hinge).with(LockableDoorBlock.OPEN, open);
        ConfiguredModel model = ConfiguredModel.builder().modelFile(modelFile).rotationY(
                getRotationY(dir, open, hinge)
        ).buildLast();
        bld.setModels(state, model);
    }

    private int getRotationY(Direction dir, boolean open, DoorHingeSide hinge) {
        int add = 0;
        if (open) {
            if (hinge == DoorHingeSide.LEFT) add = 90;
            if (hinge == DoorHingeSide.RIGHT) add = -90;
        }
        int ret = ((dir.getHorizontalIndex() + 1) * 90) + add;

        if (ret >= 360) { ret -= 360; }
        if (ret < 0) { ret += 360; }

        return ret;
    }

    @SuppressWarnings("unused")
    private void setupDoor(BlockModelBuilder builder, String texture, Direction dir,
            boolean rightHinge) {
        builder.ao(false).texture("particle", texture);
        ModelBuilder<?>.ElementBuilder el = builder.element().from(0f, 0f, 0f).to(3f, 16f, 16f);
        el.face(dir).uvs(13f, 0f, 16, 16f).texture("#bottom").cullface(dir);
        setupAndEndFaces(el, texture, rightHinge);
    }

    private void setupAndEndFaces(ModelBuilder<?>.ElementBuilder el, String texture,
            boolean rightHinge) {
        float d = rightHinge ? 16F : 0F;
        el.face(Direction.SOUTH).texture(texture).uvs(0f, 0f, 3f, 16f).cullface(Direction.SOUTH);
        el.face(Direction.NORTH).texture(texture).uvs(3f, 0f, 0f, 16f).cullface(Direction.NORTH);
        el.face(Direction.WEST).texture(texture).uvs(16f - d, 0f, d, 16f).cullface(Direction.WEST);
        el.face(Direction.EAST).texture(texture).uvs(d, 0f, 16f - d, 16f);
    }
}
