package tk.sciwhiz12.basedefense.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import tk.sciwhiz12.basedefense.container.LocksmithContainer;

public class LocksmithBlock extends Block {
    public LocksmithBlock() {
        super(Block.Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL));
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity,
                                 InteractionHand hand, BlockHitResult result) {
        if (!world.isClientSide) {
            NetworkHooks.openGui((ServerPlayer) playerEntity, getMenuProvider(state, world, pos));
        }
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimpleMenuProvider(
            (windowId, playerInventory, playerEntity) -> new LocksmithContainer(windowId, playerInventory,
                ContainerLevelAccess.create(world, pos)), new TranslatableComponent("container.basedefense.locksmith"));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }
}
