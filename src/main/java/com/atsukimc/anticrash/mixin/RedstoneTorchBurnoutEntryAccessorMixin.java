package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.util.math.BlockPos;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.block.RedstoneTorchBlock;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <=1.21.11 {
@Mixin(RedstoneTorchBlock.BurnoutEntry.class)
//?} else if >=26.1 {
//@Mixin(RedstoneTorchBlock.Toggle.class)
//?}
public interface RedstoneTorchBurnoutEntryAccessorMixin {
    @Accessor("pos")
    BlockPos anticrash$getPos();

//? if <=1.21.11 {
    @Accessor("time")
//?} else if >=26.1 {
//    @Accessor("when")
//?}
    long anticrash$getTime();
}
