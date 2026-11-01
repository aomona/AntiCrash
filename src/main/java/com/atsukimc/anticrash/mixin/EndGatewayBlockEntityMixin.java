package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.RuntimeExtensionNbt;
//? if (>=1.18 && <=1.21.11) {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//?} else if =1.16.1 {
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.EndGatewayBlockEntity;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <=1.21.11 {
@Mixin(EndGatewayBlockEntity.class)
//?} else if >=26.1 {
//@Mixin(TheEndGatewayBlockEntity.class)
//?}
public abstract class EndGatewayBlockEntityMixin {
    @Shadow private int teleportCooldown;

    /**
     * "Age" is saved by Vanilla but advances every tick without the chunk being marked for
     * saving, so a checkpoint kept whatever age the chunk was last saved with. Every 2400 ticks
     * of age the gateway starts a 40-tick teleport cooldown; a stale age moves that moment.
     */
//? if <=1.21.11 {
    @Inject(method = "serverTick", at = @At("TAIL"))
    private static void anticrash$markChunkDirty(World world, BlockPos pos, BlockState state,
                                                  EndGatewayBlockEntity gateway, CallbackInfo callbackInfo) {
        world.markDirty(pos);
//?} else if >=26.1 {
//    @Inject(method = "portalTick", at = @At("TAIL"))
//    private static void anticrash$markChunkDirty(Level world, BlockPos pos, BlockState state,
//                                                  TheEndGatewayBlockEntity gateway, CallbackInfo callbackInfo) {
//        world.blockEntityChanged(pos);
//?}
    }

    /** The cooldown itself is not saved: after a restart a gateway on cooldown teleports at once. */
//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method = "writeData", at = @At("TAIL"))
    private void anticrash$saveTeleportCooldown(net.minecraft.storage.WriteView nbt, CallbackInfo callbackInfo) {
//?} else if (>=1.18 && <=1.20.4) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveTeleportCooldown(NbtCompound nbt, CallbackInfo callbackInfo) {
//?} else if (>=1.20.5 && <=1.21.5) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveTeleportCooldown(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo callbackInfo) {
//?} else if >=26.1 {
//    @Inject(method = "saveAdditional", at = @At("TAIL"))
//    private void anticrash$saveTeleportCooldown(net.minecraft.world.level.storage.ValueOutput nbt, CallbackInfo callbackInfo) {
//?} else if =1.16.1 {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveTeleportCooldown(CompoundTag nbt, CallbackInfo callbackInfo) {
//?}
        if (!RuntimeExtensionNbt.savingLoadedChunk()) return;
        RuntimeExtensionNbt.write(nbt, extension -> {
            if (teleportCooldown > 0) extension.putInt("TeleportCooldown", teleportCooldown);
            else extension.remove("TeleportCooldown");
        });
    }

//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method = "readData", at = @At("TAIL"))
    private void anticrash$loadTeleportCooldown(net.minecraft.storage.ReadView nbt, CallbackInfo callbackInfo) {
        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
        if (extension != null && com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "TeleportCooldown", 99)) {
            teleportCooldown = Math.max(0, extension.getInt("TeleportCooldown").orElseThrow());
//?} else if (>=1.18 && <=1.20.4) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadTeleportCooldown(NbtCompound nbt, CallbackInfo callbackInfo) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension != null && extension.contains("TeleportCooldown", 99)) {
//            teleportCooldown = Math.max(0, extension.getInt("TeleportCooldown"));
//?} else if (>=1.20.5 && <=1.21.4) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadTeleportCooldown(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo callbackInfo) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension != null && extension.contains("TeleportCooldown", 99)) {
//            teleportCooldown = Math.max(0, extension.getInt("TeleportCooldown"));
//?} else if >=26.1 {
//    @Inject(method = "loadAdditional", at = @At("TAIL"))
//    private void anticrash$loadTeleportCooldown(net.minecraft.world.level.storage.ValueInput nbt, CallbackInfo callbackInfo) {
//        CompoundTag extension = RuntimeExtensionNbt.read(nbt);
//        if (extension != null && com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "TeleportCooldown", 99)) {
//            teleportCooldown = Math.max(0, extension.getInt("TeleportCooldown").orElseThrow());
//?} else if =1.16.1 {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadTeleportCooldown(CompoundTag nbt, CallbackInfo callbackInfo) {
//        CompoundTag extension = RuntimeExtensionNbt.read(nbt);
//        if (extension != null && extension.contains("TeleportCooldown", 99)) {
//            teleportCooldown = Math.max(0, extension.getInt("TeleportCooldown"));
//?} else if =1.21.5 {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadTeleportCooldown(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo callbackInfo) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//        if (extension != null && com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "TeleportCooldown", 99)) {
//            teleportCooldown = Math.max(0, extension.getInt("TeleportCooldown").orElseThrow());
//?}
        }
    }
}
