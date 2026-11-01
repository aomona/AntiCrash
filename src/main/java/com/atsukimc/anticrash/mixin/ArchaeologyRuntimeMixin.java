//? if >=1.19.4 {
package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.RuntimeExtensionNbt;
//? if (>=1.20 && <=1.21.11) {
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
//?} else if >=26.1 {
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.block.entity.BrushableBlockEntity;
//?} else if =1.19.4 {
//import net.minecraft.block.entity.SuspiciousSandBlockEntity;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.util.math.Direction;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.19.4 preview archaeology omits brushing progress, cooldowns and direction from writeNbt. */
//? if >=1.20 {
@Mixin(BrushableBlockEntity.class)
//?} else if =1.19.4 {
//@Mixin(SuspiciousSandBlockEntity.class)
//?}
public abstract class ArchaeologyRuntimeMixin {
//? if (>=1.19.4 && <=1.21.11) {
    @Shadow private int brushesCount;
    @Shadow private long nextDustTime;
    @Shadow private long nextBrushTime;
//?} else if >=26.1 {
//    @Shadow private int brushCount;
//    @Shadow private long brushCountResetsAtTick;
//    @Shadow private long coolDownEndsAtTick;
//?}
    @Shadow private Direction hitDirection;
//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method = "writeData", at = @At("TAIL"))
    private void anticrash$saveBrush(net.minecraft.storage.WriteView nbt, CallbackInfo ci) {
//?} else if (>=1.20.5 && <=1.21.5) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveBrush(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
//?} else if (>=1.19.4 && <=1.20.4) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveBrush(NbtCompound nbt, CallbackInfo ci) {
//?} else if >=26.1 {
//    @Inject(method = "saveAdditional", at = @At("TAIL"))
//    private void anticrash$saveBrush(net.minecraft.world.level.storage.ValueOutput nbt, CallbackInfo ci) {
//?}
        if (!RuntimeExtensionNbt.savingLoadedChunk()) return;
        RuntimeExtensionNbt.write(nbt, extension -> {
//? if (>=1.21.5 && <=1.21.11) {
            extension.putInt("BrushCount", brushesCount);
            extension.putLong("BrushNextDust", nextDustTime);
            extension.putLong("BrushNextUse", nextBrushTime);
            extension.putInt("BrushDirection", hitDirection == null ? -1 : hitDirection.getIndex());
//?} else if (>=1.19.4 && <=1.21.4) {
//            extension.putInt("BrushCount", brushesCount);
//            extension.putLong("BrushNextDust", nextDustTime);
//            extension.putLong("BrushNextUse", nextBrushTime);
//            extension.putInt("BrushDirection", hitDirection == null ? -1 : hitDirection.getId());
//?} else if >=26.1 {
//            extension.putInt("BrushCount", brushCount);
//            extension.putLong("BrushNextDust", brushCountResetsAtTick);
//            extension.putLong("BrushNextUse", coolDownEndsAtTick);
//            extension.putInt("BrushDirection", hitDirection == null ? -1 : hitDirection.get3DDataValue());
//?}
        });
    }
//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method = "readData", at = @At("TAIL"))
    private void anticrash$loadBrush(net.minecraft.storage.ReadView nbt, CallbackInfo ci) {
        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
        if (extension == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "BrushCount", 3)) return;
        brushesCount = extension.getInt("BrushCount").orElseThrow();
        nextDustTime = extension.getLong("BrushNextDust").orElseThrow();
        nextBrushTime = extension.getLong("BrushNextUse").orElseThrow();
        int direction = extension.getInt("BrushDirection").orElseThrow();
//?} else if (>=1.20.5 && <=1.21.4) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadBrush(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !extension.contains("BrushCount", 3)) return;
//        brushesCount = extension.getInt("BrushCount");
//        nextDustTime = extension.getLong("BrushNextDust");
//        nextBrushTime = extension.getLong("BrushNextUse");
//        int direction = extension.getInt("BrushDirection");
//?} else if (>=1.19.4 && <=1.20.4) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadBrush(NbtCompound nbt, CallbackInfo ci) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !extension.contains("BrushCount", 3)) return;
//        brushesCount = extension.getInt("BrushCount");
//        nextDustTime = extension.getLong("BrushNextDust");
//        nextBrushTime = extension.getLong("BrushNextUse");
//        int direction = extension.getInt("BrushDirection");
//?} else if >=26.1 {
//    @Inject(method = "loadAdditional", at = @At("TAIL"))
//    private void anticrash$loadBrush(net.minecraft.world.level.storage.ValueInput nbt, CallbackInfo ci) {
//        CompoundTag extension = RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "BrushCount", 3)) return;
//        brushCount = extension.getInt("BrushCount").orElseThrow();
//        brushCountResetsAtTick = extension.getLong("BrushNextDust").orElseThrow();
//        coolDownEndsAtTick = extension.getLong("BrushNextUse").orElseThrow();
//        int direction = extension.getInt("BrushDirection").orElseThrow();
//?} else if =1.21.5 {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadBrush(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "BrushCount", 3)) return;
//        brushesCount = extension.getInt("BrushCount").orElseThrow();
//        nextDustTime = extension.getLong("BrushNextDust").orElseThrow();
//        nextBrushTime = extension.getLong("BrushNextUse").orElseThrow();
//        int direction = extension.getInt("BrushDirection").orElseThrow();
//?}
        if (direction < -1 || direction > 5) throw new IllegalStateException("Invalid saved brush direction");
//? if (>=1.21.5 && <=1.21.11) {
        hitDirection = direction == -1 ? null : Direction.byIndex(direction);
//?} else if (>=1.19.4 && <=1.21.4) {
//        hitDirection = direction == -1 ? null : Direction.byId(direction);
//?} else if >=26.1 {
//        hitDirection = direction == -1 ? null : Direction.from3DDataValue(direction);
//?}
    }
//? if (>=1.19.4 && <=1.21.11) {
    @Inject(method = "scheduledTick", at = @At("HEAD"))
//?} else if >=26.1 {
//    @Inject(method = "checkReset", at = @At("HEAD"))
//?}
    private void anticrash$dirtyDecay(CallbackInfo ci) {
//? if (>=1.20 && <=1.21.11) {
        ((BrushableBlockEntity)(Object)this).markDirty();
//?} else if >=26.1 {
//        ((BrushableBlockEntity)(Object)this).setChanged();
//?} else if =1.19.4 {
//        ((SuspiciousSandBlockEntity)(Object)this).markDirty();
//?}
    }
    @Inject(method = "brush", at = @At("RETURN"))
    private void anticrash$dirtyBrush(CallbackInfoReturnable<Boolean> ci) {
//? if (>=1.20 && <=1.21.11) {
        ((BrushableBlockEntity)(Object)this).markDirty();
//?} else if >=26.1 {
//        ((BrushableBlockEntity)(Object)this).setChanged();
//?} else if =1.19.4 {
//        ((SuspiciousSandBlockEntity)(Object)this).markDirty();
//?}
    }
}
//?}
