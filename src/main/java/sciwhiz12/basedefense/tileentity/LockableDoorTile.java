package sciwhiz12.basedefense.tileentity;

import sciwhiz12.basedefense.api.lock.LockContext;
import sciwhiz12.basedefense.init.ModTileEntities;

public class LockableDoorTile extends LockableTile {
    public LockableDoorTile() {
        super(ModTileEntities.LOCK_DOOR_TILE.get());
    }

    @Override
    public boolean onUnlock(LockContext context) {
        return true;
    }
}
