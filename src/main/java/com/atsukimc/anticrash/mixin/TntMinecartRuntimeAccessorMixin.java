//? if >=1.21.5 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.21.5 && <=1.21.11) {
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.entity.damage.DamageSource;
//?} else if >=26.1 {
//import net.minecraft.world.damagesource.DamageSource;
//import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if (>=1.21.5 && <=1.21.11) {
@Mixin(TntMinecartEntity.class)
//?} else if >=26.1 {
//@Mixin(MinecartTNT.class)
//?}
public interface TntMinecartRuntimeAccessorMixin {
//? if (>=1.21.5 && <=1.21.11) {
    @Accessor("damageSource") DamageSource anticrash$getDamageSource();
//?} else if >=26.1 {
//    @Accessor("ignitionSource") DamageSource anticrash$getDamageSource();
//?}
}
//?}
