//? if >=1.20 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.20 && <=1.21.11) {
import net.minecraft.world.World;
import net.minecraft.world.event.Vibrations;
import net.minecraft.util.math.BlockPos;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20 separates ListenerData/Ticker/Callback; countdown and pending selector must be dirty. */
//? if (>=1.20 && <=1.21.11) {
@Mixin(Vibrations.Ticker.class)
//?} else if >=26.1 {
//@Mixin(VibrationSystem.Ticker.class)
//?}
public interface VibrationTickerMutationMixin {
    @Inject(method = "tick", at = @At("HEAD"))
//? if (>=1.20 && <=1.21.8) {
    private static void anticrash$dirtyVibrationTick(World world, Vibrations.ListenerData data,
            Vibrations.Callback callback, CallbackInfo ci) {
        if (!world.isClient) callback.getPositionSource().getPos(world)
                .ifPresent(pos -> world.markDirty(BlockPos.ofFloored(pos)));
//?} else if (>=1.21.9 && <=1.21.11) {
//    private static void anticrash$dirtyVibrationTick(World world, Vibrations.ListenerData data,
//            Vibrations.Callback callback, CallbackInfo ci) {
//        if (!world.isClient()) callback.getPositionSource().getPos(world)
//                .ifPresent(pos -> world.markDirty(BlockPos.ofFloored(pos)));
//?} else if >=26.1 {
//    private static void anticrash$dirtyVibrationTick(Level world, VibrationSystem.Data data,
//            VibrationSystem.User callback, CallbackInfo ci) {
//        if (!world.isClientSide()) callback.getPositionSource().getPosition(world)
//                .ifPresent(pos -> world.blockEntityChanged(BlockPos.containing(pos)));
//?}
    }
}
//?}
