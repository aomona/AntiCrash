//? if >=1.21.2 {
package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.RecipeBoundTicker;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//? if (>=1.21.2 && <=1.21.11) {
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.*;
import net.minecraft.recipe.*;
import net.minecraft.world.World;
//?} else if >=26.1 {
//import net.minecraft.world.level.block.entity.*;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.item.crafting.RecipeManager;
//import net.minecraft.world.item.crafting.RecipeType;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.CampfireBlock;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.BlockEntityTicker;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/** 1.21.2 getTicker captures MatchGetter in a lambda rather than storing it on the BE.
 * Bind at ticker construction, not first tick: relighting may be checkpointed before any tick. */
@Mixin(CampfireBlock.class)
public abstract class CampfireTickerBindingMixin {
    @Unique private static final ThreadLocal<java.util.ArrayDeque<RecipeMatchGetterAccessorMixin[]>> anticrash$scopes =
            ThreadLocal.withInitial(java.util.ArrayDeque::new);

    @WrapMethod(method = "getTicker")
//? if (>=1.21.2 && <=1.21.11) {
    private <T extends BlockEntity> BlockEntityTicker<T> anticrash$bind(World world, BlockState state,
//?} else if >=26.1 {
//    private <T extends BlockEntity> BlockEntityTicker<T> anticrash$bind(Level world, BlockState state,
//?}
            BlockEntityType<T> type, Operation<BlockEntityTicker<T>> original) {
        var stack = anticrash$scopes.get();
        RecipeMatchGetterAccessorMixin[] scope = new RecipeMatchGetterAccessorMixin[1];
        stack.push(scope);
        try {
            BlockEntityTicker<T> ticker = original.call(world, state, type);
//? if (>=1.21.2 && <=1.21.8) {
            return ticker == null || world.isClient ? ticker : new RecipeBoundTicker<>(ticker, scope[0]);
//?} else if (>=1.21.9 && <=1.21.11) {
//            return ticker == null || world.isClient() ? ticker : new RecipeBoundTicker<>(ticker, scope[0]);
//?} else if >=26.1 {
//            return ticker == null || world.isClientSide() ? ticker : new RecipeBoundTicker<>(ticker, scope[0]);
//?}
        } finally {
            stack.pop();
            if (stack.isEmpty()) anticrash$scopes.remove();
        }
    }

    @WrapOperation(method = "getTicker", at = @At(value = "INVOKE", target =
//? if (>=1.21.2 && <=1.21.11) {
            "Lnet/minecraft/recipe/ServerRecipeManager;createCachedMatchGetter(Lnet/minecraft/recipe/RecipeType;)Lnet/minecraft/recipe/ServerRecipeManager$MatchGetter;"))
    private ServerRecipeManager.MatchGetter<?, ?> anticrash$capture(RecipeType<?> type,
            Operation<ServerRecipeManager.MatchGetter<?, ?>> original) {
//?} else if >=26.1 {
//            "Lnet/minecraft/world/item/crafting/RecipeManager;createCheck(Lnet/minecraft/world/item/crafting/RecipeType;)Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;"))
//    private RecipeManager.CachedCheck<?, ?> anticrash$capture(RecipeType<?> type,
//            Operation<RecipeManager.CachedCheck<?, ?>> original) {
//?}
        var getter = original.call(type);
        var stack = anticrash$scopes.get();
        if (!stack.isEmpty()) stack.peek()[0] = (RecipeMatchGetterAccessorMixin) getter;
        return getter;
    }
}
//?}
