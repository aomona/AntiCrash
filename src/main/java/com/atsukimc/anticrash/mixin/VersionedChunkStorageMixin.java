package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.snapshot.SealedNbt;
//? if (>=1.18 && <=1.21.11) {
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.storage.VersionedChunkStorage;
//?} else if >=26.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
//?} else if =1.16.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.storage.VersionedChunkStorage;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

//? if <=1.21.11 {
@Mixin(VersionedChunkStorage.class)
//?} else if >=26.1 {
//@Mixin(SimpleRegionStorage.class)
//?}
public abstract class VersionedChunkStorageMixin {
    /**
     * A chunk read while its save is still queued gets the queued NbtCompound itself, and this
     * method writes a "__context" key into it. Vanilla races its own IO thread there; a sealed
     * checkpoint also writes that object into its generation, so it gets a private copy instead.
     */
//? if (>=1.18 && <=1.21.11) {
    @ModifyVariable(method = "updateChunkNbt", at = @At("HEAD"), argsOnly = true)
    private NbtCompound anticrash$copySealedChunk(NbtCompound nbt) {
//?} else if >=26.1 {
//    @ModifyVariable(method = "upgradeChunkTag", at = @At("HEAD"), argsOnly = true)
//    private CompoundTag anticrash$copySealedChunk(CompoundTag nbt) {
//?} else if =1.16.1 {
//    @ModifyVariable(method = "updateChunkNbt", at = @At("HEAD"), argsOnly = true)
//    private CompoundTag anticrash$copySealedChunk(CompoundTag nbt) {
//?}
        return SealedNbt.isSealed(nbt) ? nbt.copy() : nbt;
    }
}
