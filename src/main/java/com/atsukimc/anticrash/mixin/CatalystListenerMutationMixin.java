//? if >=1.20 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.20 && <=1.21.11) {
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
//import net.minecraft.world.level.gameevent.GameEvent;
//import net.minecraft.world.level.gameevent.PositionSource;
//import net.minecraft.world.phys.Vec3;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if (>=1.20 && <=1.21.11) {
@Mixin(SculkCatalystBlockEntity.Listener.class)
//?} else if >=26.1 {
//@Mixin(SculkCatalystBlockEntity.CatalystListener.class)
//?}
public abstract class CatalystListenerMutationMixin {
//? if (>=1.20.5 && <=1.21.11) {
    @Shadow public abstract PositionSource getPositionSource();
    @Inject(method = "listen", at = @At("RETURN"))
    private void anticrash$dirtyCursor(ServerWorld world, net.minecraft.registry.entry.RegistryEntry<GameEvent> event, GameEvent.Emitter emitter,
            Vec3d origin, CallbackInfoReturnable<Boolean> ci) {
        if (ci.getReturnValueZ()) getPositionSource().getPos(world).ifPresent(pos -> world.markDirty(BlockPos.ofFloored(pos)));
//?} else if (>=1.20 && <=1.20.4) {
//    @Shadow public abstract PositionSource getPositionSource();
//    @Inject(method = "listen", at = @At("RETURN"))
//    private void anticrash$dirtyCursor(ServerWorld world, GameEvent event, GameEvent.Emitter emitter,
//            Vec3d origin, CallbackInfoReturnable<Boolean> ci) {
//        if (ci.getReturnValueZ()) getPositionSource().getPos(world).ifPresent(pos -> world.markDirty(BlockPos.ofFloored(pos)));
//?} else if >=26.1 {
//    @Shadow public abstract PositionSource getListenerSource();
//    @Inject(method = "handleGameEvent", at = @At("RETURN"))
//    private void anticrash$dirtyCursor(ServerLevel world, net.minecraft.core.Holder<GameEvent> event, GameEvent.Context emitter,
//            Vec3 origin, CallbackInfoReturnable<Boolean> ci) {
//        if (ci.getReturnValueZ()) getListenerSource().getPosition(world).ifPresent(pos -> world.blockEntityChanged(BlockPos.containing(pos)));
//?}
    }
}
//?}
