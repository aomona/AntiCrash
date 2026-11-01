package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.world.item.ItemCooldowns;
//?}

//? if <=1.21.11 {
@Mixin(ItemCooldownManager.class)
//?} else if >=26.1 {
//@Mixin(ItemCooldowns.class)
//?}
public interface ItemCooldownManagerAccessorMixin {
//? if (>=1.21.2 && <=1.21.11) {
    @Accessor("entries")
    Map<net.minecraft.util.Identifier, Object> anticrash$getEntries();
//?} else if <=1.21.1 {
//    @Accessor("entries")
//    Map<Item, Object> anticrash$getEntries();
//?} else if >=26.1 {
//    @Accessor("cooldowns")
//    Map<net.minecraft.resources.Identifier, Object> anticrash$getEntries();
//?}

//? if <=1.21.11 {
    @Accessor("tick")
//?} else if >=26.1 {
//    @Accessor("tickCount")
//?}
    int anticrash$getTick();
}
