package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.block.entity.PistonBlockEntity;
//?} else if >=26.1 {
//import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <=1.21.11 {
@Mixin(PistonBlockEntity.class)
//?} else if >=26.1 {
//@Mixin(PistonMovingBlockEntity.class)
//?}
public interface PistonBlockEntityAccessorMixin {
    @Accessor("progress")
    float anticrash$getProgress();

//? if <=1.21.11 {
    @Accessor("lastProgress")
//?} else if >=26.1 {
//    @Accessor("progressO")
//?}
    float anticrash$getLastProgress();
}
