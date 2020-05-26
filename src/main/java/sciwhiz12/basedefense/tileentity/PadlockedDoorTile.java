package sciwhiz12.basedefense.tileentity;

import sciwhiz12.basedefense.init.ModTileEntities;

public class PadlockedDoorTile extends LockableTile {
    public PadlockedDoorTile() {
        super(ModTileEntities.PADLOCKED_DOOR.get());
    }

    @Override
    public boolean hasFastRenderer() {
        return true;
    }
}
