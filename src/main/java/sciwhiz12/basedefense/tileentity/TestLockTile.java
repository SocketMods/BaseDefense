package sciwhiz12.basedefense.tileentity;

import sciwhiz12.basedefense.api.lock.LockContext;
import sciwhiz12.basedefense.init.ModTileEntities;

public class TestLockTile extends LockableTile {
    public TestLockTile() {
        super(ModTileEntities.TEST_LOCK_TILE.get());
    }

    @Override
    public boolean onUnlock(LockContext context) {
        return true;
    }
}
