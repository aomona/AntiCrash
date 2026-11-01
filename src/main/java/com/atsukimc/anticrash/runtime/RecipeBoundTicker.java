//? if >=1.21.2 {
package com.atsukimc.anticrash.runtime;

import com.atsukimc.anticrash.mixin.RecipeMatchGetterAccessorMixin;
//? if (>=1.21.2 && <=1.21.11) {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.BlockEntityTicker;
//import net.minecraft.world.level.block.state.BlockState;
//?}

/** A native ticker delegate plus its semantic recipe cache; never serialized as a Java object. */
public record RecipeBoundTicker<T extends BlockEntity>(BlockEntityTicker<T> delegate,
        RecipeMatchGetterAccessorMixin getter) implements BlockEntityTicker<T> {
//? if (>=1.21.2 && <=1.21.11) {
    @Override public void tick(World world, BlockPos pos, BlockState state, T entity) {
//?} else if >=26.1 {
//    @Override public void tick(Level world, BlockPos pos, BlockState state, T entity) {
//?}
        delegate.tick(world, pos, state, entity);
    }
}
//?}
