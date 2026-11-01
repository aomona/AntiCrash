package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
//?} else if >=26.1 {
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <=1.21.11 {
@Mixin(PlayerEntity.class)
//?} else if >=26.1 {
//@Mixin(Player.class)
//?}
public interface PlayerEntityAccessorMixin {
    /** The main-hand stack as of the player's last tick; a change resets the attack cooldown. */
//? if <=1.21.11 {
    @Accessor("selectedItem")
//?} else if >=26.1 {
//    @Accessor("lastItemInMainHand")
//?}
    void anticrash$setSelectedItem(ItemStack stack);
}
