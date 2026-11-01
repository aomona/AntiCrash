package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.AntiCrashController;
import com.atsukimc.anticrash.AntiCrashMod;
import com.atsukimc.anticrash.command.RecoveryCommandPolicy;
//? if (>=1.18 && <=1.21.11) {
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.protocol.game.*;
//import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
//import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
//import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
//import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
//import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
//import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
//import net.minecraft.network.protocol.game.ServerboundInteractPacket;
//import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
//import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
//import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
//import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
//import net.minecraft.network.protocol.game.ServerboundPickItemFromEntityPacket;
//import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
//import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
//import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
//import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
//import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
//import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
//import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
//import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
//import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.server.network.ServerGamePacketListenerImpl;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.inventory.ContainerInput;
//import net.minecraft.world.item.BlockItem;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.phys.Vec3;
//?} else if =1.16.1 {
//import com.atsukimc.anticrash.config.AntiCrashConfig.Lock;
//import net.minecraft.item.BlockItem;
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.packet.c2s.play.*;
//import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
//import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
//import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.server.network.ServerPlayNetworkHandler;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.screen.slot.SlotActionType;
//import net.minecraft.util.Hand;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Vec3d;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Packet-level recovery lock. Keepalive and read-only protocol traffic are not intercepted.
 *
 * <p>Only an operator-controlled recovery freeze refuses packets here, and every refusal is
 * answered so the client can undo its prediction. A checkpoint never refuses anything: its packets
 * wait in the held server task queue (see MinecraftServerMixin) and run once the cut is taken.</p>
 */
//? if <=1.21.11 {
@Mixin(ServerPlayNetworkHandler.class)
//?} else if >=26.1 {
//@Mixin(ServerGamePacketListenerImpl.class)
//?}
public abstract class ServerPlayNetworkHandlerMixin {
    private AntiCrashController anticrash$controller() { return AntiCrashMod.controller(); }
//? if <=1.21.11 {
    private ServerPlayerEntity anticrash$player() { return ((ServerPlayNetworkHandler)(Object)this).player; }
//?} else if >=26.1 {
//    private ServerPlayer anticrash$player() { return ((ServerGamePacketListenerImpl)(Object)this).player; }
//?}
    private boolean anticrash$frozen() { AntiCrashController controller=anticrash$controller(); return controller!=null&&controller.isRecoveryFrozen(); }
//? if <=1.21.11 {
    private ServerPlayNetworkHandler anticrash$handler() { return (ServerPlayNetworkHandler)(Object)this; }
//?} else if >=26.1 {
//    private ServerGamePacketListenerImpl anticrash$handler() { return (ServerGamePacketListenerImpl)(Object)this; }
//?}

//? if (>=1.19 && <=1.21.11) {
    /** Runs on the server thread; malformed input must not throw from an unwrapped task. */
    private void anticrash$ackPrediction(int sequence) {
        if (sequence < 0) {
            anticrash$handler().disconnect(net.minecraft.text.Text.translatable("multiplayer.disconnect.invalid_packet"));
        } else {
            anticrash$handler().updateSequence(sequence);
        }
    }

    /**
     * Answers a refused interaction the way Vanilla does.
     *
     * <p>Minecraft never drops a block or inventory action silently: {@code onPlayerAction} always
     * replies with {@link PlayerActionResponseS2CPacket}, {@code onPlayerInteractBlock} re-sends the
     * two affected block states, and {@code onClickSlot} calls {@link ScreenHandler#syncState()}.
     * The client needs that answer to undo the change it already predicted locally. Cancelling the
     * packet at HEAD skipped every one of those replies, so a block mined or placed during a
     * checkpoint barrier or a recovery freeze stayed on the client forever as a ghost block.</p>
     *
     * <p>A HEAD injection also runs on the Netty thread, before Minecraft moves the packet to the
     * server thread, so the world must only be read from inside the scheduled task.</p>
     */
//?} else if <=1.18.2 {
//    /**
//     * Answers a refused interaction the way Vanilla does.
//     *
//     * <p>Minecraft never drops a block or inventory action silently: {@code onPlayerAction} always
//     * replies with {@link PlayerActionResponseS2CPacket}, {@code onPlayerInteractBlock} re-sends the
//     * two affected block states, and {@code onClickSlot} calls {@link ScreenHandler#syncState()}.
//     * The client needs that answer to undo the change it already predicted locally. Cancelling the
//     * packet at HEAD skipped every one of those replies, so a block mined or placed during a
//     * checkpoint barrier or a recovery freeze stayed on the client forever as a ghost block.</p>
//     *
//     * <p>A HEAD injection also runs on the Netty thread, before Minecraft moves the packet to the
//     * server thread, so the world must only be read from inside the scheduled task.</p>
//     */
//?} else if >=26.1 {
//    /** Runs on the server thread; malformed input must not throw from an unwrapped task. */
//    private void anticrash$ackPrediction(int sequence) {
//        if (sequence < 0) {
//            anticrash$handler().disconnect(net.minecraft.network.chat.Component.translatable("multiplayer.disconnect.invalid_packet"));
//        } else {
//            anticrash$handler().ackBlockChangesUpTo(sequence);
//        }
//    }
//
//    /**
//     * Answers a refused interaction the way Vanilla does.
//     *
//     * <p>Minecraft never drops a block or inventory action silently: {@code onPlayerAction} always
//     * replies with {@link ClientboundBlockChangedAckPacket}, {@code onPlayerInteractBlock} re-sends the
//     * two affected block states, and {@code onClickSlot} calls {@link AbstractContainerMenu#sendAllDataToRemote()}.
//     * The client needs that answer to undo the change it already predicted locally. Cancelling the
//     * packet at HEAD skipped every one of those replies, so a block mined or placed during a
//     * checkpoint barrier or a recovery freeze stayed on the client forever as a ghost block.</p>
//     *
//     * <p>A HEAD injection also runs on the Netty thread, before Minecraft moves the packet to the
//     * server thread, so the world must only be read from inside the scheduled task.</p>
//     */
//?}
    private void anticrash$resyncBlocks(BlockPos first, BlockPos second) {
//? if <=1.21.11 {
        ServerPlayerEntity player = anticrash$player();
//?} else if >=26.1 {
//        ServerPlayer player = anticrash$player();
//?}
        if (player == null) return;
//? if (>=1.21.6 && <=1.21.8) {
        player.getServer().execute(() -> {
            anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getWorld(), first));
//?} else if (>=1.18 && <=1.21.5) {
//        player.server.execute(() -> {
//            anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getWorld(), first));
//?} else if (>=1.21.9 && <=1.21.11) {
//        player.getEntityWorld().getServer().execute(() -> {
//            anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getEntityWorld(), first));
//?} else if >=26.1 {
//        player.level().getServer().execute(() -> {
//            anticrash$handler().send(new ClientboundBlockUpdatePacket(player.level(), first));
//?} else if =1.16.1 {
//        player.server.execute(() -> {
//            anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getServerWorld(), first));
//?}
            if (second != null) {
//? if (>=1.18 && <=1.21.8) {
                anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getWorld(), second));
//?} else if (>=1.21.9 && <=1.21.11) {
//                anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getEntityWorld(), second));
//?} else if >=26.1 {
//                anticrash$handler().send(new ClientboundBlockUpdatePacket(player.level(), second));
//?} else if =1.16.1 {
//                anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getServerWorld(), second));
//?}
            }
        });
    }

    /** Tells the client the mining action was not approved so it restores the block it removed. */
//? if <=1.21.11 {
    private void anticrash$resyncBlockBreak(PlayerActionC2SPacket packet) {
        ServerPlayerEntity player = anticrash$player();
//?} else if >=26.1 {
//    private void anticrash$resyncBlockBreak(ServerboundPlayerActionPacket packet) {
//        ServerPlayer player = anticrash$player();
//?}
        if (player == null) return;
        BlockPos pos = packet.getPos();
//? if (>=1.21.6 && <=1.21.8) {
        player.getServer().execute(() -> {
            // 1.19 replaced per-block approvals with a cumulative prediction sequence ACK.
            anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getWorld(), pos));
            anticrash$ackPrediction(packet.getSequence());
        });
//?} else if (>=1.19 && <=1.21.5) {
//        player.server.execute(() -> {
//            // 1.19 replaced per-block approvals with a cumulative prediction sequence ACK.
//            anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getWorld(), pos));
//            anticrash$ackPrediction(packet.getSequence());
//        });
//?} else if (>=1.18 && <=1.18.2) {
//        PlayerActionC2SPacket.Action action = packet.getAction();
//        player.server.execute(() -> anticrash$handler().sendPacket(new PlayerActionResponseS2CPacket(
//                pos, player.getWorld().getBlockState(pos), action, false, "AntiCrash freeze")));
//?} else if (>=1.21.9 && <=1.21.11) {
//        player.getEntityWorld().getServer().execute(() -> {
//            // 1.19 replaced per-block approvals with a cumulative prediction sequence ACK.
//            anticrash$handler().sendPacket(new BlockUpdateS2CPacket(player.getEntityWorld(), pos));
//            anticrash$ackPrediction(packet.getSequence());
//        });
//?} else if >=26.1 {
//        player.level().getServer().execute(() -> {
//            // 1.19 replaced per-block approvals with a cumulative prediction sequence ACK.
//            anticrash$handler().send(new ClientboundBlockUpdatePacket(player.level(), pos));
//            anticrash$ackPrediction(packet.getSequence());
//        });
//?} else if =1.16.1 {
//        PlayerActionC2SPacket.Action action = packet.getAction();
//        player.server.execute(() -> anticrash$handler().sendPacket(new PlayerActionResponseS2CPacket(
//                pos, player.getServerWorld().getBlockState(pos), action, false, "AntiCrash freeze")));
//?}
    }

    /** Re-sends the authoritative ability flags after a refused toggle. */
    private void anticrash$resyncAbilities() {
//? if <=1.21.11 {
        ServerPlayerEntity player = anticrash$player();
//?} else if >=26.1 {
//        ServerPlayer player = anticrash$player();
//?}
        if (player == null) return;
//? if (>=1.21.6 && <=1.21.8) {
        player.getServer().execute(player::sendAbilitiesUpdate);
//?} else if <=1.21.5 {
//        player.server.execute(player::sendAbilitiesUpdate);
//?} else if (>=1.21.9 && <=1.21.11) {
//        player.getEntityWorld().getServer().execute(player::sendAbilitiesUpdate);
//?} else if >=26.1 {
//        player.level().getServer().execute(player::onUpdateAbilities);
//?}
    }

    /** Restores the client's predicted inventory, cursor stack and container contents. */
    private void anticrash$resyncInventory() {
//? if <=1.21.11 {
        ServerPlayerEntity player = anticrash$player();
//?} else if >=26.1 {
//        ServerPlayer player = anticrash$player();
//?}
        if (player == null) return;
//? if (>=1.21.6 && <=1.21.8) {
        player.getServer().execute(() -> {
            ScreenHandler handler = player.currentScreenHandler;
            if (handler != null) handler.syncState();
//?} else if <=1.21.5 {
//        player.server.execute(() -> {
//            ScreenHandler handler = player.currentScreenHandler;
//            if (handler != null) handler.syncState();
//?} else if (>=1.21.9 && <=1.21.11) {
//        player.getEntityWorld().getServer().execute(() -> {
//            ScreenHandler handler = player.currentScreenHandler;
//            if (handler != null) handler.syncState();
//?} else if >=26.1 {
//        player.level().getServer().execute(() -> {
//            AbstractContainerMenu handler = player.containerMenu;
//            if (handler != null) handler.sendAllDataToRemote();
//?}
        });
    }

    /** Records a mutation only when the controller exists, matching the freeze checks above. */
//? if <=1.21.11 {
    private void anticrash$record(String type, ServerPlayerEntity player, String source,
//?} else if >=26.1 {
//    private void anticrash$record(String type, ServerPlayer player, String source,
//?}
                                  String destination, BlockPos position, ItemStack stack) {
        // Mutation packets pass this HEAD injection twice: on the network thread and again
        // after Minecraft re-queues them to the server thread. Record only the authoritative pass.
//? if (>=1.21.6 && <=1.21.8) {
        if (player == null || !player.getServer().isOnThread()) return;
//?} else if <=1.21.5 {
//        if (player == null || !player.server.isOnThread()) return;
//?} else if (>=1.21.9 && <=1.21.11) {
//        if (player == null || !player.getEntityWorld().getServer().isOnThread()) return;
//?} else if >=26.1 {
//        if (player == null || !player.level().getServer().isSameThread()) return;
//?}
        AntiCrashController controller = anticrash$controller();
        if (controller != null) controller.recordTransaction(type, player, source, destination, position, stack);
    }
    /**
     * Stops the player's own tick while a checkpoint holds the world at its cut.
     *
     * <p>The connection tick runs from MinecraftServer#tick, outside the frozen world tick, and
     * {@code playerTick} moves, damages and feeds the player and picks up items. Letting it run
     * would change the world after the cut; its packets are already waiting in the held task
     * queue. Pausing it keeps the whole barrier equivalent to a single long server tick, which is
     * also what Vanilla's movement checks expect when the queued packets are processed.</p>
     */
    @Inject(method="tick",at=@At("HEAD"),cancellable=true)
    private void anticrash$holdPlayerTick(CallbackInfo info){
        AntiCrashController controller=anticrash$controller();
        if(controller!=null&&controller.holdsServerTasks())info.cancel();
    }

//? if (>=1.19.1 && <=1.21.11) {
    // Consume only after Vanilla has validated the signature, unpacked the chain and processed ACKs.
    @Inject(method="handleDecoratedMessage",at=@At("HEAD"),cancellable=true)
    private void anticrash$chat(net.minecraft.network.message.SignedMessage packet,CallbackInfo info){
//?} else if <=1.19 {
//    @Inject(method="onChatMessage",at=@At("HEAD"),cancellable=true)
//    private void anticrash$chat(ChatMessageC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    // Consume only after Vanilla has validated the signature, unpacked the chain and processed ACKs.
//    @Inject(method="broadcastChatMessage",at=@At("HEAD"),cancellable=true)
//    private void anticrash$chat(net.minecraft.network.chat.PlayerChatMessage packet,CallbackInfo info){
//?}
        AntiCrashController controller=anticrash$controller();
//? if (>=1.19.3 && <=1.21.11) {
        String message=packet.getSignedContent();
//?} else if <=1.19 {
//        String message=packet.getChatMessage();
//?} else if >=26.1 {
//        String message=packet.signedContent();
//?} else if (>=1.19.1 && <=1.19.2) {
//        String message=packet.getSignedContent().plain();
//?}
        if(controller!=null&&controller.isRecoveryAnswerCandidate(anticrash$player(),message)){
            // Plain chat is handled on the network thread. Answering schedules a restore and
            // stops the server, which must happen on the server thread like /ac y does.
            info.cancel();
//? if (>=1.21.6 && <=1.21.8) {
            ServerPlayerEntity player=anticrash$player();
            player.getServer().execute(()->controller.answerRecoveryFromChat(player,message));
//?} else if <=1.21.5 {
//            ServerPlayerEntity player=anticrash$player();
//            player.server.execute(()->controller.answerRecoveryFromChat(player,message));
//?} else if (>=1.21.9 && <=1.21.11) {
//            ServerPlayerEntity player=anticrash$player();
//            player.getEntityWorld().getServer().execute(()->controller.answerRecoveryFromChat(player,message));
//?} else if >=26.1 {
//            ServerPlayer player=anticrash$player();
//            player.level().getServer().execute(()->controller.answerRecoveryFromChat(player,message));
//?}
            return;
        }
        // A checkpoint barrier needs no filter here: a command reaches the world only through the
        // server task queue, which the barrier holds until its cut is taken.
        if(!anticrash$frozen())return;
        var lock=controller.config().recovery.lock;
        if((message.startsWith("/")&&!RecoveryCommandPolicy.allowed(message,lock.allowCommands))
                ||(!message.startsWith("/")&&!lock.allowChat))info.cancel();
    }

//? if <=1.21.11 {
    @Inject(method="onPlayerMove",at=@At("HEAD"),cancellable=true)
    private void anticrash$move(PlayerMoveC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handleMovePlayer",at=@At("HEAD"),cancellable=true)
//    private void anticrash$move(ServerboundMovePlayerPacket packet,CallbackInfo info){
//?}
        if(!anticrash$frozen())return;
        var lock=anticrash$controller().config().recovery.lock;
//? if <=1.21.11 {
        if((packet.changesPosition()&&!lock.allowPlayerMovement)||(packet.changesLook()&&!lock.allowPlayerLook))info.cancel();
//?} else if >=26.1 {
//        if((packet.hasPosition()&&!lock.allowPlayerMovement)||(packet.hasRotation()&&!lock.allowPlayerLook))info.cancel();
//?}
    }

//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method="onPlayerInput",at=@At(value="INVOKE",target="Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",shift=At.Shift.AFTER),cancellable=true)
    private void anticrash$input(PlayerInputC2SPacket packet,CallbackInfo info){
//?} else if <=1.21.5 {
//    @Inject(method="onPlayerInput",at=@At("HEAD"),cancellable=true)
//    private void anticrash$input(PlayerInputC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handlePlayerInput",at=@At(value="INVOKE",target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",shift=At.Shift.AFTER),cancellable=true)
//    private void anticrash$input(ServerboundPlayerInputPacket packet,CallbackInfo info){
//?}
        if(!anticrash$frozen())return;
        var lock=anticrash$controller().config().recovery.lock;
//? if (>=1.21.6 && <=1.21.11) {
        var input = packet.input();
        if(!com.atsukimc.anticrash.integration.carpet.PlayerInputCodec.allowedDuringFreeze(
                anticrash$player().getPlayerInput(),input,lock.allowPlayerMovement,lock.allowSneak))info.cancel();
//?} else if <=1.21.1 {
//        if((packet.isSneaking()&&!lock.allowSneak)||(!lock.allowPlayerMovement&&(packet.getForward()!=0||packet.getSideways()!=0||packet.isJumping())))info.cancel();
//?} else if (>=1.21.2 && <=1.21.5) {
//        var input = packet.input();
//        if((input.sneak()&&!lock.allowSneak)||(!lock.allowPlayerMovement&&(input.forward()||input.backward()||input.left()||input.right()||input.jump())))info.cancel();
//?} else if >=26.1 {
//        var input = packet.input();
//        if(!com.atsukimc.anticrash.integration.carpet.PlayerInputCodec.allowedDuringFreeze(
//                anticrash$player().getLastClientInput(),input,lock.allowPlayerMovement,lock.allowSneak))info.cancel();
//?}
    }

//? if <=1.21.11 {
    @Inject(method="onVehicleMove",at=@At("HEAD"),cancellable=true)
    private void anticrash$vehicleMove(VehicleMoveC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handleMoveVehicle",at=@At("HEAD"),cancellable=true)
//    private void anticrash$vehicleMove(ServerboundMoveVehiclePacket packet,CallbackInfo info){
//?}
        if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowPlayerMovement)info.cancel();
    }

//? if <=1.21.11 {
    @Inject(method="onPlayerAction",at=@At("HEAD"),cancellable=true)
    private void anticrash$action(PlayerActionC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handlePlayerAction",at=@At("HEAD"),cancellable=true)
//    private void anticrash$action(ServerboundPlayerActionPacket packet,CallbackInfo info){
//?}
        var action=packet.getAction();
//? if <=1.21.11 {
        boolean block=action==PlayerActionC2SPacket.Action.START_DESTROY_BLOCK
                ||action==PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK
                ||action==PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK;
        boolean drop=action==PlayerActionC2SPacket.Action.DROP_ITEM||action==PlayerActionC2SPacket.Action.DROP_ALL_ITEMS;
        boolean use=action==PlayerActionC2SPacket.Action.RELEASE_USE_ITEM||action==PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND;
//?} else if >=26.1 {
//        boolean block=action==ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK
//                ||action==ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK
//                ||action==ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK;
//        boolean drop=action==ServerboundPlayerActionPacket.Action.DROP_ITEM||action==ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS;
//        boolean use=action==ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM||action==ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND;
//?}
        AntiCrashController controller=anticrash$controller();
        boolean refused=false;
        if(anticrash$frozen()){
            var lock=controller.config().recovery.lock;
            refused=(block&&!lock.allowBlockBreak)||(drop&&!lock.allowItemDrop)||(use&&!lock.allowItemUse);
        }
        if(refused){
            info.cancel();
            if(block)anticrash$resyncBlockBreak(packet);
            else anticrash$resyncInventory();
            return;
        }
        // Actual block breaks are recorded at ServerPlayerInteractionManager.tryBreakBlock.
//? if <=1.21.11 {
        if(drop)anticrash$record("ITEM_DROP",anticrash$player(),"player","world",packet.getPos(),anticrash$player().getMainHandStack());
//?} else if >=26.1 {
//        if(drop)anticrash$record("ITEM_DROP",anticrash$player(),"player","world",packet.getPos(),anticrash$player().getMainHandItem());
//?}
    }

//? if <=1.21.11 {
    @Inject(method="onPlayerInteractBlock",at=@At("HEAD"),cancellable=true)
    private void anticrash$blockInteract(PlayerInteractBlockC2SPacket packet,CallbackInfo info){
        ServerPlayerEntity player=anticrash$player();
        BlockPos pos=packet.getBlockHitResult().getBlockPos();
        BlockPos placement=pos.offset(packet.getBlockHitResult().getSide());
//?} else if (>=26.1 && <=26.2) {
//    @Inject(method="handleUseItemOn",at=@At("HEAD"),cancellable=true)
//    private void anticrash$blockInteract(ServerboundUseItemOnPacket packet,CallbackInfo info){
//        ServerPlayer player=anticrash$player();
//        BlockPos pos=packet.getHitResult().getBlockPos();
//        BlockPos placement=pos.relative(packet.getHitResult().getDirection());
//?} else if =26.3 {
//    @Inject(method="handleUseItemOn",at=@At("HEAD"),cancellable=true)
//    private void anticrash$blockInteract(ServerboundUseItemOnPacket packet,CallbackInfo info){
//        ServerPlayer player=anticrash$player();
//        BlockPos pos=packet.hitResult().getBlockPos();
//        BlockPos placement=pos.relative(packet.hitResult().getDirection());
//?}
        AntiCrashController controller=anticrash$controller();
//? if <=1.21.11 {
        boolean place=player.getStackInHand(packet.getHand()).getItem() instanceof BlockItem;
//?} else if (>=26.1 && <=26.2) {
//        boolean place=player.getItemInHand(packet.getHand()).getItem() instanceof BlockItem;
//?} else if =26.3 {
//        boolean place=player.getItemInHand(packet.hand()).getItem() instanceof BlockItem;
//?}
        if(anticrash$frozen()){
            var lock=controller.config().recovery.lock;
            boolean refused=place?!lock.allowBlockPlace:!lock.allowItemUse;
            if(!refused&&!lock.allowContainerInteraction){
                // Only a container changes the outcome here, and identifying one reads the world.
                // That read is safe only once Minecraft has moved this packet to the server thread,
                // so let the packet through and decide again on that second pass.
//? if (>=1.21.6 && <=1.21.8) {
                if(Thread.currentThread()!=player.getServer().getThread())return;
                refused=player.getWorld().getBlockEntity(pos)!=null;
//?} else if (>=1.18 && <=1.21.5) {
//                if(Thread.currentThread()!=player.server.getThread())return;
//                refused=player.getWorld().getBlockEntity(pos)!=null;
//?} else if (>=1.21.9 && <=1.21.11) {
//                if(Thread.currentThread()!=player.getEntityWorld().getServer().getThread())return;
//                refused=player.getEntityWorld().getBlockEntity(pos)!=null;
//?} else if >=26.1 {
//                if(Thread.currentThread()!=player.level().getServer().getRunningThread())return;
//                refused=player.level().getBlockEntity(pos)!=null;
//?} else if =1.16.1 {
//                if(Thread.currentThread()!=player.server.getThread())return;
//                refused=player.getServerWorld().getBlockEntity(pos)!=null;
//?}
            }
            if(refused){
                info.cancel();
                anticrash$resyncBlocks(pos,placement);
//? if (>=1.21.6 && <=1.21.8) {
                player.getServer().execute(() -> anticrash$ackPrediction(packet.getSequence()));
//?} else if (>=1.19 && <=1.21.5) {
//                player.server.execute(() -> anticrash$ackPrediction(packet.getSequence()));
//?} else if <=1.18.2 {
//?} else if (>=1.21.9 && <=1.21.11) {
//                player.getEntityWorld().getServer().execute(() -> anticrash$ackPrediction(packet.getSequence()));
//?} else if (>=26.1 && <=26.2) {
//                player.level().getServer().execute(() -> anticrash$ackPrediction(packet.getSequence()));
//?} else if =26.3 {
//                player.level().getServer().execute(() -> anticrash$ackPrediction(packet.sequence()));
//?}
                anticrash$resyncInventory();
                return;
            }
        }
//? if <=1.21.11 {
        anticrash$record(place?"BLOCK_PLACE":"ITEM_USE",player,"player","world",pos,player.getStackInHand(packet.getHand()));
//?} else if (>=26.1 && <=26.2) {
//        anticrash$record(place?"BLOCK_PLACE":"ITEM_USE",player,"player","world",pos,player.getItemInHand(packet.getHand()));
//?} else if =26.3 {
//        anticrash$record(place?"BLOCK_PLACE":"ITEM_USE",player,"player","world",pos,player.getItemInHand(packet.hand()));
//?}
    }

//? if <=1.21.11 {
    @Inject(method="onPlayerInteractItem",at=@At("HEAD"),cancellable=true)
    private void anticrash$itemInteract(PlayerInteractItemC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handleUseItem",at=@At("HEAD"),cancellable=true)
//    private void anticrash$itemInteract(ServerboundUseItemPacket packet,CallbackInfo info){
//?}
        if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowItemUse){
//? if (>=1.21.6 && <=1.21.8) {
            info.cancel();anticrash$resyncInventory();
            ServerPlayerEntity player = anticrash$player();
            player.getServer().execute(() -> anticrash$ackPrediction(packet.getSequence()));
            return;}
//?} else if (>=1.19 && <=1.21.5) {
//            info.cancel();anticrash$resyncInventory();
//            ServerPlayerEntity player = anticrash$player();
//            player.server.execute(() -> anticrash$ackPrediction(packet.getSequence()));
//            return;}
//?} else if <=1.18.2 {
//            info.cancel();anticrash$resyncInventory();return;}
//?} else if (>=1.21.9 && <=1.21.11) {
//            info.cancel();anticrash$resyncInventory();
//            ServerPlayerEntity player = anticrash$player();
//            player.getEntityWorld().getServer().execute(() -> anticrash$ackPrediction(packet.getSequence()));
//            return;}
//?} else if (>=26.1 && <=26.2) {
//            info.cancel();anticrash$resyncInventory();
//            ServerPlayer player = anticrash$player();
//            player.level().getServer().execute(() -> anticrash$ackPrediction(packet.getSequence()));
//            return;}
//?} else if =26.3 {
//            info.cancel();anticrash$resyncInventory();
//            ServerPlayer player = anticrash$player();
//            player.level().getServer().execute(() -> anticrash$ackPrediction(packet.sequence()));
//            return;}
//?}
        anticrash$record("ITEM_USE",anticrash$player(),"player","world",null,
//? if <=1.21.11 {
                anticrash$player().getStackInHand(packet.getHand()));
//?} else if (>=26.1 && <=26.2) {
//                anticrash$player().getItemInHand(packet.getHand()));
//?} else if =26.3 {
//                anticrash$player().getItemInHand(packet.hand()));
//?}
    }

//? if <=1.21.11 {
    @Inject(method="onPlayerInteractEntity",at=@At("HEAD"),cancellable=true)
    private void anticrash$entityInteract(PlayerInteractEntityC2SPacket packet,CallbackInfo info){
        boolean attack=anticrash$entityActionIsAttack(packet);
//?} else if >=26.1 {
//    @Inject(method="handleInteract",at=@At(value="INVOKE",
//            target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",
//            shift=At.Shift.AFTER),cancellable=true)
//    private void anticrash$entityInteract(ServerboundInteractPacket packet,CallbackInfo info){
//?}
        if(anticrash$frozen()){
            var lock=anticrash$controller().config().recovery.lock;
//? if <=1.21.11 {
            if((attack&&!lock.allowEntityAttack)||(!attack&&!lock.allowEntityInteract)){
//?} else if >=26.1 {
//            if(!lock.allowEntityInteract){
//?}
                info.cancel();anticrash$resyncInventory();return;}
        }
//? if <=1.21.11 {
        anticrash$record(attack?"ENTITY_ATTACK":"ENTITY_INTERACT",anticrash$player(),"player","entity",null,anticrash$player().getMainHandStack());
//?} else if >=26.1 {
//        anticrash$record("ENTITY_INTERACT",anticrash$player(),"player","entity",null,anticrash$player().getMainHandItem());
//?}
    }

//? if (>=1.21.5 && <=1.21.11) {
    @Inject(method="onClickSlot",at=@At(value="INVOKE",
            target="Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",
            shift=At.Shift.AFTER),cancellable=true)
    private void anticrash$click(ClickSlotC2SPacket packet,CallbackInfo info){
        boolean drop=packet.actionType()==SlotActionType.THROW;
//?} else if (>=1.18 && <=1.21.4) {
//    @Inject(method="onClickSlot",at=@At("HEAD"),cancellable=true)
//    private void anticrash$click(ClickSlotC2SPacket packet,CallbackInfo info){
//        boolean drop=packet.getActionType()==SlotActionType.THROW;
//?} else if >=26.1 {
//    // 26.1 has a separate attack packet. Cancelling only Interact no longer prevents attacks.
//    @Inject(method="handleAttack",at=@At(value="INVOKE",
//            target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",
//            shift=At.Shift.AFTER),cancellable=true)
//    private void anticrash$entityAttack(ServerboundAttackPacket packet,CallbackInfo info){
//        if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowEntityAttack){
//            info.cancel();anticrash$resyncInventory();return;
//        }
//        anticrash$record("ENTITY_ATTACK",anticrash$player(),"player","entity",null,anticrash$player().getMainHandItem());
//    }
//
//    @Inject(method="handleContainerClick",at=@At(value="INVOKE",
//            target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",
//            shift=At.Shift.AFTER),cancellable=true)
//    private void anticrash$click(ServerboundContainerClickPacket packet,CallbackInfo info){
//        boolean drop=packet.containerInput()==ContainerInput.THROW;
//?} else if =1.16.1 {
//    @Inject(method="onClickSlot",at=@At("HEAD"),cancellable=true)
//    private void anticrash$click(ClickWindowC2SPacket packet,CallbackInfo info){
//        boolean drop=packet.getActionType()==SlotActionType.THROW;
//?}
        if(anticrash$frozen()){
            var lock=anticrash$controller().config().recovery.lock;
            if((drop&&!lock.allowItemDrop)||(!drop&&!lock.allowContainerInteraction)){
                info.cancel();anticrash$resyncInventory();return;}
        }
//? if (>=1.21.5 && <=1.21.11) {
        // The packet now contains hashes, not trusted ItemStacks. Log the server-owned cursor.
        anticrash$record(drop?"ITEM_DROP":"CONTAINER_INTERACTION",anticrash$player(),"player","container",null,anticrash$player().currentScreenHandler.getCursorStack());
//?} else if <=1.21.4 {
//        anticrash$record(drop?"ITEM_DROP":"CONTAINER_INTERACTION",anticrash$player(),"player","container",null,packet.getStack());
//?} else if >=26.1 {
//        // The packet now contains hashes, not trusted ItemStacks. Log the server-owned cursor.
//        anticrash$record(drop?"ITEM_DROP":"CONTAINER_INTERACTION",anticrash$player(),"player","container",null,anticrash$player().containerMenu.getCarried());
//?}
    }

//? if (>=1.20.5 && <=1.21.11) {
    @Inject(method="onCreativeInventoryAction",at=@At("HEAD"),cancellable=true)
    private void anticrash$creative(CreativeInventoryActionC2SPacket packet,CallbackInfo info){
        boolean drop=packet.slot()<0;
//?} else if <=1.20.4 {
//    @Inject(method="onCreativeInventoryAction",at=@At("HEAD"),cancellable=true)
//    private void anticrash$creative(CreativeInventoryActionC2SPacket packet,CallbackInfo info){
//        boolean drop=packet.getSlot()<0;
//?} else if >=26.1 {
//    @Inject(method="handleSetCreativeModeSlot",at=@At("HEAD"),cancellable=true)
//    private void anticrash$creative(ServerboundSetCreativeModeSlotPacket packet,CallbackInfo info){
//        boolean drop=packet.slotNum()<0;
//?}
        if(anticrash$frozen()){
            var lock=anticrash$controller().config().recovery.lock;
            if((drop&&!lock.allowItemDrop)||(!drop&&!lock.allowContainerInteraction)){
                info.cancel();anticrash$resyncInventory();return;}
        }
//? if (>=1.20.5 && <=1.21.11) {
        anticrash$record(drop?"ITEM_DROP":"CREATIVE_INVENTORY",anticrash$player(),"player",drop?"world":"inventory",null,packet.stack());
//?} else if <=1.20.1 {
//        anticrash$record(drop?"ITEM_DROP":"CREATIVE_INVENTORY",anticrash$player(),"player",drop?"world":"inventory",null,packet.getItemStack());
//?} else if (>=1.20.2 && <=1.20.4) {
//        anticrash$record(drop?"ITEM_DROP":"CREATIVE_INVENTORY",anticrash$player(),"player",drop?"world":"inventory",null,packet.getStack());
//?} else if >=26.1 {
//        anticrash$record(drop?"ITEM_DROP":"CREATIVE_INVENTORY",anticrash$player(),"player",drop?"world":"inventory",null,packet.itemStack());
//?}
    }

//? if (>=1.18 && <=1.21.11) {
    @Inject(method="onCloseHandledScreen",at=@At("HEAD"),cancellable=true)
    private void anticrash$closeScreen(CloseHandledScreenC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handleContainerClose",at=@At("HEAD"),cancellable=true)
//    private void anticrash$closeScreen(ServerboundContainerClosePacket packet,CallbackInfo info){
//?} else if =1.16.1 {
//    @Inject(method="onCloseHandledScreen",at=@At("HEAD"),cancellable=true)
//    private void anticrash$closeScreen(GuiCloseC2SPacket packet,CallbackInfo info){
//?}
        if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowContainerInteraction){
            info.cancel();anticrash$resyncInventory();return;}
//? if <=1.21.11 {
        anticrash$record("CONTAINER_CLOSE",anticrash$player(),"container","player",null,anticrash$player().getMainHandStack());
//?} else if >=26.1 {
//        anticrash$record("CONTAINER_CLOSE",anticrash$player(),"container","player",null,anticrash$player().getMainHandItem());
//?}
    }

//? if (>=1.18 && <=1.21.11) {
    @Inject(method="onSelectMerchantTrade",at=@At("HEAD"),cancellable=true)
    private void anticrash$merchant(SelectMerchantTradeC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handleSelectTrade",at=@At("HEAD"),cancellable=true)
//    private void anticrash$merchant(ServerboundSelectTradePacket packet,CallbackInfo info){
//?} else if =1.16.1 {
//    @Inject(method="onSelectMerchantTrade",at=@At("HEAD"),cancellable=true)
//    private void anticrash$merchant(SelectVillagerTradeC2SPacket packet,CallbackInfo info){
//?}
        if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowContainerInteraction){
            info.cancel();anticrash$resyncInventory();return;}
//? if <=1.21.11 {
        anticrash$record("MERCHANT_SELECTION",anticrash$player(),"player","merchant",null,anticrash$player().getMainHandStack());
//?} else if >=26.1 {
//        anticrash$record("MERCHANT_SELECTION",anticrash$player(),"player","merchant",null,anticrash$player().getMainHandItem());
//?}
    }

//? if (>=1.21.4 && <=1.21.11) {
    @Inject(method="onPickItemFromBlock",at=@At(value="INVOKE",target="Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",shift=At.Shift.AFTER),cancellable=true)
    private void anticrash$pickBlock(PickItemFromBlockC2SPacket packet,CallbackInfo info){
        anticrash$pickInventory(info);
    }
    @Inject(method="onPickItemFromEntity",at=@At(value="INVOKE",target="Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/server/world/ServerWorld;)V",shift=At.Shift.AFTER),cancellable=true)
    private void anticrash$pickEntity(PickItemFromEntityC2SPacket packet,CallbackInfo info){
        anticrash$pickInventory(info);
    }
    private void anticrash$pickInventory(CallbackInfo info){
//?} else if <=1.21.3 {
//    @Inject(method="onPickFromInventory",at=@At("HEAD"),cancellable=true)
//    private void anticrash$pickInventory(PickFromInventoryC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handlePickItemFromBlock",at=@At(value="INVOKE",target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",shift=At.Shift.AFTER),cancellable=true)
//    private void anticrash$pickBlock(ServerboundPickItemFromBlockPacket packet,CallbackInfo info){
//        anticrash$pickInventory(info);
//    }
//    @Inject(method="handlePickItemFromEntity",at=@At(value="INVOKE",target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",shift=At.Shift.AFTER),cancellable=true)
//    private void anticrash$pickEntity(ServerboundPickItemFromEntityPacket packet,CallbackInfo info){
//        anticrash$pickInventory(info);
//    }
//    private void anticrash$pickInventory(CallbackInfo info){
//?}
        if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowContainerInteraction){
            info.cancel();anticrash$resyncInventory();return;}
//? if <=1.21.11 {
        anticrash$record("INVENTORY_PICK",anticrash$player(),"inventory","hotbar",null,anticrash$player().getMainHandStack());
//?} else if >=26.1 {
//        anticrash$record("INVENTORY_PICK",anticrash$player(),"inventory","hotbar",null,anticrash$player().getMainHandItem());
//?}
    }

//? if <=1.21.11 {
    @Inject(method="onSpectatorTeleport",at=@At("HEAD"),cancellable=true)
    private void anticrash$spectator(SpectatorTeleportC2SPacket packet,CallbackInfo info){if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowPlayerMovement)info.cancel();}
    @Inject(method="onClientStatus",at=@At("HEAD"),cancellable=true)
    private void anticrash$status(ClientStatusC2SPacket packet,CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onUpdatePlayerAbilities",at=@At("HEAD"),cancellable=true)
    private void anticrash$abilities(UpdatePlayerAbilitiesC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handleTeleportToEntityPacket",at=@At("HEAD"),cancellable=true)
//    private void anticrash$spectator(ServerboundTeleportToEntityPacket packet,CallbackInfo info){if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowPlayerMovement)info.cancel();}
//    @Inject(method="handleClientCommand",at=@At("HEAD"),cancellable=true)
//    private void anticrash$status(ServerboundClientCommandPacket packet,CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handlePlayerAbilities",at=@At("HEAD"),cancellable=true)
//    private void anticrash$abilities(ServerboundPlayerAbilitiesPacket packet,CallbackInfo info){
//?}
        // Vanilla only copies the client's flying flag onto the player here. Dropping it left the
        // server-side abilities.flying stale, which is exactly the flag Carpet's creativeNoClip
        // reads, so only the explicit recovery lock refuses it (and then re-sends the flags).
        if(!anticrash$frozen())return;
        if(!anticrash$controller().config().recovery.lock.allowPlayerMovement){
            info.cancel();
            anticrash$resyncAbilities();
        }
    }
//? if <=1.21.11 {
    @Inject(method="onUpdateDifficulty",at=@At("HEAD"),cancellable=true)
    private void anticrash$difficulty(UpdateDifficultyC2SPacket packet,CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onUpdateDifficultyLock",at=@At("HEAD"),cancellable=true)
    private void anticrash$difficultyLock(UpdateDifficultyLockC2SPacket packet,CallbackInfo info){if(anticrash$frozen())info.cancel();}
//?} else if >=26.1 {
//    @Inject(method="handleChangeDifficulty",at=@At("HEAD"),cancellable=true)
//    private void anticrash$difficulty(ServerboundChangeDifficultyPacket packet,CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handleLockDifficulty",at=@At("HEAD"),cancellable=true)
//    private void anticrash$difficultyLock(ServerboundLockDifficultyPacket packet,CallbackInfo info){if(anticrash$frozen())info.cancel();}
//?}

//? if <=1.21.11 {
    @Inject(method="onUpdateCommandBlock",at=@At("HEAD"),cancellable=true) private void anticrash$commandBlock(CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onUpdateCommandBlockMinecart",at=@At("HEAD"),cancellable=true) private void anticrash$commandBlockMinecart(CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onUpdateBeacon",at=@At("HEAD"),cancellable=true) private void anticrash$beacon(CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onUpdateStructureBlock",at=@At("HEAD"),cancellable=true) private void anticrash$structure(CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onUpdateJigsaw",at=@At("HEAD"),cancellable=true) private void anticrash$jigsaw(CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onJigsawGenerating",at=@At("HEAD"),cancellable=true) private void anticrash$jigsawGenerate(CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onBookUpdate",at=@At("HEAD"),cancellable=true) private void anticrash$book(CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onRenameItem",at=@At("HEAD"),cancellable=true) private void anticrash$rename(CallbackInfo info){if(anticrash$frozen()){info.cancel();anticrash$resyncInventory();}}
    @Inject(method="onUpdateSign",at=@At("HEAD"),cancellable=true) private void anticrash$sign(CallbackInfo info){if(anticrash$frozen())info.cancel();}
    @Inject(method="onCraftRequest",at=@At("HEAD"),cancellable=true) private void anticrash$craft(CallbackInfo info){if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowContainerInteraction){info.cancel();anticrash$resyncInventory();}}
    @Inject(method="onButtonClick",at=@At("HEAD"),cancellable=true) private void anticrash$button(CallbackInfo info){if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowContainerInteraction){info.cancel();anticrash$resyncInventory();}}
    @Inject(method="onClientCommand",at=@At("HEAD"),cancellable=true)
    private void anticrash$clientCommand(ClientCommandC2SPacket packet,CallbackInfo info){
//?} else if >=26.1 {
//    @Inject(method="handleSetCommandBlock",at=@At("HEAD"),cancellable=true) private void anticrash$commandBlock(CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handleSetCommandMinecart",at=@At("HEAD"),cancellable=true) private void anticrash$commandBlockMinecart(CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handleSetBeaconPacket",at=@At("HEAD"),cancellable=true) private void anticrash$beacon(CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handleSetStructureBlock",at=@At("HEAD"),cancellable=true) private void anticrash$structure(CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handleSetJigsawBlock",at=@At("HEAD"),cancellable=true) private void anticrash$jigsaw(CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handleJigsawGenerate",at=@At("HEAD"),cancellable=true) private void anticrash$jigsawGenerate(CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handleEditBook",at=@At("HEAD"),cancellable=true) private void anticrash$book(CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handleRenameItem",at=@At("HEAD"),cancellable=true) private void anticrash$rename(CallbackInfo info){if(anticrash$frozen()){info.cancel();anticrash$resyncInventory();}}
//    @Inject(method="handleSignUpdate",at=@At("HEAD"),cancellable=true) private void anticrash$sign(CallbackInfo info){if(anticrash$frozen())info.cancel();}
//    @Inject(method="handlePlaceRecipe",at=@At("HEAD"),cancellable=true) private void anticrash$craft(CallbackInfo info){if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowContainerInteraction){info.cancel();anticrash$resyncInventory();}}
//    @Inject(method="handleContainerButtonClick",at=@At("HEAD"),cancellable=true) private void anticrash$button(CallbackInfo info){if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowContainerInteraction){info.cancel();anticrash$resyncInventory();}}
//    @Inject(method="handlePlayerCommand",at=@At("HEAD"),cancellable=true)
//    private void anticrash$clientCommand(ServerboundPlayerCommandPacket packet,CallbackInfo info){
//?}
        if(!anticrash$frozen())return;
        var lock=anticrash$controller().config().recovery.lock;
//? if (>=1.21.6 && <=1.21.11) {
        boolean allowed=switch(packet.getMode()){
//?} else if <=1.21.5 {
//        boolean allowed=switch(packet.getMode()){
//            case PRESS_SHIFT_KEY, RELEASE_SHIFT_KEY -> lock.allowSneak;
//?} else if >=26.1 {
//        boolean allowed=switch(packet.getAction()){
//?}
            case START_SPRINTING, STOP_SPRINTING, START_RIDING_JUMP,
                    STOP_RIDING_JUMP, START_FALL_FLYING -> lock.allowPlayerMovement;
            case OPEN_INVENTORY -> lock.allowContainerInteraction;
            case STOP_SLEEPING -> false;
        };
        if(!allowed)info.cancel();
    }
//? if <=1.21.11 {
    @Inject(method="onBoatPaddleState",at=@At("HEAD"),cancellable=true) private void anticrash$boatPaddle(CallbackInfo info){if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowPlayerMovement)info.cancel();}
//?} else if >=26.1 {
//    @Inject(method="handlePaddleBoat",at=@At("HEAD"),cancellable=true) private void anticrash$boatPaddle(CallbackInfo info){if(anticrash$frozen()&&!anticrash$controller().config().recovery.lock.allowPlayerMovement)info.cancel();}
//?}
    /**
     * Mod payloads are not handled while recovery is frozen, but they are not thrown away either:
     * handshakes pass at once and the rest is delivered after the start; see DeferredPayloads.
     */
//? if >=1.20.2 {
//?} else if <=1.20.1 {
//    @Inject(method="onCustomPayload",at=@At("HEAD"),cancellable=true)
//    private void anticrash$customPayload(CustomPayloadC2SPacket packet,CallbackInfo info){
//        if(!anticrash$frozen())return;
//        if(com.atsukimc.anticrash.network.DeferredPayloads.immediate(String.valueOf(packet.getChannel())))return;
//        anticrash$controller().deferCustomPayload(anticrash$handler(),packet);
//        info.cancel();
//    }
//?}

//? if <=1.21.11 {
    private static boolean anticrash$entityActionIsAttack(PlayerInteractEntityC2SPacket packet){
        boolean[] attack={false};
        packet.handle(new PlayerInteractEntityC2SPacket.Handler(){
            @Override public void interact(Hand hand) { }
            @Override public void interactAt(Hand hand, Vec3d position) { }
            @Override public void attack() { attack[0]=true; }
        });
        return attack[0];
    }
//?} else if >=26.1 {
//?}
}
