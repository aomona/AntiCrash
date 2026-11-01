package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.RuntimeExtensionNbt;
//? if (>=1.18 && <=1.21.11) {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//?} else if =1.16.1 {
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.PistonBlockEntity;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <=1.21.11 {
@Mixin(PistonBlockEntity.class)
//?} else if >=26.1 {
//@Mixin(PistonMovingBlockEntity.class)
//?}
public abstract class PistonBlockEntityMixin {
    @Shadow private float progress;
//? if <=1.21.11 {
    @Shadow private float lastProgress;
    @Shadow private long savedWorldTime;
//?} else if >=26.1 {
//    @Shadow private float progressO;
//    @Shadow private long lastTicked;
//?}

    /**
     * Vanilla advances piston progress without notifying the owning chunk that its block entity
     * data changed. Keep ordinary saves and AntiCrash checkpoints from retaining stale progress.
     */
    @Inject(method = "tick", at = @At("TAIL"))
//? if <=1.21.11 {
    private static void anticrash$markChunkDirty(World world, BlockPos pos, BlockState state,
                                                  PistonBlockEntity piston, CallbackInfo callbackInfo) {
//?} else if >=26.1 {
//    private static void anticrash$markChunkDirty(Level world, BlockPos pos, BlockState state,
//                                                  PistonMovingBlockEntity piston, CallbackInfo callbackInfo) {
//?}
        // World#markDirty only sets the loaded chunk's needsSaving flag. Calling
        // BlockEntity#markDirty here would also run comparator updates on every piston tick.
//? if <=1.21.11 {
        world.markDirty(pos);
//?} else if >=26.1 {
//        world.blockEntityChanged(pos);
//?}
    }

    /**
     * Vanilla saves {@code lastProgress} under "progress" and loads it into both fields, so a
     * piston that was half way (progress 0.5, lastProgress 0.0) or about to finish (1.0, 0.5)
     * restarts half a step behind and arrives one tick late. That one tick stops every flying
     * machine caught mid-move. {@code lastProgress} stays Vanilla's; only the real progress and
     * the tick it was last advanced in ({@code savedWorldTime}, which PistonBlock compares with
     * the world time when a piston is retracted again) are added.
     */
//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method = "writeData", at = @At("TAIL"))
    private void anticrash$saveExactProgress(net.minecraft.storage.WriteView nbt, CallbackInfo callbackInfo) {
//?} else if (>=1.18 && <=1.20.4) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveExactProgress(NbtCompound nbt, CallbackInfo callbackInfo) {
//?} else if (>=1.20.5 && <=1.21.5) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveExactProgress(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo callbackInfo) {
//?} else if >=26.1 {
//    @Inject(method = "saveAdditional", at = @At("TAIL"))
//    private void anticrash$saveExactProgress(net.minecraft.world.level.storage.ValueOutput nbt, CallbackInfo callbackInfo) {
//?} else if =1.16.1 {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveExactProgress(CompoundTag nbt, CallbackInfo callbackInfo) {
//?}
        if (!RuntimeExtensionNbt.savingLoadedChunk()) return;
        RuntimeExtensionNbt.write(nbt, extension -> {
            extension.putFloat("Progress", progress);
//? if <=1.21.11 {
            extension.putLong("SavedWorldTime", savedWorldTime);
//?} else if >=26.1 {
//            extension.putLong("SavedWorldTime", lastTicked);
//?}
        });
    }

//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method = "readData", at = @At("TAIL"))
    private void anticrash$loadExactProgress(net.minecraft.storage.ReadView nbt, CallbackInfo callbackInfo) {
        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
        if (extension == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "Progress", 99)) return;
        float exact = extension.getFloat("Progress").orElseThrow();
//?} else if (>=1.18 && <=1.20.4) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadExactProgress(NbtCompound nbt, CallbackInfo callbackInfo) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !extension.contains("Progress", 99)) return;
//        float exact = extension.getFloat("Progress");
//?} else if (>=1.20.5 && <=1.21.4) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadExactProgress(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo callbackInfo) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !extension.contains("Progress", 99)) return;
//        float exact = extension.getFloat("Progress");
//?} else if >=26.1 {
//    @Inject(method = "loadAdditional", at = @At("TAIL"))
//    private void anticrash$loadExactProgress(net.minecraft.world.level.storage.ValueInput nbt, CallbackInfo callbackInfo) {
//        CompoundTag extension = RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "Progress", 99)) return;
//        float exact = extension.getFloat("Progress").orElseThrow();
//?} else if =1.16.1 {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadExactProgress(CompoundTag nbt, CallbackInfo callbackInfo) {
//        CompoundTag extension = RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !extension.contains("Progress", 99)) return;
//        float exact = extension.getFloat("Progress");
//?} else if =1.21.5 {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadExactProgress(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo callbackInfo) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "Progress", 99)) return;
//        float exact = extension.getFloat("Progress").orElseThrow();
//?}
        // Progress only moves forward from lastProgress to 1; anything else is not a state the
        // game can be in, so Vanilla's own value is kept.
//? if (>=1.21.5 && <=1.21.11) {
        if (exact >= lastProgress && exact <= 1.0f) progress = exact;
        if (com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "SavedWorldTime", 99)) savedWorldTime = extension.getLong("SavedWorldTime").orElseThrow();
//?} else if <=1.21.4 {
//        if (exact >= lastProgress && exact <= 1.0f) progress = exact;
//        if (extension.contains("SavedWorldTime", 99)) savedWorldTime = extension.getLong("SavedWorldTime");
//?} else if >=26.1 {
//        if (exact >= progressO && exact <= 1.0f) progress = exact;
//        if (com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "SavedWorldTime", 99)) lastTicked = extension.getLong("SavedWorldTime").orElseThrow();
//?}
    }
}
