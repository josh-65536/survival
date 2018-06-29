package net.survival.world;

import java.util.HashMap;
import java.util.Map;

import net.survival.entity.Entity;
import net.survival.world.chunk.Chunk;
import net.survival.world.chunk.ChunkPos;

public class World
{
    public static final double TICKS_PER_SECOND = 30.0;
    public static final double SECONDS_PER_TICK = 1.0 / TICKS_PER_SECOND;
    
    private final HashMap<Long, Chunk> chunks;
    
    public World() {
        chunks = new HashMap<>();
    }
    
    public Chunk getChunk(int cx, int cy, int cz) {
        return chunks.get(ChunkPos.hashPos(cx, cy, cz));
    }
    
    public Chunk getChunk(long hashedPos) {
        return chunks.get(hashedPos);
    }
    
    public Iterable<Map.Entry<Long, Chunk>> iterateChunkMap() {
        return chunks.entrySet();
    }
    
    public Iterable<Chunk> iterateChunks() {
        return chunks.values();
    }
    
    public void addChunk(int cx, int cy, int cz, Chunk chunk) {
        chunks.put(ChunkPos.hashPos(cx, cy, cz), chunk);
    }
    
    public void removeChunk(int cx, int cy, int cz) {
        chunks.remove(ChunkPos.hashPos(cx, cy, cz));
    }
    
    public void removeChunk(long hashedPos) {
        chunks.remove(hashedPos);
    }

    public short getBlockID(int x, int y, int z) {
        int cx = ChunkPos.toChunkX(x);
        int cy = ChunkPos.toChunkY(y);
        int cz = ChunkPos.toChunkZ(z);

        Chunk chunk = chunks.get(ChunkPos.hashPos(cx, cy, cz));
        if (chunk == null)
            throw new RuntimeException("Cannot query a block in an unloaded chunk.");

        int localX = ChunkPos.toLocalX(cx, x);
        int localY = ChunkPos.toLocalY(cy, y);
        int localZ = ChunkPos.toLocalZ(cz, z);

        return chunk.getBlockID(localX, localY, localZ);
    }

    public void setBlockID(int x, int y, int z, short to) {
        int cx = ChunkPos.toChunkX(x);
        int cy = ChunkPos.toChunkY(y);
        int cz = ChunkPos.toChunkZ(z);

        Chunk chunk = chunks.get(ChunkPos.hashPos(cx, cy, cz));
        if (chunk == null)
            throw new RuntimeException("Cannot place/replace a block in an unloaded chunk.");

        int localX = ChunkPos.toLocalX(cx, x);
        int localY = ChunkPos.toLocalY(cy, y);
        int localZ = ChunkPos.toLocalZ(cz, z);
        
        chunk.setBlockID(localX, localY, localZ, to);
    }
    
    public void addEntity(Entity entity) {
        int cx = ChunkPos.toChunkX((int) entity.getX());
        int cy = ChunkPos.toChunkY((int) entity.getY());
        int cz = ChunkPos.toChunkZ((int) entity.getZ());

        Chunk chunk = chunks.get(ChunkPos.hashPos(cx, cy, cz));
        if (chunk == null)
            throw new RuntimeException("Cannot place an entity in an unloaded chunk.");
        
        chunk.addEntity(entity);
    }
}