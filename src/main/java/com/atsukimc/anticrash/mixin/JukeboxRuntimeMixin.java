//? if >=1.19.1 {
package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.RuntimeExtensionNbt;
//? if (>=1.19.1 && <=1.21.11) {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21: Vanilla saves ticks_since_song_started, including the JUKEBOX_PLAY modulo-20 phase. */
@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxRuntimeMixin {
//? if (>=1.21 && <=1.21.8) {
    @Inject(method = "tick", at = @At("HEAD"))
    private static void anticrash$markPlaybackTick(World world, BlockPos pos, BlockState state,
                                                   JukeboxBlockEntity jukebox, CallbackInfo ci) {
        if (!world.isClient && jukebox.getManager().getSong() != null) world.markDirty(pos);
//?} else if (>=1.19.4 && <=1.20.4) {
//    @Shadow private int ticksThisSecond;
//
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveEventPhase(NbtCompound nbt, CallbackInfo ci) {
//        if (!RuntimeExtensionNbt.savingLoadedChunk()) return;
//        RuntimeExtensionNbt.write(nbt, extension -> extension.putInt("JukeboxEventPhase", ticksThisSecond));
//    }
//
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadEventPhase(NbtCompound nbt, CallbackInfo ci) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension != null && extension.contains("JukeboxEventPhase", 3)) {
//            ticksThisSecond = extension.getInt("JukeboxEventPhase");
//        }
//    }
//
//    @Inject(method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("HEAD"))
//    private void anticrash$markPlaybackTick(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
//        if (!world.isClient) world.markDirty(pos);
//?} else if (>=1.19.1 && <=1.19.3) {
//    @Shadow private int ticksThisSecond;
//
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveEventPhase(NbtCompound nbt, CallbackInfo ci) {
//        if (!RuntimeExtensionNbt.savingLoadedChunk()) return;
//        RuntimeExtensionNbt.write(nbt, extension -> extension.putInt("JukeboxEventPhase", ticksThisSecond));
//    }
//
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadEventPhase(NbtCompound nbt, CallbackInfo ci) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension != null && extension.contains("JukeboxEventPhase", 3)) {
//            ticksThisSecond = extension.getInt("JukeboxEventPhase");
//        }
//    }
//
//    @Inject(method = "tick", at = @At("HEAD"))
//    private static void anticrash$markPlaybackTick(World world, BlockPos pos, BlockState state,
//            JukeboxBlockEntity jukebox, CallbackInfo ci) {
//        if (!world.isClient) world.markDirty(pos);
//?} else if (>=1.21.9 && <=1.21.11) {
//    @Inject(method = "tick", at = @At("HEAD"))
//    private static void anticrash$markPlaybackTick(World world, BlockPos pos, BlockState state,
//                                                   JukeboxBlockEntity jukebox, CallbackInfo ci) {
//        if (!world.isClient() && jukebox.getManager().getSong() != null) world.markDirty(pos);
//?} else if >=26.1 {
//    @Inject(method = "tick", at = @At("HEAD"))
//    private static void anticrash$markPlaybackTick(Level world, BlockPos pos, BlockState state,
//                                                   JukeboxBlockEntity jukebox, CallbackInfo ci) {
//        if (!world.isClientSide() && jukebox.getSongPlayer().getSong() != null) world.blockEntityChanged(pos);
//?} else if (>=1.20.5 && <=1.20.6) {
//    @Shadow private int ticksThisSecond;
//
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveEventPhase(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
//        if (!RuntimeExtensionNbt.savingLoadedChunk()) return;
//        RuntimeExtensionNbt.write(nbt, extension -> extension.putInt("JukeboxEventPhase", ticksThisSecond));
//    }
//
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadEventPhase(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension != null && extension.contains("JukeboxEventPhase", 3)) {
//            ticksThisSecond = extension.getInt("JukeboxEventPhase");
//        }
//    }
//
//    @Inject(method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("HEAD"))
//    private void anticrash$markPlaybackTick(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
//        if (!world.isClient) world.markDirty(pos);
//?}
    }
}
//?}
