//? if >=1.20 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.20 && <=1.21.11) {
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.gameevent.GameEvent;
//import net.minecraft.world.level.gameevent.PositionSource;
//import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
//import net.minecraft.world.phys.Vec3;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if (>=1.20 && <=1.21.11) {
@Mixin(Vibrations.VibrationListener.class)
//?} else if >=26.1 {
//@Mixin(VibrationSystem.Listener.class)
//?}
public abstract class VibrationListenerMutationMixin {
//? if (>=1.20.5 && <=1.21.11) {
    @Shadow public abstract PositionSource getPositionSource();
    @Inject(method = "listen(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/world/event/GameEvent$Emitter;Lnet/minecraft/util/math/Vec3d;)Z", at = @At("RETURN"))
    private void anticrash$dirtyListen(ServerWorld world, net.minecraft.registry.entry.RegistryEntry<GameEvent> event, GameEvent.Emitter emitter,
            Vec3d origin, CallbackInfoReturnable<Boolean> ci) {
        if (ci.getReturnValueZ()) getPositionSource().getPos(world).ifPresent(pos -> world.markDirty(BlockPos.ofFloored(pos)));
//?} else if (>=1.20 && <=1.20.4) {
//    @Shadow public abstract PositionSource getPositionSource();
//    @Inject(method = "listen(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/world/event/GameEvent$Emitter;Lnet/minecraft/util/math/Vec3d;)Z", at = @At("RETURN"))
//    private void anticrash$dirtyListen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter,
//            Vec3d origin, CallbackInfoReturnable<Boolean> ci) {
//        if (ci.getReturnValueZ()) getPositionSource().getPos(world).ifPresent(pos -> world.markDirty(BlockPos.ofFloored(pos)));
//?} else if >=26.1 {
//    @Shadow public abstract PositionSource getListenerSource();
//    @Inject(method = "handleGameEvent(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/gameevent/GameEvent$Context;Lnet/minecraft/world/phys/Vec3;)Z", at = @At("RETURN"))
//    private void anticrash$dirtyListen(ServerLevel world, net.minecraft.core.Holder<GameEvent> event, GameEvent.Context emitter,
//            Vec3 origin, CallbackInfoReturnable<Boolean> ci) {
//        if (ci.getReturnValueZ()) getListenerSource().getPosition(world).ifPresent(pos -> world.blockEntityChanged(BlockPos.containing(pos)));
//?}
    }
//? if (>=1.20.5 && <=1.21.11) {
    @Inject(method = "forceListen", at = @At("TAIL"))
    private void anticrash$dirtyForcedListen(ServerWorld world, net.minecraft.registry.entry.RegistryEntry<GameEvent> event, GameEvent.Emitter emitter,
            Vec3d origin, CallbackInfo ci) {
        getPositionSource().getPos(world).ifPresent(pos -> world.markDirty(BlockPos.ofFloored(pos)));
//?} else if (>=1.20 && <=1.20.4) {
//    @Inject(method = "forceListen", at = @At("TAIL"))
//    private void anticrash$dirtyForcedListen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter,
//            Vec3d origin, CallbackInfo ci) {
//        getPositionSource().getPos(world).ifPresent(pos -> world.markDirty(BlockPos.ofFloored(pos)));
//?} else if >=26.1 {
//    @Inject(method = "forceScheduleVibration", at = @At("TAIL"))
//    private void anticrash$dirtyForcedListen(ServerLevel world, net.minecraft.core.Holder<GameEvent> event, GameEvent.Context emitter,
//            Vec3 origin, CallbackInfo ci) {
//        getListenerSource().getPosition(world).ifPresent(pos -> world.blockEntityChanged(BlockPos.containing(pos)));
//?}
    }
}
//?}
