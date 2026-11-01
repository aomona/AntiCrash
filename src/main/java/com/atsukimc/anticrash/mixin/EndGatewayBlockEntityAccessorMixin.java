package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.block.entity.EndGatewayBlockEntity;
//?} else if >=26.1 {
//import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <=1.21.11 {
@Mixin(EndGatewayBlockEntity.class)
//?} else if >=26.1 {
//@Mixin(TheEndGatewayBlockEntity.class)
//?}
public interface EndGatewayBlockEntityAccessorMixin {
    @Accessor("teleportCooldown")
    int anticrash$getTeleportCooldown();
}
