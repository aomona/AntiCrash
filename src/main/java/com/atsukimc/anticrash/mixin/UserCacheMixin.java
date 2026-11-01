package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.integration.carpet.FakePlayerProfileScope;
import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if =1.21.8 {
/** Exact 1.21.8 mapped cache API; ordinary authentication/cache lookups are unchanged. */
@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.16.1 {
///** Exact 1.16.1 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.18 {
///** Exact 1.18 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.18.1 {
///** Exact 1.18.1 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.18.2 {
///** Exact 1.18.2 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.19 {
///** Exact 1.19 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.19.1 {
///** Exact 1.19.1 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.19.2 {
///** Exact 1.19.2 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.19.3 {
///** Exact 1.19.3 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.19.4 {
///** Exact 1.19.4 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.20 {
///** Exact 1.20 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.20.1 {
///** Exact 1.20.1 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.20.2 {
///** Exact 1.20.2 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.20.3 {
///** Exact 1.20.3 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.20.4 {
///** Exact 1.20.4 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.20.5 {
///** Exact 1.20.5 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.20.6 {
///** Exact 1.20.6 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21 {
///** Exact 1.21 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.1 {
///** Exact 1.21.1 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.2 {
///** Exact 1.21.2 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.3 {
///** Exact 1.21.3 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.4 {
///** Exact 1.21.4 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.5 {
///** Exact 1.21.5 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.6 {
///** Exact 1.21.6 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.7 {
///** Exact 1.21.7 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.9 {
///** Exact 1.21.9 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.10 {
///** Exact 1.21.10 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =1.21.11 {
///** Exact 1.21.11 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.util.UserCache.class)
//?} else if =26.1 {
///** Exact 26.1 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.server.players.CachedUserNameToIdResolver.class)
//?} else if =26.2 {
///** Exact 26.2 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.server.players.CachedUserNameToIdResolver.class)
//?} else if =26.3 {
///** Exact 26.3 mapped cache API; ordinary authentication/cache lookups are unchanged. */
//@Mixin(net.minecraft.server.players.CachedUserNameToIdResolver.class)
//?}
public abstract class UserCacheMixin {
//? if (>=1.18 && <=1.21.11) {
    @Inject(method = "findByName(Ljava/lang/String;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
//?} else if >=26.1 {
//    @Inject(method = "get(Ljava/lang/String;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
//?} else if =1.16.1 {
//    @Inject(method = "findByName(Ljava/lang/String;)Lcom/mojang/authlib/GameProfile;", at = @At("HEAD"), cancellable = true)
//?}
    private void anticrash$checkpointFakePlayerProfile(String name,
//? if (>=1.18 && <=1.21.8) {
            CallbackInfoReturnable<java.util.Optional<com.mojang.authlib.GameProfile>> callback) {
//?} else if (>=1.21.9 && <=1.21.11) {
//            CallbackInfoReturnable<java.util.Optional<net.minecraft.server.PlayerConfigEntry>> callback) {
//?} else if >=26.1 {
//            CallbackInfoReturnable<java.util.Optional<net.minecraft.server.players.NameAndId>> callback) {
//?} else if =1.16.1 {
//            CallbackInfoReturnable<GameProfile> callback) {
//?}
        GameProfile profile = FakePlayerProfileScope.lookup(this, name);
//? if (>=1.18 && <=1.21.8) {
        if (profile != null) callback.setReturnValue(java.util.Optional.of(profile));
//?} else if (>=1.21.9 && <=1.21.11) {
//        if (profile != null) callback.setReturnValue(java.util.Optional.of(new net.minecraft.server.PlayerConfigEntry(profile)));
//?} else if >=26.1 {
//        if (profile != null) callback.setReturnValue(java.util.Optional.of(new net.minecraft.server.players.NameAndId(profile)));
//?} else if =1.16.1 {
//        if (profile != null) callback.setReturnValue(profile);
//?}
    }
}
