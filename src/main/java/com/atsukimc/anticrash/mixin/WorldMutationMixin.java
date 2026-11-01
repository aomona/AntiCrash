package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.snapshot.logical.ChunkMutationTracker;
//? if <=1.21.11 {
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Covers World#markDirty, including piston progress that bypasses BlockEntity#markDirty. */
//? if <=1.21.11 {
@Mixin(World.class)
//?} else if >=26.1 {
//@Mixin(Level.class)
//?}
public abstract class WorldMutationMixin {
//? if <=1.21.11 {
    @Inject(method = "markDirty", at = @At("HEAD"))
//?} else if >=26.1 {
//    @Inject(method = "blockEntityChanged", at = @At("HEAD"))
//?}
    private void anticrash$worldDirty(BlockPos pos, CallbackInfo callbackInfo) {
        if (!com.atsukimc.anticrash.snapshot.logical.LogicalTracking.enabled()) return;
//? if <=1.21.11 {
        ChunkMutationTracker.record((World) (Object) this,
                new net.minecraft.util.math.ChunkPos(pos), "world.markDirty");
//?} else if >=26.1 {
//        ChunkMutationTracker.record((Level) (Object) this,
//                net.minecraft.world.level.ChunkPos.containing(pos), "world.markDirty");
//?}
    }
}
