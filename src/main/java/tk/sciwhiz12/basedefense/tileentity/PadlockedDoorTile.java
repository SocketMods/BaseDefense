package tk.sciwhiz12.basedefense.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import tk.sciwhiz12.basedefense.Reference.TileEntities;

public class PadlockedDoorTile extends LockableTile {
    public PadlockedDoorTile(BlockPos pos, BlockState state) {
        super(TileEntities.PADLOCKED_DOOR, pos, state);
    }
}
