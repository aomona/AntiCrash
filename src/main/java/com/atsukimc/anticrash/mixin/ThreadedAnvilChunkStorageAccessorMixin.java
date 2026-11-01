package com.atsukimc.anticrash.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
//? if (>=1.21 && <=1.21.11) {
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.world.chunk.Chunk;
//?} else if <=1.20.6 {
//import net.minecraft.server.world.ChunkHolder;
//import net.minecraft.server.world.ThreadedAnvilChunkStorage;
//import net.minecraft.world.chunk.Chunk;
//?} else if >=26.1 {
//import net.minecraft.server.level.ChunkHolder;
//import net.minecraft.server.level.ChunkMap;
//import net.minecraft.world.level.chunk.ChunkAccess;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

//? if (>=1.21 && <=1.21.11) {
@Mixin(ServerChunkLoadingManager.class)
//?} else if <=1.20.6 {
//@Mixin(ThreadedAnvilChunkStorage.class)
//?} else if >=26.1 {
//@Mixin(ChunkMap.class)
//?}
public interface ThreadedAnvilChunkStorageAccessorMixin {
//? if (>=1.21 && <=1.21.11) {
    @Accessor("currentChunkHolders")
//?} else if <=1.20.6 {
//    @Accessor("loadedChunks")
//    LongSet anticrash$getLoadedChunks();
//
//    @Accessor("currentChunkHolders")
//?} else if >=26.1 {
//    @Accessor("updatingChunkMap")
//?}
    Long2ObjectLinkedOpenHashMap<ChunkHolder> anticrash$getCurrentChunkHolders();

    /**
     * Holders already taken out of the live map and waiting for their unload task. Their chunk
     * may still hold changes: the unload task is what saves it, one tick or more later.
     */
//? if <=1.21.11 {
    @Accessor("chunksToUnload")
//?} else if >=26.1 {
//    @Accessor("pendingUnloads")
//?}
    Long2ObjectLinkedOpenHashMap<ChunkHolder> anticrash$getChunksToUnload();

    /**
     * Serializes one already-loaded dirty chunk without joining every ChunkHolder future.
     * CheckpointManager calls this in small, IO-drained batches while simulation is frozen.
     */
    @Invoker("save")
//? if <=1.21.11 {
    boolean anticrash$saveChunk(Chunk chunk);
//?} else if >=26.1 {
//    boolean anticrash$saveChunk(ChunkAccess chunk);
//?}
}
