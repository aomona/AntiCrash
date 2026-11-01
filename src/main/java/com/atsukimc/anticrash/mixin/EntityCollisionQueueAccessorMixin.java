//? if >=1.21.2 {
package com.atsukimc.anticrash.mixin;
//? if (>=1.21.2 && <=1.21.11) {
import net.minecraft.entity.Entity;
//?} else if >=26.1 {
//import net.minecraft.world.entity.Entity;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(Entity.class)
public interface EntityCollisionQueueAccessorMixin {
//? if (>=1.21.6 && <=1.21.11) {
    @Accessor("queuedCollisionChecks") java.util.ArrayDeque<?> anticrash$collisionChecks();
//?} else if (>=1.21.2 && <=1.21.5) {
//    @Accessor("queuedCollisionChecks") java.util.List<?> anticrash$collisionChecks();
//?} else if >=26.1 {
//    @Accessor("movementThisTick") java.util.ArrayDeque<?> anticrash$collisionChecks();
//?}
}
//?}
