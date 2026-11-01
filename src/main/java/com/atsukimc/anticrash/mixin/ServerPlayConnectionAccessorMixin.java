//? if >=1.19.4 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.20.2 && <=1.21.11) {
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerCommonNetworkHandler;
//?} else if (>=1.19.4 && <=1.20.1) {
//import net.minecraft.network.ClientConnection;
//import net.minecraft.server.network.ServerPlayNetworkHandler;
//?} else if >=26.1 {
//import net.minecraft.network.Connection;
//import net.minecraft.server.network.ServerCommonPacketListenerImpl;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if (>=1.20.2 && <=1.21.11) {
@Mixin(ServerCommonNetworkHandler.class)
//?} else if (>=1.19.4 && <=1.20.1) {
//@Mixin(ServerPlayNetworkHandler.class)
//?} else if >=26.1 {
//@Mixin(ServerCommonPacketListenerImpl.class)
//?}
public interface ServerPlayConnectionAccessorMixin {
//? if (>=1.19.4 && <=1.21.11) {
    @Accessor("connection") ClientConnection anticrash$getConnection();
//?} else if >=26.1 {
//    @Accessor("connection") Connection anticrash$getConnection();
//?}
}
//?}
