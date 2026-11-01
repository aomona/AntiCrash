//? if >=1.21.2 {
package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.CampfireRecipeState;
import com.atsukimc.anticrash.runtime.RecipeBoundTicker;
//? if (>=1.21.2 && <=1.21.11) {
import net.minecraft.block.entity.*;
import net.minecraft.world.chunk.*;
//?} else if >=26.1 {
//import net.minecraft.world.level.block.entity.*;
//import net.minecraft.world.level.chunk.*;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.BlockEntityTicker;
//import net.minecraft.world.level.block.entity.CampfireBlockEntity;
//import net.minecraft.world.level.block.entity.TickingBlockEntity;
//import net.minecraft.world.level.chunk.LevelChunk;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if (>=1.21.2 && <=1.21.11) {
@Mixin(WorldChunk.class)
//?} else if >=26.1 {
//@Mixin(LevelChunk.class)
//?}
public abstract class WorldChunkRecipeBindingMixin {
//? if (>=1.21.2 && <=1.21.11) {
    @Inject(method = "wrapTicker", at = @At("HEAD"))
//?} else if >=26.1 {
//    @Inject(method = "createTicker", at = @At("HEAD"))
//?}
    private <T extends BlockEntity> void anticrash$bind(T entity, BlockEntityTicker<T> ticker,
//? if (>=1.21.2 && <=1.21.11) {
            CallbackInfoReturnable<BlockEntityTickInvoker> cir) {
//?} else if >=26.1 {
//            CallbackInfoReturnable<TickingBlockEntity> cir) {
//?}
        if (entity instanceof CampfireBlockEntity && ticker instanceof RecipeBoundTicker<?> bound)
            ((CampfireRecipeState) entity).anticrash$bindRecipeGetter(bound.getter());
    }
}
//?}
