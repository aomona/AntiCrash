//? if >=1.19 {
package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.RuntimeExtensionNbt;
//? if (>=1.19 && <=1.21.11) {
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
//?} else if >=26.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.level.block.entity.CampfireBlockEntity;
//?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Restores only the semantic cached identifier on the newly constructed Vanilla MatchGetter. */
@Mixin(CampfireBlockEntity.class)
//? if (>=1.21.2 && <=1.21.11) {
public abstract class CampfireRecipeMatchingMixin implements com.atsukimc.anticrash.runtime.CampfireRecipeState {
    @org.spongepowered.asm.mixin.Unique private RecipeMatchGetterAccessorMixin anticrash$getter;
    @org.spongepowered.asm.mixin.Unique private net.minecraft.registry.RegistryKey<net.minecraft.recipe.Recipe<?>> anticrash$savedId;
    @org.spongepowered.asm.mixin.Unique private boolean anticrash$pending;
//?} else if (>=1.19 && <=1.21.1) {
//public abstract class CampfireRecipeMatchingMixin {
//    @Shadow @Final private RecipeManager.MatchGetter matchGetter;
//?} else if >=26.1 {
//public abstract class CampfireRecipeMatchingMixin implements com.atsukimc.anticrash.runtime.CampfireRecipeState {
//    @org.spongepowered.asm.mixin.Unique private RecipeMatchGetterAccessorMixin anticrash$getter;
//    @org.spongepowered.asm.mixin.Unique private net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> anticrash$savedId;
//    @org.spongepowered.asm.mixin.Unique private boolean anticrash$pending;
//?}

//? if (>=1.21.6 && <=1.21.11) {
    @Override
    public void anticrash$bindRecipeGetter(RecipeMatchGetterAccessorMixin getter) {
        if (anticrash$pending && getter != null) {
            getter.anticrash$setRecipeId(anticrash$savedId);
            anticrash$pending = false;
        }
        anticrash$getter = getter;
        if (getter == null) { anticrash$savedId = null; anticrash$pending = false; }
    }

    @Inject(method = "litServerTick", at = @At("HEAD"))
    private static void anticrash$restoreRecipeBeforeFirstTick(net.minecraft.server.world.ServerWorld world,
            net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state,
            CampfireBlockEntity campfire, net.minecraft.recipe.ServerRecipeManager.MatchGetter getter, CallbackInfo ci) {
        ((com.atsukimc.anticrash.runtime.CampfireRecipeState) campfire)
                .anticrash$bindRecipeGetter((RecipeMatchGetterAccessorMixin) getter);
    }

    @Inject(method = "writeData", at = @At("TAIL"))
    private void anticrash$saveRecipeMatch(net.minecraft.storage.WriteView nbt, CallbackInfo ci) {
//?} else if (>=1.19 && <=1.20.4) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveRecipeMatch(NbtCompound nbt, CallbackInfo ci) {
//?} else if (>=1.20.5 && <=1.21.1) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveRecipeMatch(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
//?} else if (>=1.21.2 && <=1.21.5) {
//    @Override
//    public void anticrash$bindRecipeGetter(RecipeMatchGetterAccessorMixin getter) {
//        if (anticrash$pending && getter != null) {
//            getter.anticrash$setRecipeId(anticrash$savedId);
//            anticrash$pending = false;
//        }
//        anticrash$getter = getter;
//        if (getter == null) { anticrash$savedId = null; anticrash$pending = false; }
//    }
//
//    @Inject(method = "litServerTick", at = @At("HEAD"))
//    private static void anticrash$restoreRecipeBeforeFirstTick(net.minecraft.server.world.ServerWorld world,
//            net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state,
//            CampfireBlockEntity campfire, net.minecraft.recipe.ServerRecipeManager.MatchGetter getter, CallbackInfo ci) {
//        ((com.atsukimc.anticrash.runtime.CampfireRecipeState) campfire)
//                .anticrash$bindRecipeGetter((RecipeMatchGetterAccessorMixin) getter);
//    }
//
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveRecipeMatch(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
//?} else if >=26.1 {
//    @Override
//    public void anticrash$bindRecipeGetter(RecipeMatchGetterAccessorMixin getter) {
//        if (anticrash$pending && getter != null) {
//            getter.anticrash$setRecipeId(anticrash$savedId);
//            anticrash$pending = false;
//        }
//        anticrash$getter = getter;
//        if (getter == null) { anticrash$savedId = null; anticrash$pending = false; }
//    }
//
//    @Inject(method = "cookTick", at = @At("HEAD"))
//    private static void anticrash$restoreRecipeBeforeFirstTick(net.minecraft.server.level.ServerLevel world,
//            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state,
//            CampfireBlockEntity campfire, net.minecraft.world.item.crafting.RecipeManager.CachedCheck getter, CallbackInfo ci) {
//        ((com.atsukimc.anticrash.runtime.CampfireRecipeState) campfire)
//                .anticrash$bindRecipeGetter((RecipeMatchGetterAccessorMixin) getter);
//    }
//
//    @Inject(method = "saveAdditional", at = @At("TAIL"))
//    private void anticrash$saveRecipeMatch(net.minecraft.world.level.storage.ValueOutput nbt, CallbackInfo ci) {
//?}
        if (!RuntimeExtensionNbt.savingLoadedChunk()) return;
//? if >=1.21.2 {
        var id = anticrash$getter == null ? anticrash$savedId : anticrash$getter.anticrash$getRecipeId();
//?} else if (>=1.19 && <=1.21.1) {
//        Identifier id = ((RecipeMatchGetterAccessorMixin) matchGetter).anticrash$getRecipeId();
//?}
        RuntimeExtensionNbt.write(nbt, extension -> {
            extension.remove("RecipeMatch");
//? if (>=1.21.2 && <=1.21.11) {
            if (id != null) extension.putString("RecipeMatch", id.getValue().toString());
//?} else if (>=1.19 && <=1.21.1) {
//            if (id != null) extension.putString("RecipeMatch", id.toString());
//?} else if >=26.1 {
//            if (id != null) extension.putString("RecipeMatch", id.identifier().toString());
//?}
        });
    }

//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method = "readData", at = @At("TAIL"))
    private void anticrash$loadRecipeMatch(net.minecraft.storage.ReadView nbt, CallbackInfo ci) {
        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//?} else if (>=1.19 && <=1.20.4) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadRecipeMatch(NbtCompound nbt, CallbackInfo ci) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//?} else if (>=1.20.5 && <=1.21.5) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadRecipeMatch(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//?} else if >=26.1 {
//    @Inject(method = "loadAdditional", at = @At("TAIL"))
//    private void anticrash$loadRecipeMatch(net.minecraft.world.level.storage.ValueInput nbt, CallbackInfo ci) {
//        CompoundTag extension = RuntimeExtensionNbt.read(nbt);
//?}
        if (extension == null) return;
//? if (>=1.21.5 && <=1.21.11) {
        anticrash$savedId = com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "RecipeMatch", 8)
                ? net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.RECIPE,
                        Identifier.of(extension.getString("RecipeMatch").orElseThrow())) : null;
        anticrash$pending = true;
//?} else if (>=1.19 && <=1.21.1) {
//        ((RecipeMatchGetterAccessorMixin) matchGetter).anticrash$setRecipeId(
//                extension.contains("RecipeMatch", 8) ? Identifier.tryParse(extension.getString("RecipeMatch")) : null);
//?} else if (>=1.21.2 && <=1.21.4) {
//        anticrash$savedId = extension.contains("RecipeMatch", 8)
//                ? net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.RECIPE,
//                        Identifier.of(extension.getString("RecipeMatch"))) : null;
//        anticrash$pending = true;
//?} else if >=26.1 {
//        anticrash$savedId = com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "RecipeMatch", 8)
//                ? net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE,
//                        Identifier.parse(extension.getString("RecipeMatch").orElseThrow())) : null;
//        anticrash$pending = true;
//?}
    }
}
//?}
