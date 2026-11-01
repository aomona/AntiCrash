package com.atsukimc.anticrash.runtime;

import com.atsukimc.anticrash.mixin.RedstoneTorchBlockAccessorMixin;
import com.atsukimc.anticrash.mixin.RedstoneTorchBurnoutEntryAccessorMixin;
//? if <=1.21.11 {
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

//?} else if >=26.1 {
//?}
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.block.RedstoneTorchBlock;
//?}

/** Serializable representation of RedstoneTorchBlock's per-world burnout history. */
public final class RedstoneTorchBurnoutSnapshot {
    public int formatVersion = 1;
    public String dimension;
    public final List<Entry> entries = new ArrayList<>();

//? if <=1.21.11 {
    public static RedstoneTorchBurnoutSnapshot capture(ServerWorld world) {
//?} else if >=26.1 {
//    public static RedstoneTorchBurnoutSnapshot capture(ServerLevel world) {
//?}
        RedstoneTorchBurnoutSnapshot snapshot = new RedstoneTorchBurnoutSnapshot();
//? if <=1.21.11 {
        snapshot.dimension = world.getRegistryKey().getValue().toString();
        Map<BlockView, List<RedstoneTorchBlock.BurnoutEntry>> map =
//?} else if >=26.1 {
//        snapshot.dimension = world.dimension().identifier().toString();
//        Map<BlockGetter, List<RedstoneTorchBlock.Toggle>> map =
//?}
                RedstoneTorchBlockAccessorMixin.anticrash$getBurnoutMap();
//? if <=1.21.11 {
        List<RedstoneTorchBlock.BurnoutEntry> live = map.get(world);
//?} else if >=26.1 {
//        List<RedstoneTorchBlock.Toggle> live = map.get(world);
//?}
        if (live != null) {
//? if <=1.21.11 {
            for (RedstoneTorchBlock.BurnoutEntry entry : live) {
//?} else if >=26.1 {
//            for (RedstoneTorchBlock.Toggle entry : live) {
//?}
                if (entry == null) continue;
                RedstoneTorchBurnoutEntryAccessorMixin entryAccessor =
                        (RedstoneTorchBurnoutEntryAccessorMixin) entry;
                if (entryAccessor.anticrash$getPos() == null) continue;
                Entry copy = new Entry();
                copy.x = entryAccessor.anticrash$getPos().getX();
                copy.y = entryAccessor.anticrash$getPos().getY();
                copy.z = entryAccessor.anticrash$getPos().getZ();
                copy.time = entryAccessor.anticrash$getTime();
                snapshot.entries.add(copy);
            }
        }
        return snapshot;
    }

//? if <=1.21.11 {
    public void restore(ServerWorld world) {
        Map<BlockView, List<RedstoneTorchBlock.BurnoutEntry>> map =
//?} else if >=26.1 {
//    public void restore(ServerLevel world) {
//        Map<BlockGetter, List<RedstoneTorchBlock.Toggle>> map =
//?}
                RedstoneTorchBlockAccessorMixin.anticrash$getBurnoutMap();
        if (entries.isEmpty()) {
            map.remove(world);
            return;
        }
//? if <=1.21.11 {
        List<RedstoneTorchBlock.BurnoutEntry> restored = new ArrayList<>(entries.size());
//?} else if >=26.1 {
//        List<RedstoneTorchBlock.Toggle> restored = new ArrayList<>(entries.size());
//?}
        for (Entry entry : entries) {
//? if <=1.21.11 {
            restored.add(new RedstoneTorchBlock.BurnoutEntry(
//?} else if >=26.1 {
//            restored.add(new RedstoneTorchBlock.Toggle(
//?}
                    new BlockPos(entry.x, entry.y, entry.z), entry.time));
        }
        // Only the new ServerWorld key is written; old World instances are never retained.
        map.put(world, restored);
    }

    public List<String> canonicalLines() {
        List<String> result = new ArrayList<>(entries.size());
        for (int index = 0; index < entries.size(); index++) {
            Entry entry = entries.get(index);
            result.add(dimension + "|" + index + "|" + entry.x + ',' + entry.y + ','
                    + entry.z + "|" + entry.time);
        }
        return result;
    }

    public static final class Entry {
        public int x;
        public int y;
        public int z;
        public long time;
    }
}
