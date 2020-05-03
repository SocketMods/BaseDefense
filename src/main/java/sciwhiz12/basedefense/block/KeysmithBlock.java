package sciwhiz12.basedefense.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import sciwhiz12.basedefense.container.KeysmithContainer;

public class KeysmithBlock extends Block {
    private static final TranslationTextComponent nameTranslationKey = new TranslationTextComponent(
            "container.basedefense.keysmith"
    );

    public KeysmithBlock() {
        super(Block.Properties.create(Material.WOOD));
    }

    public ActionResultType onBlocskActivated(BlockState state, World worldIn, BlockPos pos,
            PlayerEntity player, Hand handIn, BlockRayTraceResult rayTrace) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            player.openContainer(state.getContainer(worldIn, pos));
            return ActionResultType.SUCCESS;
        }
    }

    /*
     * @SuppressWarnings("deprecation")
     * 
     * @Override public ActionResultType onBlockActivated(BlockState state, World
     * world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult
     * result) { if (!world.isRemote) { NetworkHooks.openGui((ServerPlayerEntity)
     * player, state.getContainer(world, pos), pos); return
     * ActionResultType.SUCCESS; } return super.onBlockActivated(state, world, pos,
     * player, hand, result); }
     */

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos,
            PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result) {
        playerEntity.openContainer(state.getContainer(world, pos));
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedContainerProvider(
                (windowId, playerInventory, playerEntity) -> {
                    return new KeysmithContainer(
                            windowId, playerInventory, IWorldPosCallable.of(world, pos)
                    );
                }, nameTranslationKey
        );
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos,
            PathType type) {
        return false;
    }
}
