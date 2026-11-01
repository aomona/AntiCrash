//? if >=1.20.5 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.20.5 && <=1.21.10) {
import net.minecraft.world.storage.ChunkPosKeyedStorage;
import net.minecraft.world.storage.StorageIoWorker;
//?} else if >=26.1 {
//import net.minecraft.world.level.chunk.storage.IOWorker;
//import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
//?} else if =1.21.11 {
//import net.minecraft.world.storage.VersionedChunkStorage;
//import net.minecraft.world.storage.StorageIoWorker;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if (>=1.20.5 && <=1.21.10) {
@Mixin(ChunkPosKeyedStorage.class)
//?} else if >=26.1 {
//@Mixin(SimpleRegionStorage.class)
//?} else if =1.21.11 {
//@Mixin(VersionedChunkStorage.class)
//?}
public interface ChunkPosKeyedStorageAccessor {
//? if (>=1.20.5 && <=1.21.11) {
    @Accessor("worker") StorageIoWorker anticrash$getWorker();
//?} else if >=26.1 {
//    @Accessor("worker") IOWorker anticrash$getWorker();
//?}
}
//?}
