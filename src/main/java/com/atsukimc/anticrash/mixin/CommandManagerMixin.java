package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.AntiCrashController;
import com.atsukimc.anticrash.AntiCrashMod;
import com.atsukimc.anticrash.command.AntiCrashCommand;
import com.atsukimc.anticrash.command.RecoveryCommandPolicy;
import com.atsukimc.anticrash.i18n.MessageCatalog;
import com.mojang.brigadier.CommandDispatcher;
//? if <=1.21.11 {
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
//?} else if >=26.1 {
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.Commands;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if <=1.21.11 {
@Mixin(CommandManager.class)
//?} else if >=26.1 {
//@Mixin(Commands.class)
//?}
public abstract class CommandManagerMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void anticrash$register(CallbackInfo callbackInfo) {
        AntiCrashController controller = AntiCrashMod.controller();
        if (controller != null) {
//? if <=1.21.11 {
            CommandDispatcher<ServerCommandSource> dispatcher = ((CommandManager) (Object) this).getDispatcher();
//?} else if >=26.1 {
//            CommandDispatcher<CommandSourceStack> dispatcher = ((Commands) (Object) this).getDispatcher();
//?}
            AntiCrashCommand.register(dispatcher, controller);
        }
    }

//? if (>=1.20.3 && <=1.21.11) {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void anticrash$blockCommandsDuringCheckpoint(com.mojang.brigadier.ParseResults<ServerCommandSource> parse, String command,
                                                          CallbackInfo callbackInfo) {
        ServerCommandSource source = parse.getContext().getSource();
//?} else if (>=1.19.1 && <=1.20.2) {
//    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
//    private void anticrash$blockCommandsDuringCheckpoint(com.mojang.brigadier.ParseResults<ServerCommandSource> parse, String command,
//                                                          CallbackInfoReturnable<Integer> callbackInfo) {
//        ServerCommandSource source = parse.getContext().getSource();
//?} else if <=1.19 {
//    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
//    private void anticrash$blockCommandsDuringCheckpoint(ServerCommandSource source, String command,
//                                                          CallbackInfoReturnable<Integer> callbackInfo) {
//?} else if >=26.1 {
//    @Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
//    private void anticrash$blockCommandsDuringCheckpoint(com.mojang.brigadier.ParseResults<CommandSourceStack> parse, String command,
//                                                          CallbackInfo callbackInfo) {
//        CommandSourceStack source = parse.getContext().getSource();
//?}
        AntiCrashController controller = AntiCrashMod.controller();
        if (controller == null) return;
        if (RecoveryCommandPolicy.isInternalExecution()) return;
        String normalized = RecoveryCommandPolicy.normalize(command);
        if (controller.isCheckpointFrozen()) {
            // A clean stop must be able to enter the deferred final-checkpoint path. The other
            // permitted commands are read-only AntiCrash inspection commands. Refusing silently
            // made a command issued inside the sub-second barrier look like a broken feature, so
            // say why and let the operator simply run it again.
            if (!RecoveryCommandPolicy.allowedDuringCheckpoint(normalized)) {
//? if <=1.21.11 {
                source.sendError(controller.chat(MessageCatalog.text(controller.config().language,
//?} else if >=26.1 {
//                source.sendFailure(controller.chat(MessageCatalog.text(controller.config().language,
//?}
                        "checkpoint.command_blocked"), null));
//? if >=1.20.3 {
                callbackInfo.cancel();
//?} else if <=1.20.2 {
//                callbackInfo.setReturnValue(0);
//?}
            }
            return;
        }
        // RCON, console wrappers and mod-issued command sources can mutate the World just as a
        // player can. Recovery safety therefore applies to every source; recovery controls and
        // read-only commands are explicitly allowed by the policy below.
        if (!controller.isRecoveryFrozen()) return;
        if (!RecoveryCommandPolicy.allowed(normalized, controller.config().recovery.lock.allowCommands)) {
//? if <=1.21.11 {
            source.sendError(controller.chat(MessageCatalog.text(controller.config().language,
//?} else if >=26.1 {
//            source.sendFailure(controller.chat(MessageCatalog.text(controller.config().language,
//?}
                    "recovery.command_mutation_blocked"), null));
//? if >=1.20.3 {
            callbackInfo.cancel();
//?} else if <=1.20.2 {
//            callbackInfo.setReturnValue(0);
//?}
        }
    }
}
