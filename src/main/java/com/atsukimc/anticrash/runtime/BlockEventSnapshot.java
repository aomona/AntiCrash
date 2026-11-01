package com.atsukimc.anticrash.runtime;

import com.atsukimc.anticrash.mixin.ServerWorldAccessorMixin;
//? if (>=1.19.3 && <=1.21.11) {
import net.minecraft.block.Block;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;

//?} else if <=1.19.2 {
//import net.minecraft.block.Block;
//import net.minecraft.server.world.BlockEvent;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.registry.Registry;
//
//?} else if >=26.1 {
//?}
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.Identifier;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.BlockEventData;
//import net.minecraft.world.level.block.Block;
//?}

public final class BlockEventSnapshot {
    public int formatVersion = 1;
    public final List<Entry> entries = new ArrayList<>();

//? if <=1.21.11 {
    public static BlockEventSnapshot capture(Iterable<ServerWorld> worlds) {
//?} else if >=26.1 {
//    public static BlockEventSnapshot capture(Iterable<ServerLevel> worlds) {
//?}
        BlockEventSnapshot snapshot = new BlockEventSnapshot();
//? if <=1.21.11 {
        for (ServerWorld world : worlds) {
//?} else if >=26.1 {
//        for (ServerLevel world : worlds) {
//?}
            ServerWorldAccessorMixin accessor = (ServerWorldAccessorMixin) world;
//? if <=1.21.11 {
            for (BlockEvent event : accessor.anticrash$getSyncedBlockEventQueue()) {
//?} else if >=26.1 {
//            for (BlockEventData event : accessor.anticrash$getSyncedBlockEventQueue()) {
//?}
                snapshot.entries.add(Entry.of(world, event, "SYNCED"));
            }
//? if <=1.21.11 {
            for (BlockEvent event : accessor.anticrash$getBlockEventQueue()) {
//?} else if >=26.1 {
//            for (BlockEventData event : accessor.anticrash$getBlockEventQueue()) {
//?}
                snapshot.entries.add(Entry.of(world, event, "REGULAR"));
            }
        }
        return snapshot;
    }

//? if <=1.21.11 {
    public RestoreResult restore(Iterable<ServerWorld> worlds) {
//?} else if >=26.1 {
//    public RestoreResult restore(Iterable<ServerLevel> worlds) {
//?}
        RestoreResult result = new RestoreResult();
        if (formatVersion != 1 || entries == null) { result.failed = 1; return result; }
//? if <=1.21.11 {
        Map<String, ServerWorld> byDimension = new LinkedHashMap<>();
        for (ServerWorld world : worlds) byDimension.put(world.getRegistryKey().getValue().toString(), world);
//?} else if >=26.1 {
//        Map<String, ServerLevel> byDimension = new LinkedHashMap<>();
//        for (ServerLevel world : worlds) byDimension.put(world.dimension().identifier().toString(), world);
//?}
        List<PreparedEntry> prepared = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            try {
                if (entry == null || !("SYNCED".equals(entry.queue) || "REGULAR".equals(entry.queue))) throw new IllegalArgumentException("invalid block event queue");
//? if <=1.21.11 {
                ServerWorld world = byDimension.get(entry.dimension);
//?} else if >=26.1 {
//                ServerLevel world = byDimension.get(entry.dimension);
//?}
                if (world == null) throw new IllegalArgumentException("unknown block event dimension");
//? if (>=1.21 && <=1.21.11) {
                Identifier blockId = Identifier.of(entry.block);
                if (!Registries.BLOCK.containsId(blockId)) throw new IllegalArgumentException("unknown block id");
                Block block = Registries.BLOCK.get(blockId);
                BlockEvent event = new BlockEvent(new BlockPos(entry.x, entry.y, entry.z), block, entry.type, entry.data);
//?} else if (>=1.19.3 && <=1.20.6) {
//                Identifier blockId = new Identifier(entry.block);
//                if (!Registries.BLOCK.containsId(blockId)) throw new IllegalArgumentException("unknown block id");
//                Block block = Registries.BLOCK.get(blockId);
//                BlockEvent event = new BlockEvent(new BlockPos(entry.x, entry.y, entry.z), block, entry.type, entry.data);
//?} else if <=1.19.2 {
//                Identifier blockId = new Identifier(entry.block);
//                if (!Registry.BLOCK.containsId(blockId)) throw new IllegalArgumentException("unknown block id");
//                Block block = Registry.BLOCK.get(blockId);
//                BlockEvent event = new BlockEvent(new BlockPos(entry.x, entry.y, entry.z), block, entry.type, entry.data);
//?} else if >=26.1 {
//                Identifier blockId = Identifier.parse(entry.block);
//                if (!BuiltInRegistries.BLOCK.containsKey(blockId)) throw new IllegalArgumentException("unknown block id");
//                Block block = BuiltInRegistries.BLOCK.getValue(blockId);
//                BlockEventData event = new BlockEventData(new BlockPos(entry.x, entry.y, entry.z), block, entry.type, entry.data);
//?}
                prepared.add(new PreparedEntry(world, entry.queue, event));
            } catch (Exception exception) {
                result.failed++;
            }
        }
        // Never clear live queues if even one checkpoint entry is invalid.  Validation is
        // all-or-nothing so RECOVERY_FAILED leaves the freshly loaded world untouched.
        if (result.failed > 0) return result;
//? if <=1.21.11 {
        for (ServerWorld world : byDimension.values()) {
//?} else if >=26.1 {
//        for (ServerLevel world : byDimension.values()) {
//?}
            ServerWorldAccessorMixin accessor = (ServerWorldAccessorMixin) world;
            accessor.anticrash$getSyncedBlockEventQueue().clear();
            accessor.anticrash$getBlockEventQueue().clear();
        }
        for (PreparedEntry entry : prepared) {
            ServerWorldAccessorMixin accessor = (ServerWorldAccessorMixin) entry.world;
            if ("SYNCED".equals(entry.queue)) accessor.anticrash$getSyncedBlockEventQueue().add(entry.event);
            else accessor.anticrash$getBlockEventQueue().add(entry.event);
            result.restored++;
        }
        return result;
    }

//? if <=1.21.11 {
    private record PreparedEntry(ServerWorld world, String queue, BlockEvent event) { }
//?} else if >=26.1 {
//    private record PreparedEntry(ServerLevel world, String queue, BlockEventData event) { }
//?}

    public static final class Entry {
        public String queue;
        public String dimension;
        public int x, y, z;
        public String block;
        public int type, data;

//? if <=1.21.11 {
        static Entry of(ServerWorld world, BlockEvent event, String queue) {
//?} else if >=26.1 {
//        static Entry of(ServerLevel world, BlockEventData event, String queue) {
//?}
            Entry entry = new Entry();
            entry.queue = queue;
//? if <=1.21.11 {
            entry.dimension = world.getRegistryKey().getValue().toString();
//?} else if >=26.1 {
//            entry.dimension = world.dimension().identifier().toString();
//?}
            entry.x = event.pos().getX(); entry.y = event.pos().getY(); entry.z = event.pos().getZ();
//? if (>=1.19.3 && <=1.21.11) {
            entry.block = Registries.BLOCK.getId(event.block()).toString();
            entry.type = event.type(); entry.data = event.data();
//?} else if <=1.19.2 {
//            entry.block = Registry.BLOCK.getId(event.block()).toString();
//            entry.type = event.type(); entry.data = event.data();
//?} else if >=26.1 {
//            entry.block = BuiltInRegistries.BLOCK.getKey(event.block()).toString();
//            entry.type = event.paramA(); entry.data = event.paramB();
//?}
            return entry;
        }
    }

    public static final class RestoreResult {
        public int restored;
        public int failed;
    }
}
