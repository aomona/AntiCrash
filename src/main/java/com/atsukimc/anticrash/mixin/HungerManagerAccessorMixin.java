//? if >=1.21.2 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.21.2 && <=1.21.11) {
import net.minecraft.entity.player.HungerManager;
//?} else if >=26.1 {
//import net.minecraft.world.food.FoodData;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.21.2 removed the public exhaustion accessors, not the persisted exhaustion field. */
//? if (>=1.21.2 && <=1.21.11) {
@Mixin(HungerManager.class)
//?} else if >=26.1 {
//@Mixin(FoodData.class)
//?}
public interface HungerManagerAccessorMixin {
//? if (>=1.21.2 && <=1.21.11) {
    @Accessor("exhaustion") float anticrash$getExhaustion();
    @Accessor("exhaustion") void anticrash$setExhaustion(float value);
//?} else if >=26.1 {
//    @Accessor("exhaustionLevel") float anticrash$getExhaustion();
//    @Accessor("exhaustionLevel") void anticrash$setExhaustion(float value);
//?}
}
//?}
