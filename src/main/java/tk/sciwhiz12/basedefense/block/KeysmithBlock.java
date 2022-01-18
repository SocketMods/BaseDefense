package tk.sciwhiz12.basedefense.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraftforge.fml.network.NetworkHooks;
import tk.sciwhiz12.basedefense.container.KeysmithContainer;

public class KeysmithBlock extends Block {
    public KeysmithBlock() {
        super(Block.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity,
            Hand hand, BlockRayTraceResult result) {
        if (!world.isClientSide) { NetworkHooks.openGui((ServerPlayerEntity) playerEntity, getMenuProvider(state, world, pos)); }
        return ActionResultType.SUCCESS;
    }

    @Override
    public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedContainerProvider(
                (windowId, playerInventory, playerEntity) -> new KeysmithContainer(windowId, playerInventory,
                        IWorldPosCallable.create(world, pos)), new TranslationTextComponent("container.basedefense.keysmith"));
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
