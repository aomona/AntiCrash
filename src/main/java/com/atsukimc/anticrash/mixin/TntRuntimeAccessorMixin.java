//? if >=1.21 {
package com.atsukimc.anticrash.mixin;
//? if (>=1.21 && <=1.21.11) {
import net.minecraft.entity.TntEntity;
//?} else if >=26.1 {
//import net.minecraft.world.entity.item.PrimedTnt;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
//? if (>=1.21 && <=1.21.11) {
@Mixin(TntEntity.class)
//?} else if >=26.1 {
//@Mixin(PrimedTnt.class)
//?}
public interface TntRuntimeAccessorMixin {
//? if (>=1.21 && <=1.21.11) {
    @Accessor("teleported") boolean anticrash$teleported();
    @Accessor("teleported") void anticrash$setTeleported(boolean value);
//?} else if >=26.1 {
//    @Accessor("usedPortal") boolean anticrash$teleported();
//    @Accessor("usedPortal") void anticrash$setTeleported(boolean value);
//?}
}
//?}
