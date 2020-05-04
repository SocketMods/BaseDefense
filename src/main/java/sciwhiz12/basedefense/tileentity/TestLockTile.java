package sciwhiz12.basedefense.tileentity;

import sciwhiz12.basedefense.api.lock.LockContext;
import sciwhiz12.basedefense.init.BDBlocks;

public class TestLockTile extends LockableTile {
    public TestLockTile() {
        super(BDBlocks.TEST_LOCK_TILE.get());
    }

    @Override
    public void onUnlock(LockContext context) {
    }
}
