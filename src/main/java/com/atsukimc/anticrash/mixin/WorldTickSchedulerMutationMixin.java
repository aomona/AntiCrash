package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.ScheduledTickMutationTracer;
import com.atsukimc.anticrash.snapshot.logical.ChunkMutationTracker;
//? if <=1.21.11 {
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.tick.ChunkTickScheduler;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.WorldTickScheduler;
//?} else if >=26.1 {
//import net.minecraft.core.Vec3i;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.levelgen.structure.BoundingBox;
//import net.minecraft.world.ticks.LevelChunkTicks;
//import net.minecraft.world.ticks.LevelTicks;
//import net.minecraft.world.ticks.ScheduledTick;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Observes Vanilla scheduler mutations without altering queue ownership or ordering. */
//? if <=1.21.11 {
@Mixin(WorldTickScheduler.class)
//?} else if >=26.1 {
//@Mixin(LevelTicks.class)
//?}
public abstract class WorldTickSchedulerMutationMixin {
//? if <=1.21.11 {
    @Inject(method = "scheduleTick", at = @At("HEAD"))
    private void anticrash$schedule(OrderedTick<?> tick, CallbackInfo callbackInfo) {
//?} else if >=26.1 {
//    @Inject(method = "schedule", at = @At("HEAD"))
//    private void anticrash$schedule(ScheduledTick<?> tick, CallbackInfo callbackInfo) {
//?}
        if (ScheduledTickMutationTracer.isActive()) {
            ScheduledTickMutationTracer.record("world.schedule",
                    "dimension=" + String.valueOf(ChunkMutationTracker.schedulerDimension(this))
                            + " position=" + tick.pos() + " tick=" + tick);
        }
        if (com.atsukimc.anticrash.snapshot.logical.LogicalTracking.enabled()) {
//? if <=1.21.11 {
            ChunkMutationTracker.recordScheduler(this, new ChunkPos(tick.pos()), "world.schedule");
//?} else if >=26.1 {
//            ChunkMutationTracker.recordScheduler(this, ChunkPos.containing(tick.pos()), "world.schedule");
//?}
        }
    }

//? if <=1.21.11 {
    @Inject(method = "addChunkTickScheduler", at = @At("HEAD"))
    private void anticrash$addChunk(ChunkPos pos, ChunkTickScheduler<?> scheduler,
//?} else if >=26.1 {
//    @Inject(method = "addContainer", at = @At("HEAD"))
//    private void anticrash$addChunk(ChunkPos pos, LevelChunkTicks<?> scheduler,
//?}
                                    CallbackInfo callbackInfo) {
        if (ScheduledTickMutationTracer.isActive()) {
            ScheduledTickMutationTracer.record("world.attach",
                    "dimension=" + String.valueOf(ChunkMutationTracker.schedulerDimension(this))
                            + " position=" + pos);
        }
        ChunkMutationTracker.recordScheduler(this, pos, "world.attach");
    }

//? if <=1.21.11 {
    @Inject(method = "removeChunkTickScheduler", at = @At("HEAD"))
//?} else if >=26.1 {
//    @Inject(method = "removeContainer", at = @At("HEAD"))
//?}
    private void anticrash$removeChunk(ChunkPos pos, CallbackInfo callbackInfo) {
        if (ScheduledTickMutationTracer.isActive()) {
            ScheduledTickMutationTracer.record("world.detach",
                    "dimension=" + String.valueOf(ChunkMutationTracker.schedulerDimension(this))
                            + " position=" + pos);
        }
        ChunkMutationTracker.recordScheduler(this, pos, "world.detach");
    }

    /** Box-wide command APIs can mutate many scheduler entries without exposing exact positions. */
//? if <=1.21.11 {
    @Inject(method = "clearNextTicks", at = @At("HEAD"))
    private void anticrash$clearNextTicks(BlockBox box, CallbackInfo callbackInfo) {
//?} else if >=26.1 {
//    @Inject(method = "clearArea", at = @At("HEAD"))
//    private void anticrash$clearNextTicks(BoundingBox box, CallbackInfo callbackInfo) {
//?}
        if (ScheduledTickMutationTracer.isActive()) {
            ScheduledTickMutationTracer.record("world.clearNextTicks",
                    "dimension=" + String.valueOf(ChunkMutationTracker.schedulerDimension(this))
                            + " box=" + box);
        }
        ChunkMutationTracker.markSchedulerUncertain("clearNextTicks");
    }

//? if (>=1.19.4 && <=1.21.11) {
    @Inject(method = "scheduleTicks(Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/Vec3i;)V", at = @At("HEAD"))
    private void anticrash$scheduleTicks(BlockBox box, Vec3i offset, CallbackInfo callbackInfo) {
//?} else if <=1.19.3 {
//    @Inject(method = "scheduleTicks", at = @At("HEAD"))
//    private void anticrash$scheduleTicks(BlockBox box, Vec3i offset, CallbackInfo callbackInfo) {
//?} else if >=26.1 {
//    @Inject(method = "copyArea(Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/core/Vec3i;)V", at = @At("HEAD"))
//    private void anticrash$scheduleTicks(BoundingBox box, Vec3i offset, CallbackInfo callbackInfo) {
//?}
        if (ScheduledTickMutationTracer.isActive()) {
            ScheduledTickMutationTracer.record("world.scheduleTicks",
                    "dimension=" + String.valueOf(ChunkMutationTracker.schedulerDimension(this))
                            + " box=" + box + " offset=" + offset);
        }
        ChunkMutationTracker.markSchedulerUncertain("scheduleTicks");
    }
//? if (>=1.19.4 && <=1.21.11) {

    @Inject(method = "scheduleTicks(Lnet/minecraft/world/tick/WorldTickScheduler;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/Vec3i;)V", at = @At("HEAD"))
    private void anticrash$copyTicks(WorldTickScheduler<?> source, BlockBox box, Vec3i offset, CallbackInfo ci) {
        // The three-argument API may be called directly by mods/clone commands.
        ChunkMutationTracker.markSchedulerUncertain("copy.scheduleTicks");
    }
//?} else if <=1.19.3 {
//?} else if >=26.1 {
//
//    @Inject(method = "copyAreaFrom(Lnet/minecraft/world/ticks/LevelTicks;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/core/Vec3i;)V", at = @At("HEAD"))
//    private void anticrash$copyTicks(LevelTicks<?> source, BoundingBox box, Vec3i offset, CallbackInfo ci) {
//        // The three-argument API may be called directly by mods/clone commands.
//        ChunkMutationTracker.markSchedulerUncertain("copy.scheduleTicks");
//    }
//?}
}
