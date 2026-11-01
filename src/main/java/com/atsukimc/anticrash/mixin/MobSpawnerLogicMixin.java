package com.atsukimc.anticrash.mixin;

//? if (>=1.20.3 && <=1.21.11) {
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.spawner.MobSpawnerLogic;
//?} else if <=1.20.2 {
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.MobSpawnerLogic;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.BaseSpawner;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The spawner {@code Delay} changes while a player is in range without a chunk save mark. */
//? if <=1.21.11 {
@Mixin(MobSpawnerLogic.class)
//?} else if >=26.1 {
//@Mixin(BaseSpawner.class)
//?}
public abstract class MobSpawnerLogicMixin {
    @Shadow private int spawnDelay;
    @Unique private int anticrash$delayBefore;

    @Inject(method = "serverTick", at = @At("HEAD"))
//? if <=1.21.11 {
    private void anticrash$rememberDelay(ServerWorld world, BlockPos pos, CallbackInfo callbackInfo) {
//?} else if >=26.1 {
//    private void anticrash$rememberDelay(ServerLevel world, BlockPos pos, CallbackInfo callbackInfo) {
//?}
        anticrash$delayBefore = spawnDelay;
    }

    @Inject(method = "serverTick", at = @At("RETURN"))
//? if <=1.21.11 {
    private void anticrash$markDelayDirty(ServerWorld world, BlockPos pos, CallbackInfo callbackInfo) {
        if (spawnDelay != anticrash$delayBefore) world.markDirty(pos);
//?} else if >=26.1 {
//    private void anticrash$markDelayDirty(ServerLevel world, BlockPos pos, CallbackInfo callbackInfo) {
//        if (spawnDelay != anticrash$delayBefore) world.blockEntityChanged(pos);
//?}
    }
}
