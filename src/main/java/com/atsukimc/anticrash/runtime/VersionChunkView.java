//? if >=1.21 {
package com.atsukimc.anticrash.runtime;

//? if (>=1.21 && <=1.21.11) {
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
//?} else if >=26.1 {
//import net.minecraft.server.level.ChunkHolder;
//import net.minecraft.world.level.chunk.ImposterProtoChunk;
//import net.minecraft.world.level.chunk.LevelChunk;
//?}

/** Read-only, non-loading view. ChunkHolder#getWorldChunk exposes ticking, not all loaded FULL. */
public final class VersionChunkView {
    private VersionChunkView() {}
//? if (>=1.21 && <=1.21.11) {
    public static WorldChunk worldChunk(ChunkHolder holder) {
        var latest = holder.getLatest();
        if (latest instanceof WrapperProtoChunk wrapper) latest = wrapper.getWrappedChunk();
        return latest instanceof WorldChunk chunk ? chunk : null;
//?} else if >=26.1 {
//    public static LevelChunk worldChunk(ChunkHolder holder) {
//        var latest = holder.getLatestChunk();
//        if (latest instanceof ImposterProtoChunk wrapper) latest = wrapper.getWrapped();
//        return latest instanceof LevelChunk chunk ? chunk : null;
//?}
    }
}
//?}
