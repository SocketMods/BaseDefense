package sciwhiz12.basedefense.api.lock;

public interface IKey {
    public boolean canUnlock(LockContext context);
    
    public void unlock(LockContext context);
}
