package com.atsukimc.anticrash.mixin;

//? if (>=1.18 && <=1.21.11) {
import net.minecraft.nbt.NbtCompound;
//?} else if =1.16.1 || >=26.1 {
//import net.minecraft.nbt.CompoundTag;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <=1.21.11 {
@Mixin(targets = "net.minecraft.world.storage.StorageIoWorker$Result")
//?} else if >=26.1 {
//@Mixin(targets = "net.minecraft.world.level.chunk.storage.IOWorker$PendingStore")
//?}
public interface StorageIoWorkerResultAccessorMixin {
//? if (>=1.18 && <=1.21.11) {
    @Accessor("nbt")
    NbtCompound anticrash$getNbt();
//?} else if >=26.1 {
//    @Accessor("data")
//    CompoundTag anticrash$getNbt();
//?} else if =1.16.1 {
//    @Accessor("nbt")
//    CompoundTag anticrash$getNbt();
//?}
}
