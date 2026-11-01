package com.atsukimc.anticrash.mixin;

import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
//? if <=1.21.11 {
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.SerializingRegionBasedStorage;
import net.minecraft.world.storage.StorageIoWorker;
//?} else if >=26.1 {
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.chunk.storage.SectionStorage;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * POI storage internals the checkpoint cut needs. PointOfInterestStorage#tick would save the
 * same sections, but it also advances the POI distance tracker, which the frozen cut must not do.
 */
//? if <=1.21.11 {
@Mixin(SerializingRegionBasedStorage.class)
//?} else if >=26.1 {
//@Mixin(SectionStorage.class)
//?}
public interface SerializingRegionBasedStorageAccessorMixin {
//? if (>=1.20.5 && <=1.21.10) {
    @Accessor("storageAccess")
    net.minecraft.world.storage.ChunkPosKeyedStorage anticrash$getStorageAccess();

//?} else if <=1.20.4 {
//    @Accessor("worker")
//    StorageIoWorker anticrash$getWorker();
//?} else if >=26.1 {
//    @Accessor("simpleRegionStorage")
//    net.minecraft.world.level.chunk.storage.SimpleRegionStorage anticrash$getStorageAccess();
//
//?} else if =1.21.11 {
//    @Accessor("storageAccess")
//    net.minecraft.world.storage.VersionedChunkStorage anticrash$getStorageAccess();
//
//?}

//? if <=1.21.11 {
    @Accessor("unsavedElements")
//?} else if >=26.1 {
//    @Accessor("dirtyChunks")
//?}
    LongLinkedOpenHashSet anticrash$getUnsavedElements();

//? if <=1.21.11 {
    @Invoker("save")
//?} else if >=26.1 {
//    @Invoker("writeChunk")
//?}
    void anticrash$save(ChunkPos pos);
}
