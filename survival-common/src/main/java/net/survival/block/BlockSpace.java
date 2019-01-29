package net.survival.block;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.survival.block.column.Column;
import net.survival.block.column.ColumnPos;
import net.survival.block.message.BlockMessageVisitor;
import net.survival.block.message.BreakBlockMessage;
import net.survival.block.message.PlaceBlockMessage;
import net.survival.interaction.InteractionContext;

public class BlockSpace implements BlockStorage, BlockMessageVisitor
{
    private final Long2ObjectOpenHashMap<Column> columns = new Long2ObjectOpenHashMap<>(1024);

    public Column getColumn(int cx, int cz) { return columns.get(ColumnPos.hashPos(cx, cz)); }
    public Column getColumn(long hashedPos) { return columns.get(hashedPos); }

    public boolean containsColumn(int cx, int cz) { return columns.containsKey(ColumnPos.hashPos(cx, cz)); }
    public boolean containsColumn(long hashedPos) { return columns.containsKey(hashedPos); }

    public Iterable<Long2ObjectMap.Entry<Column>> iterateColumnMap() { return columns.long2ObjectEntrySet(); }
    public ObjectIterator<Long2ObjectMap.Entry<Column>> getColumnMapIterator() { return columns.long2ObjectEntrySet().iterator(); }
    public ObjectIterator<Long2ObjectMap.Entry<Column>> getColumnMapFastIterator() { return columns.long2ObjectEntrySet().fastIterator(); }
    public Iterable<Column> iterateColumns() { return columns.values(); }

    public void addColumn(int cx, int cz, Column column) { columns.put(ColumnPos.hashPos(cx, cz), column); }
    public void addColumn(long hashedPos, Column column) { columns.put(hashedPos, column); }
    public void removeColumn(int cx, int cz) { columns.remove(ColumnPos.hashPos(cx, cz)); }
    public void removeColumn(long hashedPos) { columns.remove(hashedPos); }

    @Override
    public int getBlockFullID(int x, int y, int z) {
        var cx = ColumnPos.toColumnX(x);
        var cz = ColumnPos.toColumnZ(z);

        var column = columns.get(ColumnPos.hashPos(cx, cz));
        if (column == null)
            throw new RuntimeException("Cannot query a block in an unloaded column.");

        var localX = ColumnPos.toLocalX(cx, x);
        var localZ = ColumnPos.toLocalZ(cz, z);

        return column.getBlockFullID(localX, y, localZ);
    }

    @Override
    public void setBlockFullID(int x, int y, int z, int to) {
        var column = getColumnFromGlobalPos(x, z, "Cannot place/replace a block in an unloaded column.");
        var localX = ColumnPos.toLocalX(ColumnPos.toColumnX(x), x);
        var localZ = ColumnPos.toLocalZ(ColumnPos.toColumnZ(z), z);

        column.setBlockFullID(localX, y, localZ, to);
    }

    private Column getColumnFromGlobalPos(int x, int z, String exceptionMessage) {
        var cx = ColumnPos.toColumnX(x);
        var cz = ColumnPos.toColumnZ(z);

        var column = columns.get(ColumnPos.hashPos(cx, cz));
        if (column == null)
            throw new RuntimeException(exceptionMessage);
        
        return column;
    }

    @Override
    public void visit(InteractionContext ic, BreakBlockMessage message) {
        setBlockFullID(message.getX(), message.getY(), message.getZ(), 0);
        ic.burstParticles(message.getX() + 0.5, message.getY() + 0.5, message.getZ() + 0.5, 2.0, 8);
    }

    @Override
    public void visit(InteractionContext ic, PlaceBlockMessage message) {
        setBlockFullID(message.getX(), message.getY(), message.getZ(), message.getFullID());
    }
}