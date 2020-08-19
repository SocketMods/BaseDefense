package sciwhiz12.basedefense.world;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static sciwhiz12.basedefense.Reference.MODID;

public class ProtectedChunksSavedData extends WorldSavedData {
    public static final String IDENTIFIER = MODID + "_protected_chunks";

    public static ProtectedChunksSavedData getFromWorld(ServerWorld world) {
        return world.getSavedData().getOrCreate(ProtectedChunksSavedData::new, IDENTIFIER);
    }

    private final ListMultimap<UUID, Long> chunkMultimap = LinkedListMultimap.create();

    public ProtectedChunksSavedData() {
        super(IDENTIFIER);
    }

    @Override
    public void read(CompoundNBT nbt) {
        chunkMultimap.clear();
        ListNBT chunksData = nbt.getList(IDENTIFIER, Constants.NBT.TAG_COMPOUND);
        for (INBT data : chunksData) {
            CompoundNBT realData = (CompoundNBT) data;
            UUID owner = realData.getUniqueId("Owner");
            long[] chunks = realData.getLongArray("Chunks");
            for (long chunkPos : chunks) {
                chunkMultimap.put(owner, chunkPos);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT chunksData = new ListNBT();
        for (UUID id : chunkMultimap.keySet()) {
            CompoundNBT data = new CompoundNBT();
            data.putUniqueId("Owner", id);
            data.put("Chunks", new LongArrayNBT(chunkMultimap.get(id)));
            chunksData.add(data);
        }
        compound.put(IDENTIFIER, chunksData);
        return compound;
    }

    public Multimap<UUID, Long> getChunkMultimap() {
        return Multimaps.unmodifiableMultimap(chunkMultimap);
    }

    public List<Long> getOwnedChunks(UUID id) {
        return Collections.unmodifiableList(chunkMultimap.get(id));
    }

    public boolean hasOwnedChunks(UUID id) {
        return chunkMultimap.containsKey(id);
    }

    public boolean isChunkOwned(long chunkPos) {
        return chunkMultimap.containsValue(chunkPos);
    }

    public boolean ownsChunk(UUID id, long chunkPos) {
        return chunkMultimap.containsEntry(id, chunkPos);
    }

    @Nullable
    public UUID getChunkOwner(long chunkPos) {
        return !chunkMultimap.containsValue(chunkPos) ?
                null :
                chunkMultimap.entries().stream().filter(entry -> entry.getValue() == chunkPos).map(Map.Entry::getKey)
                        .findFirst().orElse(null);
    }

    public void addOwnedChunk(UUID id, long chunkPos) {
        chunkMultimap.put(id, chunkPos);
        markDirty();
    }

    public void removeChunkOwner(long chunkPos) {
        chunkMultimap.values().remove(chunkPos);
        markDirty();
    }

    public void clearOwnedChunks(UUID id) {
        chunkMultimap.removeAll(id);
        markDirty();
    }
}
