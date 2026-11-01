package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.snapshot.EntityChunkWriteDeduplicator;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? if (>=1.20.5 && <=1.21.10) {
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.EntityChunkDataAccess;
import net.minecraft.world.storage.ChunkPosKeyedStorage;
//?} else if (>=1.18 && <=1.20.4) {
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.world.storage.EntityChunkDataAccess;
//import net.minecraft.world.storage.StorageIoWorker;
//?} else if >=26.1 {
//?} else if =1.16.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.world.storage.EntityChunkDataAccess;
//import net.minecraft.world.storage.StorageIoWorker;
//?} else if =1.21.11 {
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.world.storage.EntityChunkDataAccess;
//import net.minecraft.world.storage.VersionedChunkStorage;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.chunk.storage.EntityStorage;
//import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
//?}

/** Skips rewriting an unchanged entity chunk; see {@link EntityChunkWriteDeduplicator}. */
//? if <=1.21.11 {
@Mixin(EntityChunkDataAccess.class)
//?} else if >=26.1 {
//@Mixin(EntityStorage.class)
//?}
public abstract class EntityChunkDataAccessMixin {
    @Unique
    private final EntityChunkWriteDeduplicator anticrash$deduplicator = new EntityChunkWriteDeduplicator();

    /**
     * Entity runtime state is added only to data that goes to the entity's chunk on disk. A wrap,
     * so the scope also ends when an entity fails to save.
     */
//? if <=1.21.11 {
    @com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod(method = "writeChunkData")
    private void anticrash$scopeEntitySave(net.minecraft.world.storage.ChunkDataList<?> dataList,
//?} else if >=26.1 {
//    @com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod(method = "storeEntities")
//    private void anticrash$scopeEntitySave(net.minecraft.world.level.entity.ChunkEntities<?> dataList,
//?}
                                           Operation<Void> original) {
        com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.entitySave(true);
        try {
            original.call(dataList);
        } finally {
            com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.entitySave(false);
        }
    }

//? if (>=1.20.5 && <=1.21.10) {
    @WrapOperation(method = "writeChunkData", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/storage/ChunkPosKeyedStorage;set(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> anticrash$skipUnchangedEntityChunk(ChunkPosKeyedStorage worker, ChunkPos pos,
                                                                      NbtCompound nbt,
//?} else if (>=1.18 && <=1.20.4) {
//    @WrapOperation(method = "writeChunkData", at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/world/storage/StorageIoWorker;setResult(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)Ljava/util/concurrent/CompletableFuture;"))
//    private CompletableFuture<Void> anticrash$skipUnchangedEntityChunk(StorageIoWorker worker, ChunkPos pos,
//                                                                      NbtCompound nbt,
//?} else if >=26.1 {
//    @WrapOperation(method = "storeEntities", at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/world/level/chunk/storage/SimpleRegionStorage;write(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Ljava/util/concurrent/CompletableFuture;"))
//    private CompletableFuture<Void> anticrash$skipUnchangedEntityChunk(SimpleRegionStorage worker, ChunkPos pos,
//                                                                      CompoundTag nbt,
//?} else if =1.16.1 {
//    @WrapOperation(method = "writeChunkData", at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/world/storage/StorageIoWorker;setResult(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)Ljava/util/concurrent/CompletableFuture;"))
//    private CompletableFuture<Void> anticrash$skipUnchangedEntityChunk(StorageIoWorker worker, ChunkPos pos,
//                                                                      CompoundTag nbt,
//?} else if =1.21.11 {
//    @WrapOperation(method = "writeChunkData", at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/world/storage/VersionedChunkStorage;setNbt(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/nbt/NbtCompound;)Ljava/util/concurrent/CompletableFuture;"))
//    private CompletableFuture<Void> anticrash$skipUnchangedEntityChunk(VersionedChunkStorage worker, ChunkPos pos,
//                                                                      NbtCompound nbt,
//?}
                                                                      Operation<CompletableFuture<Void>> original) {
//? if <=1.21.11 {
        long chunk = pos.toLong();
//?} else if >=26.1 {
//        long chunk = pos.pack();
//?}
        if (nbt == null) {
            anticrash$deduplicator.forget(chunk);
            return original.call(worker, pos, nbt);
        }
        byte[] digest = anticrash$deduplicator.digestIfChanged(chunk, nbt);
        if (digest == null) return CompletableFuture.completedFuture(null);
        anticrash$deduplicator.submitted(chunk, digest);
        CompletableFuture<Void> result = original.call(worker, pos, nbt);
        result.whenComplete((ignored, failure) -> {
            if (failure != null) anticrash$deduplicator.forget(chunk);
        });
        return result;
    }
//? if <=1.21.10 {
//?} else if >=26.1 {
//
//    // The empty-entity path now writes NULL_NBT_SUPPLIER. Forget its last digest so a later
//    // identical entity payload cannot be incorrectly skipped after the chunk was deleted.
//    @WrapOperation(method="storeEntities",at=@At(value="INVOKE",
//            target="Lnet/minecraft/world/level/chunk/storage/SimpleRegionStorage;write(Lnet/minecraft/world/level/ChunkPos;Ljava/util/function/Supplier;)Ljava/util/concurrent/CompletableFuture;"))
//    private CompletableFuture<Void> anticrash$forgetDeletedEntityChunk(SimpleRegionStorage storage,ChunkPos pos,
//            java.util.function.Supplier<CompoundTag> value,Operation<CompletableFuture<Void>> original) {
//        anticrash$deduplicator.forget(pos.pack());
//        return original.call(storage,pos,value);
//    }
//?} else if =1.21.11 {
//
//    // The empty-entity path now writes NULL_NBT_SUPPLIER. Forget its last digest so a later
//    // identical entity payload cannot be incorrectly skipped after the chunk was deleted.
//    @WrapOperation(method="writeChunkData",at=@At(value="INVOKE",
//            target="Lnet/minecraft/world/storage/VersionedChunkStorage;set(Lnet/minecraft/util/math/ChunkPos;Ljava/util/function/Supplier;)Ljava/util/concurrent/CompletableFuture;"))
//    private CompletableFuture<Void> anticrash$forgetDeletedEntityChunk(VersionedChunkStorage storage,ChunkPos pos,
//            java.util.function.Supplier<NbtCompound> value,Operation<CompletableFuture<Void>> original) {
//        anticrash$deduplicator.forget(pos.toLong());
//        return original.call(storage,pos,value);
//    }
//?}
}
