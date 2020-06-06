package sciwhiz12.basedefense.block;

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
    public KeysmithBlock() {
        super(Block.Properties.create(Material.WOOD));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity,
            Hand hand, BlockRayTraceResult result) {
        playerEntity.openContainer(state.getContainer(world, pos));
        return ActionResultType.SUCCESS;
    }

    @Override
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedContainerProvider((windowId, playerInventory, playerEntity) -> {
            return new KeysmithContainer(windowId, playerInventory, IWorldPosCallable.of(world, pos));
        }, new TranslationTextComponent("container.basedefense.keysmith"));
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
