//? if >=1.21 {
package com.atsukimc.anticrash.mixin;
//? if (>=1.21 && <=1.21.11) {
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
//?} else if >=26.1 {
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.projectile.Projectile;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
//? if (>=1.21 && <=1.21.11) {
@Mixin(ProjectileEntity.class)
//?} else if >=26.1 {
//@Mixin(Projectile.class)
//?}
public interface ProjectileRuntimeAccessorMixin {
//? if (>=1.21 && <=1.21.8) {
    @Accessor("lastDeflectedEntity") Entity anticrash$lastDeflectedEntity();
//?} else if (>=1.21.9 && <=1.21.11) {
//    @Accessor("lastDeflectedEntity") Entity anticrash$lastDeflectedEntity();
//    @Accessor("checkedForLeftOwner") boolean anticrash$checkedForLeftOwner();
//    @Accessor("checkedForLeftOwner") void anticrash$setCheckedForLeftOwner(boolean value);
//?} else if >=26.1 {
//    @Accessor("lastDeflectedBy") Entity anticrash$lastDeflectedEntity();
//    @Accessor("leftOwnerChecked") boolean anticrash$checkedForLeftOwner();
//    @Accessor("leftOwnerChecked") void anticrash$setCheckedForLeftOwner(boolean value);
//?}
}
//?}
