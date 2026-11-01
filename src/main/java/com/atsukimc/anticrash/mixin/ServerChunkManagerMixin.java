package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.checkpoint.CutSave;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.atsukimc.anticrash.AntiCrashController;
import com.atsukimc.anticrash.AntiCrashMod;
//? if <=1.21.11 {
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.SpawnDensityCapper;
import net.minecraft.world.SpawnHelper;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.server.level.ServerChunkCache;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.LocalMobCapCalculator;
//import net.minecraft.world.level.NaturalSpawner;
//?}

/**
 * Keeps optional consumers such as Carpet's mobcap HUD safe while AntiCrash is
 * deliberately preventing the first world tick after a recovery.
 *
 * <p>Vanilla initializes {@code ServerChunkManager.spawnInfo} from
 * {@code tickChunks}. Recovery freeze must not run that method just to satisfy
 * an observer, because doing so would advance the restored world. Instead this
 * mixin supplies an immutable, empty view until Vanilla has produced the real
 * value. The backing field itself is never changed.</p>
 */
//? if <=1.21.11 {
@Mixin(ServerChunkManager.class)
//?} else if >=26.1 {
//@Mixin(ServerChunkCache.class)
//?}
public abstract class ServerChunkManagerMixin {
//? if =1.16.1 || (>=1.18.2 && <=26.2) {
//?} else if (>=1.18 && <=1.18.1) {
//    @WrapOperation(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE",
//            target = "Lnet/minecraft/server/world/ServerChunkManager;tickChunks()V"))
//    private void anticrash$skipSimulationInLifecycleScope(ServerChunkManager manager,
//            Operation<Void> original) {
//        if (!com.atsukimc.anticrash.recovery.VersionChunkLifecycle.simulationDisabled()) {
//            original.call(manager);
//        }
//    }
//
//?} else if =26.3 {
//    @org.spongepowered.asm.mixin.Shadow @org.spongepowered.asm.mixin.Final
//    private net.minecraft.server.level.ServerLevel level;
//?}
    @Unique
//? if <=1.21.11 {
    private SpawnHelper.Info anticrash$emptySpawnInfo;
//?} else if >=26.1 {
//    private NaturalSpawner.SpawnState anticrash$emptySpawnInfo;
//?}

//? if <=1.21.11 {
    @Inject(method = "getSpawnInfo", at = @At("RETURN"), cancellable = true)
//?} else if >=26.1 {
//    @Inject(method = "getLastSpawnState", at = @At("RETURN"), cancellable = true)
//?}
    private void anticrash$nonNullSpawnInfoDuringRecovery(
//? if <=1.21.11 {
            CallbackInfoReturnable<SpawnHelper.Info> callbackInfo) {
//?} else if >=26.1 {
//            CallbackInfoReturnable<NaturalSpawner.SpawnState> callbackInfo) {
//?}
        if (callbackInfo.getReturnValue() != null) return;

        AntiCrashController controller = AntiCrashMod.controller();
        if (controller == null || !controller.needsSpawnInfoFallback()) return;

        if (anticrash$emptySpawnInfo == null) {
//? if <=1.21.11 {
            ServerChunkManager manager = (ServerChunkManager) (Object) this;
            anticrash$emptySpawnInfo = SpawnHelper.setupSpawn(
//?} else if >=26.1 {
//            ServerChunkCache manager = (ServerChunkCache) (Object) this;
//            anticrash$emptySpawnInfo = NaturalSpawner.createState(
//?}
                    0,
//? if <=26.2 {
                    List.<Entity>of(),
//?} else if =26.3 {
//                    level,
//?}
                    (chunkPos, consumer) -> { },
//? if (>=1.21 && <=1.21.11) {
                    new SpawnDensityCapper(manager.chunkLoadingManager));
//?} else if <=1.20.6 {
//                    new SpawnDensityCapper(manager.threadedAnvilChunkStorage));
//?} else if >=26.1 {
//                    new LocalMobCapCalculator(manager.chunkMap));
//?}
        }
        callbackInfo.setReturnValue(anticrash$emptySpawnInfo);
    }

    /**
     * {@code save(boolean)} first processes pending ticket changes, which loads, promotes or
     * demotes chunks. At a checkpoint cut that would change the world after its Runtime Capsule
     * was taken; the changes are applied by the next tick exactly as they would have been.
     */
    @WrapOperation(method = "save", at = @At(value = "INVOKE",
//? if (>=1.20.2 && <=1.21.11) {
            target = "Lnet/minecraft/server/world/ServerChunkManager;updateChunks()Z"))
    private boolean anticrash$keepTicketsAtCut(ServerChunkManager manager, Operation<Boolean> original) {
//?} else if <=1.20.1 {
//            target = "Lnet/minecraft/server/world/ServerChunkManager;tick()Z"))
//    private boolean anticrash$keepTicketsAtCut(ServerChunkManager manager, Operation<Boolean> original) {
//?} else if >=26.1 {
//            target = "Lnet/minecraft/server/level/ServerChunkCache;runDistanceManagerUpdates()Z"))
//    private boolean anticrash$keepTicketsAtCut(ServerChunkCache manager, Operation<Boolean> original) {
//?}
        if (CutSave.active()) return false;
        return original.call(manager);
    }
}
