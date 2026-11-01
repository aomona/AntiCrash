package com.atsukimc.anticrash.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <=1.21.11 {
@Mixin(targets = "net.minecraft.entity.player.ItemCooldownManager$Entry")
//?} else if >=26.1 {
//@Mixin(targets = "net.minecraft.world.item.ItemCooldowns$CooldownInstance")
//?}
public interface ItemCooldownEntryAccessorMixin {
//? if <=1.21.11 {
    @Accessor("endTick")
//?} else if >=26.1 {
//    @Accessor("endTime")
//?}
    int anticrash$getEndTick();
}
