package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.block.entity.HopperBlockEntity;
//?} else if >=26.1 {
//import net.minecraft.world.level.block.entity.HopperBlockEntity;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HopperBlockEntity.class)
public interface HopperBlockEntityTimerAccessor {
//? if <=1.21.11 {
    @Accessor("transferCooldown")
//?} else if >=26.1 {
//    @Accessor("cooldownTime")
//?}
    int anticrash$getTransferCooldown();
}
