package sciwhiz12.basedefense.api.lock;

public interface ILock {
    public boolean onUnlock(LockContext context);
}
