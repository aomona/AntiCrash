package com.atsukimc.anticrash.mixin;

import com.mojang.datafixers.util.Either;
//? if <=1.21.11 {
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionBasedStorage;
import net.minecraft.world.storage.StorageIoWorker;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.chunk.storage.IOWorker;
//import net.minecraft.world.level.chunk.storage.RegionFileStorage;
//?}

/** The IO actor internals a checkpoint cut seals; see {@link com.atsukimc.anticrash.checkpoint.IoSeal}. */
//? if <=1.21.11 {
@Mixin(StorageIoWorker.class)
//?} else if >=26.1 {
//@Mixin(IOWorker.class)
//?}
public interface StorageIoWorkerAccessorMixin {
    /** Queued writes not yet on disk. Only the worker's own actor thread may touch this map. */
//? if (>=1.21.2 && <=1.21.11) {
    @Accessor("results")
    java.util.SequencedMap<ChunkPos, Object> anticrash$getResults();
//?} else if <=1.21.1 {
//    @Accessor("results")
//    Map<ChunkPos, Object> anticrash$getResults();
//?} else if >=26.1 {
//    @Accessor("pendingWrites")
//    java.util.SequencedMap<ChunkPos, Object> anticrash$getResults();
//?}

    @Accessor("storage")
//? if <=1.21.11 {
    RegionBasedStorage anticrash$getStorage();
//?} else if >=26.1 {
//    RegionFileStorage anticrash$getStorage();
//?}

    /** Runs a task on the worker's actor at foreground priority, after every task queued before it. */
//? if <=1.21.11 {
    @Invoker("writeRemainingResults")
//?} else if >=26.1 {
//    @Invoker("tellStorePending")
//?}
    void anticrash$writeRemainingResults();

//? if (>=1.21.2 && <=1.21.11) {
    @Invoker("run")
    <T> CompletableFuture<T> anticrash$run(Supplier<T> task);
//?} else if <=1.21.1 {
//    @Invoker("run")
//    <T> CompletableFuture<T> anticrash$run(Supplier<Either<T, Exception>> task);
//?} else if >=26.1 {
//    @Invoker("submitTask")
//    <T> CompletableFuture<T> anticrash$run(Supplier<T> task);
//?}
}
