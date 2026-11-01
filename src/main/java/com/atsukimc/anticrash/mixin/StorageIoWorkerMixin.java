package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.checkpoint.WriteHold;
//? if <=1.21.11 {
import net.minecraft.world.storage.StorageIoWorker;
//?} else if >=26.1 {
//import net.minecraft.world.level.chunk.storage.IOWorker;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <=1.21.11 {
@Mixin(StorageIoWorker.class)
//?} else if >=26.1 {
//@Mixin(IOWorker.class)
//?}
public abstract class StorageIoWorkerMixin {
    /** Leaves the queued result in place; WriteHold restarts the writes when the cut ends. */
//? if <=1.21.11 {
    @Inject(method = "writeResult", at = @At("HEAD"), cancellable = true)
//?} else if >=26.1 {
//    @Inject(method = "storePendingChunk", at = @At("HEAD"), cancellable = true)
//?}
    private void anticrash$deferWriteDuringCut(CallbackInfo callbackInfo) {
//? if <=1.21.11 {
        if (WriteHold.defer((StorageIoWorker) (Object) this)) callbackInfo.cancel();
//?} else if >=26.1 {
//        if (WriteHold.defer((IOWorker) (Object) this)) callbackInfo.cancel();
//?}
    }
}
