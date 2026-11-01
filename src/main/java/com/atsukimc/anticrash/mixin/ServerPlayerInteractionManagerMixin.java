package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.AntiCrashController;
import com.atsukimc.anticrash.AntiCrashMod;
//? if <=1.21.11 {
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.server.level.ServerPlayerGameMode;
//?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if <=1.21.11 {
@Mixin(ServerPlayerInteractionManager.class)
//?} else if >=26.1 {
//@Mixin(ServerPlayerGameMode.class)
//?}
public abstract class ServerPlayerInteractionManagerMixin {
//? if <=1.21.11 {
    @Shadow @Final protected ServerPlayerEntity player;
//?} else if >=26.1 {
//    @Shadow @Final protected ServerPlayer player;
//?}

//? if <=1.21.11 {
    @Inject(method="tryBreakBlock",at=@At("RETURN"))
//?} else if >=26.1 {
//    @Inject(method="destroyBlock",at=@At("RETURN"))
//?}
    private void anticrash$recordSuccessfulBreak(BlockPos position,CallbackInfoReturnable<Boolean> info){
        if(!info.getReturnValueZ())return;
        AntiCrashController controller=AntiCrashMod.controller();
//? if <=1.21.11 {
        if(controller!=null)controller.recordTransaction("BLOCK_BREAK",player,"world","player",position,player.getMainHandStack());
//?} else if >=26.1 {
//        if(controller!=null)controller.recordTransaction("BLOCK_BREAK",player,"world","player",position,player.getMainHandItem());
//?}
    }
}
