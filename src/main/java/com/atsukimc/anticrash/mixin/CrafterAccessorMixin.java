//? if >=1.20.3 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.20.3 && <=1.21.11) {
import net.minecraft.block.entity.CrafterBlockEntity;
//?} else if >=26.1 {
//import net.minecraft.world.level.block.entity.CrafterBlockEntity;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CrafterBlockEntity.class)
public interface CrafterAccessorMixin {
    @Accessor("craftingTicksRemaining") int anticrash$craftingTicksRemaining();
}
//?}
