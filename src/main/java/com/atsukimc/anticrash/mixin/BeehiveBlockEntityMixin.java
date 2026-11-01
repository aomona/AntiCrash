package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//?}

@Mixin(BeehiveBlockEntity.class)
public abstract class BeehiveBlockEntityMixin {
    /**
     * "TicksInHive" is saved by Vanilla, but the count advances every tick without the chunk being
     * marked for saving. A checkpoint therefore kept an old count and the bees left late.
     */
//? if <=1.21.11 {
    @Inject(method = "tickBees", at = @At("TAIL"))
    private static void anticrash$markChunkDirty(World world, BlockPos pos, BlockState state, List<?> bees,
//?} else if >=26.1 {
//    @Inject(method = "tickOccupants", at = @At("TAIL"))
//    private static void anticrash$markChunkDirty(Level world, BlockPos pos, BlockState state, List<?> bees,
//?}
                                                  BlockPos flowerPos, CallbackInfo callbackInfo) {
//? if <=1.21.11 {
        if (!bees.isEmpty()) world.markDirty(pos);
//?} else if >=26.1 {
//        if (!bees.isEmpty()) world.blockEntityChanged(pos);
//?}
    }
}
