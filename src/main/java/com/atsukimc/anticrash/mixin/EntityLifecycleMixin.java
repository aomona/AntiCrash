package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.snapshot.logical.EntityLifecycleTracker;
//? if <=1.21.11 {
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.util.math.ChunkPos;
//?} else if >=26.1 {
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.Entity.RemovalReason;
//import net.minecraft.world.level.ChunkPos;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Observes removal and chunk-crossing edges without replacing EntityList or ServerEntityManager. */
@Mixin(Entity.class)
public abstract class EntityLifecycleMixin {
//? if <=1.21.11 {
    @Inject(method = "remove(Lnet/minecraft/entity/Entity$RemovalReason;)V", at = @At("HEAD"))
//?} else if >=26.1 {
//    @Inject(method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("HEAD"))
//?}
    private void anticrash$removed(RemovalReason reason, CallbackInfo callbackInfo) {
        EntityLifecycleTracker.recordRemoval((Entity) (Object) this,
                reason == null ? "unknown" : reason.name());
    }

//? if <=1.21.11 {
    @Inject(method = "setPosition(DDD)V", at = @At("HEAD"))
//?} else if >=26.1 {
//    @Inject(method = "setPos(DDD)V", at = @At("HEAD"))
//?}
    private void anticrash$setPosition(double x, double y, double z, CallbackInfo callbackInfo) {
        recordMove(x, z);
    }

//? if <=1.21.11 {
    @Inject(method = "refreshPositionAndAngles(DDDFF)V", at = @At("HEAD"))
//?} else if >=26.1 {
//    @Inject(method = "snapTo(DDDFF)V", at = @At("HEAD"))
//?}
    private void anticrash$refreshPosition(double x, double y, double z, float yaw, float pitch,
                                           CallbackInfo callbackInfo) {
        recordMove(x, z);
    }

    private void recordMove(double x, double z) {
        // Runs for every entity movement; return before any allocation when nothing records it.
        if (!com.atsukimc.anticrash.snapshot.logical.LogicalTracking.enabled()) return;
        Entity entity = (Entity) (Object) this;
//? if <=1.21.8 {
        if (entity.getWorld() == null) return;
        long oldChunk = entity.getChunkPos().toLong();
        long newChunk = ChunkPos.toLong(net.minecraft.util.math.MathHelper.floor(x) >> 4,
                net.minecraft.util.math.MathHelper.floor(z) >> 4);
//?} else if (>=1.21.9 && <=1.21.11) {
//        if (entity.getEntityWorld() == null) return;
//        long oldChunk = entity.getChunkPos().toLong();
//        long newChunk = ChunkPos.toLong(net.minecraft.util.math.MathHelper.floor(x) >> 4,
//                net.minecraft.util.math.MathHelper.floor(z) >> 4);
//?} else if >=26.1 {
//        if (entity.level() == null) return;
//        long oldChunk = entity.chunkPosition().pack();
//        long newChunk = ChunkPos.pack(net.minecraft.util.Mth.floor(x) >> 4,
//                net.minecraft.util.Mth.floor(z) >> 4);
//?}
        EntityLifecycleTracker.recordMove(entity, oldChunk, newChunk);
    }
}
