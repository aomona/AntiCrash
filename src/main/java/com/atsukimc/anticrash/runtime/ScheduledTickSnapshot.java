package com.atsukimc.anticrash.runtime;
//? if (>=1.19.3 && <=1.21.11) {

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.world.tick.TickPriority;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.tick.ChunkTickScheduler;
import net.minecraft.world.tick.OrderedTick;
//?} else if <=1.19.2 {
//
//import net.minecraft.block.Block;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.registry.Registry;
//import net.minecraft.world.TickPriority;
//import net.minecraft.world.chunk.WorldChunk;
//import net.minecraft.world.tick.ChunkTickScheduler;
//import net.minecraft.world.tick.OrderedTick;
//?} else if >=26.1 {
//?}

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Registry;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.chunk.LevelChunk;
//import net.minecraft.world.level.material.Fluid;
//import net.minecraft.world.ticks.LevelChunkTicks;
//import net.minecraft.world.ticks.ScheduledTick;
//import net.minecraft.world.ticks.TickPriority;
//?}

/**
 * Exact runtime representation of one chunk-local scheduled tick.
 *
 * <p>Vanilla 1.18.2 chunk NBT stores the relative delay and priority, but not
 * {@link OrderedTick#subTickOrder()}. Reconstructing a queue from that NBT can therefore change
 * the order of observer/redstone updates sharing the same trigger tick. Runtime Capsule v3 keeps
 * all ordering fields required to recreate the active queue exactly.</p>
 */
public final class ScheduledTickSnapshot {
    public static final String BLOCK = "BLOCK";
    public static final String FLUID = "FLUID";

    private static final Comparator<ScheduledTickSnapshot> STABLE_ORDER = Comparator
            .comparingLong((ScheduledTickSnapshot tick) -> tick.triggerTick)
            .thenComparingInt(tick -> tick.priority)
            .thenComparingLong(tick -> tick.subTickOrder)
            .thenComparing(tick -> tick.kind)
            .thenComparingInt(tick -> tick.x)
            .thenComparingInt(tick -> tick.y)
            .thenComparingInt(tick -> tick.z)
            .thenComparing(tick -> tick.type);

    public String dimension;
    public String kind;
    public String type;
    public int x;
    public int y;
    public int z;
    public long triggerTick;
    public int priority;
    public long subTickOrder;

//? if <=1.21.11 {
    public static List<ScheduledTickSnapshot> capture(String dimension, WorldChunk chunk) {
//?} else if >=26.1 {
//    public static List<ScheduledTickSnapshot> capture(String dimension, LevelChunk chunk) {
//?}
        List<ScheduledTickSnapshot> result = new ArrayList<>();
//? if (>=1.19.3 && <=1.21.11) {
        captureQueue(result, dimension, BLOCK, blockScheduler(chunk), Registries.BLOCK);
        captureQueue(result, dimension, FLUID, fluidScheduler(chunk), Registries.FLUID);
//?} else if <=1.19.2 {
//        captureQueue(result, dimension, BLOCK, blockScheduler(chunk), Registry.BLOCK);
//        captureQueue(result, dimension, FLUID, fluidScheduler(chunk), Registry.FLUID);
//?} else if >=26.1 {
//        captureQueue(result, dimension, BLOCK, blockScheduler(chunk), BuiltInRegistries.BLOCK);
//        captureQueue(result, dimension, FLUID, fluidScheduler(chunk), BuiltInRegistries.FLUID);
//?}
        result.sort(STABLE_ORDER);
        return result;
    }

    public BlockPos position() {
        return new BlockPos(x, y, z);
    }

    public Identifier typeIdentifier() {
        return Identifier.tryParse(type);
    }

    public TickPriority tickPriority() {
//? if <=1.21.11 {
        return TickPriority.byIndex(priority);
//?} else if >=26.1 {
//        return TickPriority.byValue(priority);
//?}
    }

    @SuppressWarnings("unchecked")
//? if <=1.21.11 {
    static ChunkTickScheduler<Block> blockScheduler(WorldChunk chunk) {
        return (ChunkTickScheduler<Block>) chunk.getBlockTickScheduler();
//?} else if >=26.1 {
//    static LevelChunkTicks<Block> blockScheduler(LevelChunk chunk) {
//        return (LevelChunkTicks<Block>) chunk.getBlockTicks();
//?}
    }

    @SuppressWarnings("unchecked")
//? if <=1.21.11 {
    static ChunkTickScheduler<Fluid> fluidScheduler(WorldChunk chunk) {
        return (ChunkTickScheduler<Fluid>) chunk.getFluidTickScheduler();
//?} else if >=26.1 {
//    static LevelChunkTicks<Fluid> fluidScheduler(LevelChunk chunk) {
//        return (LevelChunkTicks<Fluid>) chunk.getFluidTicks();
//?}
    }

    private static <T> void captureQueue(List<ScheduledTickSnapshot> output, String dimension,
//? if <=1.21.11 {
                                         String kind, ChunkTickScheduler<T> scheduler,
//?} else if >=26.1 {
//                                         String kind, LevelChunkTicks<T> scheduler,
//?}
                                         Registry<T> registry) {
//? if <=1.21.11 {
        scheduler.getQueueAsStream().forEach(ordered -> {
            Identifier identifier = registry.getId(ordered.type());
//?} else if >=26.1 {
//        scheduler.getAll().forEach(ordered -> {
//            Identifier identifier = registry.getKey(ordered.type());
//?}
            if (identifier == null) {
                throw new IllegalStateException("Scheduled tick type is not registered");
            }
            ScheduledTickSnapshot snapshot = new ScheduledTickSnapshot();
            snapshot.dimension = dimension;
            snapshot.kind = kind;
            snapshot.type = identifier.toString();
            snapshot.x = ordered.pos().getX();
            snapshot.y = ordered.pos().getY();
            snapshot.z = ordered.pos().getZ();
            snapshot.triggerTick = ordered.triggerTick();
//? if <=1.21.11 {
            snapshot.priority = ordered.priority().getIndex();
//?} else if >=26.1 {
//            snapshot.priority = ordered.priority().getValue();
//?}
            snapshot.subTickOrder = ordered.subTickOrder();
            output.add(snapshot);
        });
    }
}
