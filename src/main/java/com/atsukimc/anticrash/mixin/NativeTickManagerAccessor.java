//? if >=1.20.3 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.20.3 && <=1.21.11) {
import net.minecraft.world.tick.TickManager;
//?} else if >=26.1 {
//import net.minecraft.world.TickRateManager;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if (>=1.20.3 && <=1.21.11) {
@Mixin(TickManager.class)
//?} else if >=26.1 {
//@Mixin(TickRateManager.class)
//?}
public interface NativeTickManagerAccessor {
//? if (>=1.20.3 && <=1.21.11) {
    @Accessor("shouldTick") void anticrash$setShouldTick(boolean value);
//?} else if >=26.1 {
//    @Accessor("runGameElements") void anticrash$setShouldTick(boolean value);
//?}
}
//?}
