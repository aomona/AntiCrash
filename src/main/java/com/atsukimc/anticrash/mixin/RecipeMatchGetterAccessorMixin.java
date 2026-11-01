//? if >=1.19 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.19 && <=1.21.11) {
import net.minecraft.util.Identifier;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Audited anonymous MatchGetter implementation, not a replacement of recipe-manager maps. */
//? if (>=1.21.2 && <=1.21.11) {
@Mixin(targets = "net.minecraft.recipe.ServerRecipeManager$1")
//?} else if (>=1.19 && <=1.21.1) {
//@Mixin(targets = "net.minecraft.recipe.RecipeManager$1")
//?} else if >=26.1 {
//@Mixin(targets = "net.minecraft.world.item.crafting.RecipeManager$1")
//?}
public interface RecipeMatchGetterAccessorMixin {
//? if (>=1.21.2 && <=1.21.11) {
    @Accessor("id") net.minecraft.registry.RegistryKey<net.minecraft.recipe.Recipe<?>> anticrash$getRecipeId();
    @Accessor("id") void anticrash$setRecipeId(net.minecraft.registry.RegistryKey<net.minecraft.recipe.Recipe<?>> id);
//?} else if (>=1.19 && <=1.21.1) {
//    @Accessor("id") Identifier anticrash$getRecipeId();
//    @Accessor("id") void anticrash$setRecipeId(Identifier id);
//?} else if >=26.1 {
//    @Accessor("lastRecipe") net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> anticrash$getRecipeId();
//    @Accessor("lastRecipe") void anticrash$setRecipeId(net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> id);
//?}
}
//?}
