package com.atsukimc.anticrash.mixin;

import com.mojang.authlib.GameProfile;
import com.atsukimc.anticrash.AntiCrashController;
import com.atsukimc.anticrash.AntiCrashMod;
import com.atsukimc.anticrash.i18n.MessageCatalog;
//? if (>=1.19 && <=1.21.11) {
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Text;
//?} else if <=1.18.2 {
//import net.minecraft.network.ClientConnection;
//import net.minecraft.server.PlayerManager;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.LiteralText;
//import net.minecraft.text.Text;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.network.Connection;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.server.players.PlayerList;
//?}

//? if <=1.21.11 {
@Mixin(PlayerManager.class)
//?} else if >=26.1 {
//@Mixin(PlayerList.class)
//?}
public abstract class PlayerManagerMixin {
//? if <=1.21.8 {
    @Inject(method = "checkCanJoin", at = @At("RETURN"), cancellable = true)
    private void anticrash$loginGate(SocketAddress address, GameProfile profile,
                                     CallbackInfoReturnable<Text> callbackInfo) {
//?} else if (>=1.21.9 && <=1.21.11) {
//    @Inject(method = "checkCanJoin", at = @At("RETURN"), cancellable = true)
//    private void anticrash$loginGate(SocketAddress address, net.minecraft.server.PlayerConfigEntry profile,
//                                     CallbackInfoReturnable<Text> callbackInfo) {
//?} else if >=26.1 {
//    @Inject(method = "canPlayerLogin", at = @At("RETURN"), cancellable = true)
//    private void anticrash$loginGate(SocketAddress address, net.minecraft.server.players.NameAndId profile,
//                                     CallbackInfoReturnable<Component> callbackInfo) {
//?}
        // Preserve Vanilla ban, whitelist, IP-ban and capacity messages.  Returning a reason from
        // checkCanJoin lets ServerLoginNetworkHandler perform its normal one-shot disconnect;
        // cancelling onPlayerConnect after player creation caused handleDisconnection() twice.
        if (callbackInfo.getReturnValue() != null) return;
        AntiCrashController controller = AntiCrashMod.controller();
        // Reached only after ServerLoginNetworkHandlerMixin's retry window has expired.
        if (controller != null && controller.isLoginBlocked()) {
//? if (>=1.19 && <=1.21.11) {
            callbackInfo.setReturnValue(Text.literal(MessageCatalog.text(
//?} else if <=1.18.2 {
//            callbackInfo.setReturnValue(new LiteralText(MessageCatalog.text(
//?} else if >=26.1 {
//            callbackInfo.setReturnValue(Component.literal(MessageCatalog.text(
//?}
                    controller.config().language, "recovery.login_blocked")));
        }
    }

//? if (>=1.20.2 && <=1.21.11) {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void anticrash$playerConnected(ClientConnection connection, ServerPlayerEntity player, net.minecraft.server.network.ConnectedClientData clientData, CallbackInfo callbackInfo) {
//?} else if <=1.20.1 {
//    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
//    private void anticrash$playerConnected(ClientConnection connection, ServerPlayerEntity player, CallbackInfo callbackInfo) {
//?} else if >=26.1 {
//    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
//    private void anticrash$playerConnected(Connection connection, ServerPlayer player, net.minecraft.server.network.CommonListenerCookie clientData, CallbackInfo callbackInfo) {
//?}
        AntiCrashController controller = AntiCrashMod.controller();
        if (controller != null) controller.onPlayerConnected(player);
    }
}
