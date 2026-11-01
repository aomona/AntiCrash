package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.ExtensionCarrier;
import com.atsukimc.anticrash.runtime.RuntimeExtensionNbt;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//? if (>=1.21.2 && <=1.21.11) {
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.SerializedChunk;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
//?} else if (>=1.18 && <=1.21.1) {
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.world.ChunkSerializer;
//import net.minecraft.world.chunk.Chunk;
//import net.minecraft.world.chunk.WorldChunk;
//?} else if >=26.1 {
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.chunk.ChunkAccess;
//import net.minecraft.world.level.chunk.LevelChunk;
//import net.minecraft.world.level.chunk.storage.SerializableChunkData;
//?} else if =1.16.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.world.ChunkSerializer;
//import net.minecraft.world.chunk.Chunk;
//import net.minecraft.world.chunk.WorldChunk;
//?}
import org.spongepowered.asm.mixin.Mixin;

/** Tells block entities whether their chunk is saved while it stays loaded; see RuntimeExtensionNbt. */
//? if (>=1.21.2 && <=1.21.11) {
@Mixin(SerializedChunk.class)
//?} else if <=1.21.1 {
//@Mixin(ChunkSerializer.class)
//?} else if >=26.1 {
//@Mixin(SerializableChunkData.class)
//?}
public abstract class ChunkSerializerMixin {
    /**
     * A wrap rather than a HEAD/RETURN pair: the scope must end when serialisation throws, or the
     * next unrelated writeNbt on this thread would be taken for a save of a loaded chunk.
     */
//? if (>=1.21.2 && <=1.21.11) {
    @WrapMethod(method = "fromChunk")
    private static SerializedChunk anticrash$scopeChunkSave(ServerWorld world, Chunk chunk,
                                                        Operation<SerializedChunk> original) {
        WorldChunk worldChunk = chunk instanceof WorldChunk loaded ? loaded : null;
//?} else if (>=1.18 && <=1.21.1) {
//    @WrapMethod(method = "serialize")
//    private static NbtCompound anticrash$scopeChunkSave(ServerWorld world, Chunk chunk,
//                                                        Operation<NbtCompound> original) {
//        WorldChunk worldChunk = chunk instanceof WorldChunk loaded ? loaded : null;
//?} else if >=26.1 {
//    @WrapMethod(method = "copyOf")
//    private static SerializableChunkData anticrash$scopeChunkSave(ServerLevel world, ChunkAccess chunk,
//                                                        Operation<SerializableChunkData> original) {
//        LevelChunk worldChunk = chunk instanceof LevelChunk loaded ? loaded : null;
//?} else if =1.16.1 {
//    @WrapMethod(method = "serialize")
//    private static CompoundTag anticrash$scopeChunkSave(ServerWorld world, Chunk chunk,
//                                                        Operation<CompoundTag> original) {
//        WorldChunk worldChunk = chunk instanceof WorldChunk loaded ? loaded : null;
//?}
        boolean staysLoaded = worldChunk != null
                && ((WorldChunkAccessorMixin) worldChunk).anticrash$isLoadedToWorld();
        RuntimeExtensionNbt.loadedChunkSave(staysLoaded);
        try {
//? if (>=1.21.2 && <=1.21.11) {
            SerializedChunk nbt = original.call(world, chunk);
//?} else if (>=1.18 && <=1.21.1) {
//            NbtCompound nbt = original.call(world, chunk);
//?} else if >=26.1 {
//            SerializableChunkData nbt = original.call(world, chunk);
//?} else if =1.16.1 {
//            CompoundTag nbt = original.call(world, chunk);
//?}
            // Remembered so that an unload rewrites the chunk without the extension even when
            // nothing else changed; see ThreadedAnvilChunkStorageEntityLifecycleMixin.
            if (worldChunk != null) {
                ((ExtensionCarrier) worldChunk).anticrash$setExtensionOnDisk(RuntimeExtensionNbt.extensionWritten());
            }
            return nbt;
        } finally {
            RuntimeExtensionNbt.loadedChunkSave(false);
        }
    }
}
