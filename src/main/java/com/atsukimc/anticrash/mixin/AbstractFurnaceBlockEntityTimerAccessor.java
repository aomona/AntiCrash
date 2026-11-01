package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
//?} else if >=26.1 {
//import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityTimerAccessor {
//? if >=1.21.4 {
    @Accessor("litTimeRemaining")
//?} else if <=1.21.3 {
//    @Accessor("burnTime")
//?}
    int anticrash$getBurnTime();

//? if (>=1.21.4 && <=1.21.11) {
    @Accessor("cookingTimeSpent")
//?} else if <=1.21.3 {
//    @Accessor("cookTime")
//?} else if >=26.1 {
//    @Accessor("cookingTimer")
//?}
    int anticrash$getCookTime();
}
