//? if >=1.21 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.21 && <=1.21.11) {
import net.minecraft.block.Portal;
import net.minecraft.world.dimension.PortalManager;
//?} else if >=26.1 {
//import net.minecraft.world.entity.PortalProcessor;
//import net.minecraft.world.level.block.Portal;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if (>=1.21 && <=1.21.11) {
@Mixin(PortalManager.class)
//?} else if >=26.1 {
//@Mixin(PortalProcessor.class)
//?}
public interface PortalManagerAccessorMixin {
    @Accessor("portal") Portal anticrash$getPortal();
//? if (>=1.21 && <=1.21.11) {
    @Accessor("ticksInPortal") void anticrash$setTicksInPortal(int ticks);
//?} else if >=26.1 {
//    @Accessor("portalTime") void anticrash$setTicksInPortal(int ticks);
//?}
}
//?}
