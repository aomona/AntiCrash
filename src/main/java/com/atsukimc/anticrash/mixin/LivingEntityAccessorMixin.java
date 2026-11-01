package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
//?} else if >=26.1 {
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.item.ItemStack;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessorMixin {
//? if <=1.21.10 {
    @Accessor("lastDamageTaken")
//?} else if >=26.1 {
//    @Accessor("lastKineticHitFeedbackTime") long anticrash$lastKineticAttackTime();
//    @Accessor("lastKineticHitFeedbackTime") void anticrash$setLastKineticAttackTime(long value);
//    @Accessor("lastHurt")
//?} else if =1.21.11 {
//    @Accessor("lastKineticAttackTime") long anticrash$lastKineticAttackTime();
//    @Accessor("lastKineticAttackTime") void anticrash$setLastKineticAttackTime(long value);
//    @Accessor("lastDamageTaken")
//?}
    float anticrash$getLastDamageTaken();

//? if <=1.21.11 {
    @Accessor("lastDamageTaken")
//?} else if >=26.1 {
//    @Accessor("lastHurt")
//?}
    void anticrash$setLastDamageTaken(float value);

//? if <=1.21.10 {
    @Accessor("lastAttackedTicks")
//?} else if >=26.1 {
//    @Accessor("attackStrengthTicker")
//?} else if =1.21.11 {
//    @Accessor("ticksSinceLastAttack")
//?}
    int anticrash$getLastAttackedTicks();

//? if <=1.21.10 {
    @Accessor("lastAttackedTicks")
//?} else if >=26.1 {
//    @Accessor("attackStrengthTicker")
//?} else if =1.21.11 {
//    @Accessor("ticksSinceLastAttack")
//?}
    void anticrash$setLastAttackedTicks(int ticks);

//? if <=1.21.11 {
    @Accessor("itemUseTimeLeft")
//?} else if >=26.1 {
//    @Accessor("useItemRemaining")
//?}
    int anticrash$getItemUseTimeLeft();

//? if <=1.21.11 {
    @Accessor("itemUseTimeLeft")
//?} else if >=26.1 {
//    @Accessor("useItemRemaining")
//?}
    void anticrash$setItemUseTimeLeft(int ticks);

//? if <=1.21.11 {
    @Accessor("activeItemStack")
//?} else if >=26.1 {
//    @Accessor("useItem")
//?}
    ItemStack anticrash$getActiveItemStack();
}
