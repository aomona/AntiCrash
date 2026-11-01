package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.block.entity.BrewingStandBlockEntity;
//?} else if >=26.1 {
//import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandBlockEntity.class)
public interface BrewingStandBlockEntityTimerAccessor {
//? if <=26.2 {
//?} else if =26.3 {
//    @Accessor("totalBrewTime") int anticrash$totalBrewTime();
//    @Accessor("fuel") int anticrash$fuel();
//    @Accessor("totalFuel") int anticrash$totalFuel();
//    @Accessor("speedMultiplier") float anticrash$speed();
//    @Accessor("quickCheck") net.minecraft.world.item.crafting.RecipeManager.CachedCheck anticrash$recipeCheck();
//?}
    @Accessor("brewTime")
    int anticrash$getBrewTime();

//? if <=1.21.11 {
    @Accessor("itemBrewing")
    net.minecraft.item.Item anticrash$getItemBrewing();
//?} else if >=26.1 {
//    @Accessor("ingredient")
//    net.minecraft.world.item.Item anticrash$getItemBrewing();
//?}
}
