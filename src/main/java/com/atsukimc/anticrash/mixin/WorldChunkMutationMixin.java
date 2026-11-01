package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.snapshot.logical.ChunkMutationTracker;
//? if <=1.21.11 {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.chunk.LevelChunk;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Auditable semantic mutation hooks; this is intentionally not a universal mutation journal. */
//? if <=1.21.11 {
@Mixin(WorldChunk.class)
//?} else if >=26.1 {
//@Mixin(LevelChunk.class)
//?}
public abstract class WorldChunkMutationMixin {
    @Inject(method = "setBlockState", at = @At("TAIL"))
//? if >=1.21.5 {
    private void anticrash$blockStateMutation(BlockPos pos, BlockState state, int flags,
//?} else if <=1.21.4 {
//    private void anticrash$blockStateMutation(BlockPos pos, BlockState state, boolean moved,
//?}
                                               CallbackInfoReturnable<BlockState> callbackInfo) {
//? if <=1.21.11 {
        WorldChunk chunk = (WorldChunk) (Object) this;
        ChunkMutationTracker.record(chunk.getWorld(), chunk.getPos(), "setBlockState");
//?} else if >=26.1 {
//        LevelChunk chunk = (LevelChunk) (Object) this;
//        ChunkMutationTracker.record(chunk.getLevel(), chunk.getPos(), "setBlockState");
//?}
    }

    @Inject(method = "setBlockEntity", at = @At("TAIL"))
    private void anticrash$blockEntityMutation(BlockEntity blockEntity, CallbackInfo callbackInfo) {
//? if <=1.21.11 {
        WorldChunk chunk = (WorldChunk) (Object) this;
        ChunkMutationTracker.record(chunk.getWorld(), chunk.getPos(), "setBlockEntity");
//?} else if >=26.1 {
//        LevelChunk chunk = (LevelChunk) (Object) this;
//        ChunkMutationTracker.record(chunk.getLevel(), chunk.getPos(), "setBlockEntity");
//?}
    }

    @Inject(method = "removeBlockEntity", at = @At("TAIL"))
    private void anticrash$removeBlockEntityMutation(BlockPos pos, CallbackInfo callbackInfo) {
//? if <=1.21.11 {
        WorldChunk chunk = (WorldChunk) (Object) this;
        ChunkMutationTracker.record(chunk.getWorld(), chunk.getPos(), "removeBlockEntity");
//?} else if >=26.1 {
//        LevelChunk chunk = (LevelChunk) (Object) this;
//        ChunkMutationTracker.record(chunk.getLevel(), chunk.getPos(), "removeBlockEntity");
//?}
    }
}
