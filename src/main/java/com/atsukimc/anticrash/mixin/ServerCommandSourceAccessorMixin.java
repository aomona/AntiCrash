package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
//?} else if >=26.1 {
//import net.minecraft.commands.CommandSource;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.network.chat.Component;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

//? if <=1.21.11 {
@Mixin(ServerCommandSource.class)
//?} else if >=26.1 {
//@Mixin(CommandSourceStack.class)
//?}
public interface ServerCommandSourceAccessorMixin {
//? if <=1.21.11 {
    @Accessor("output")
    CommandOutput anticrash$getOutput();
//?} else if >=26.1 {
//    @Accessor("source")
//    CommandSource anticrash$getOutput();
//?}

    @Accessor("silent")
    boolean anticrash$isSilent();

//? if <=1.21.11 {
    @Invoker("sendToOps")
    void anticrash$sendToOps(Text message);
//?} else if >=26.1 {
//    @Invoker("broadcastToAdmins")
//    void anticrash$sendToOps(Component message);
//?}
}
