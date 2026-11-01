package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.snapshot.LiveFileTracker;
//? if (>=1.18 && <=1.21.11) {
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionBasedStorage;
//?} else if >=26.1 {
//?} else if =1.16.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.world.storage.RegionBasedStorage;
//?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.chunk.storage.RegionFileStorage;
//?}

/** Records every region file a chunk, entity or POI write touches; see {@link LiveFileTracker}. */
//? if <=1.21.11 {
@Mixin(RegionBasedStorage.class)
//?} else if >=26.1 {
//@Mixin(RegionFileStorage.class)
//?}
public abstract class RegionBasedStorageMixin {
//? if <=1.21.11 {
    @Shadow @Final private Path directory;
//?} else if >=26.1 {
//    @Shadow @Final private Path folder;
//?}

    @Inject(method = "write", at = @At("HEAD"))
//? if (>=1.18 && <=1.21.11) {
    private void anticrash$recordWrite(ChunkPos pos, NbtCompound nbt, CallbackInfo callbackInfo) {
        LiveFileTracker.global().recordRegionWrite(directory, pos);
//?} else if >=26.1 {
//    private void anticrash$recordWrite(ChunkPos pos, CompoundTag nbt, CallbackInfo callbackInfo) {
//        LiveFileTracker.global().recordRegionWrite(folder, pos);
//?} else if =1.16.1 {
//    private void anticrash$recordWrite(ChunkPos pos, CompoundTag nbt, CallbackInfo callbackInfo) {
//        LiveFileTracker.global().recordRegionWrite(directory, pos);
//?}
    }
}
