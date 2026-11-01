package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** {@code BrewTime} counts down every tick without marking the chunk for saving. */
@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin {
//? if <=1.21.11 {
    @Inject(method = "tick", at = @At("HEAD"))
    private static void anticrash$markBrewTimeDirty(World world, BlockPos pos, BlockState state,
//?} else if (>=26.1 && <=26.2) {
//    @Inject(method = "serverTick", at = @At("HEAD"))
//    private static void anticrash$markBrewTimeDirty(Level world, BlockPos pos, BlockState state,
//?} else if =26.3 {
//    @Inject(method = "serverTick", at = @At("HEAD"))
//    private static void anticrash$markBrewTimeDirty(net.minecraft.server.level.ServerLevel world, BlockPos pos, BlockState state,
//?}
                                                    BrewingStandBlockEntity stand, CallbackInfo callbackInfo) {
//? if <=1.21.11 {
        if (((BrewingStandBlockEntityTimerAccessor) stand).anticrash$getBrewTime() > 0) world.markDirty(pos);
//?} else if >=26.1 {
//        if (((BrewingStandBlockEntityTimerAccessor) stand).anticrash$getBrewTime() > 0) world.blockEntityChanged(pos);
//?}
    }

    @org.spongepowered.asm.mixin.Shadow private int brewTime;
//? if <=1.21.11 {
    @org.spongepowered.asm.mixin.Shadow private net.minecraft.item.Item itemBrewing;
//?} else if >=26.1 {
//    @org.spongepowered.asm.mixin.Shadow private net.minecraft.world.item.Item ingredient;
//?}

    /**
     * Vanilla does not save which ingredient the running brew started with. After a load it is
     * null, the first tick sees "ingredient changed" and resets the brew to zero, so a brew in
     * progress starts over (up to 400 ticks late).
     */
//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method = "writeData", at = @At("TAIL"))
    private void anticrash$saveBrewingItem(net.minecraft.storage.WriteView nbt, CallbackInfo callbackInfo) {
//?} else if (>=1.18 && <=1.20.4) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveBrewingItem(net.minecraft.nbt.NbtCompound nbt, CallbackInfo callbackInfo) {
//?} else if (>=1.20.5 && <=1.21.5) {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveBrewingItem(net.minecraft.nbt.NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo callbackInfo) {
//?} else if >=26.1 {
//    @Inject(method = "saveAdditional", at = @At("TAIL"))
//    private void anticrash$saveBrewingItem(net.minecraft.world.level.storage.ValueOutput nbt, CallbackInfo callbackInfo) {
//?} else if =1.16.1 {
//    @Inject(method = "writeNbt", at = @At("TAIL"))
//    private void anticrash$saveBrewingItem(net.minecraft.nbt.CompoundTag nbt, CallbackInfo callbackInfo) {
//?}
        if (!com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.savingLoadedChunk()) return;
        com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.write(nbt, extension -> {
            extension.remove("ItemBrewing");
//? if (>=1.19.3 && <=1.21.11) {
            if (brewTime > 0 && itemBrewing != null) {
                extension.putString("ItemBrewing", net.minecraft.registry.Registries.ITEM.getId(itemBrewing).toString());
//?} else if <=1.19.2 {
//            if (brewTime > 0 && itemBrewing != null) {
//                extension.putString("ItemBrewing", net.minecraft.util.registry.Registry.ITEM.getId(itemBrewing).toString());
//?} else if >=26.1 {
//            if (brewTime > 0 && ingredient != null) {
//                extension.putString("ItemBrewing", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(ingredient).toString());
//?}
            }
        });
    }

//? if (>=1.21.6 && <=1.21.11) {
    @Inject(method = "readData", at = @At("TAIL"))
    private void anticrash$loadBrewingItem(net.minecraft.storage.ReadView nbt, CallbackInfo callbackInfo) {
        net.minecraft.nbt.NbtCompound extension = com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.read(nbt);
        if (extension == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "ItemBrewing", 8) || brewTime <= 0) return;
        net.minecraft.util.Identifier id = net.minecraft.util.Identifier.tryParse(extension.getString("ItemBrewing").orElseThrow());
        if (id != null && net.minecraft.registry.Registries.ITEM.containsId(id)) {
            itemBrewing = net.minecraft.registry.Registries.ITEM.get(id);
//?} else if (>=1.19.3 && <=1.20.4) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadBrewingItem(net.minecraft.nbt.NbtCompound nbt, CallbackInfo callbackInfo) {
//        net.minecraft.nbt.NbtCompound extension = com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !extension.contains("ItemBrewing", 8) || brewTime <= 0) return;
//        net.minecraft.util.Identifier id = net.minecraft.util.Identifier.tryParse(extension.getString("ItemBrewing"));
//        if (id != null && net.minecraft.registry.Registries.ITEM.containsId(id)) {
//            itemBrewing = net.minecraft.registry.Registries.ITEM.get(id);
//?} else if (>=1.20.5 && <=1.21.4) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadBrewingItem(net.minecraft.nbt.NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo callbackInfo) {
//        net.minecraft.nbt.NbtCompound extension = com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !extension.contains("ItemBrewing", 8) || brewTime <= 0) return;
//        net.minecraft.util.Identifier id = net.minecraft.util.Identifier.tryParse(extension.getString("ItemBrewing"));
//        if (id != null && net.minecraft.registry.Registries.ITEM.containsId(id)) {
//            itemBrewing = net.minecraft.registry.Registries.ITEM.get(id);
//?} else if (>=1.18 && <=1.19.2) {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadBrewingItem(net.minecraft.nbt.NbtCompound nbt, CallbackInfo callbackInfo) {
//        net.minecraft.nbt.NbtCompound extension = com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !extension.contains("ItemBrewing", 8) || brewTime <= 0) return;
//        net.minecraft.util.Identifier id = net.minecraft.util.Identifier.tryParse(extension.getString("ItemBrewing"));
//        if (id != null && net.minecraft.util.registry.Registry.ITEM.containsId(id)) {
//            itemBrewing = net.minecraft.util.registry.Registry.ITEM.get(id);
//?} else if >=26.1 {
//    @Inject(method = "loadAdditional", at = @At("TAIL"))
//    private void anticrash$loadBrewingItem(net.minecraft.world.level.storage.ValueInput nbt, CallbackInfo callbackInfo) {
//        net.minecraft.nbt.CompoundTag extension = com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "ItemBrewing", 8) || brewTime <= 0) return;
//        net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.tryParse(extension.getString("ItemBrewing").orElseThrow());
//        if (id != null && net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(id)) {
//            ingredient = net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(id);
//?} else if =1.16.1 {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadBrewingItem(net.minecraft.nbt.CompoundTag nbt, CallbackInfo callbackInfo) {
//        net.minecraft.nbt.CompoundTag extension = com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !extension.contains("ItemBrewing", 8) || brewTime <= 0) return;
//        net.minecraft.util.Identifier id = net.minecraft.util.Identifier.tryParse(extension.getString("ItemBrewing"));
//        if (id != null && net.minecraft.util.registry.Registry.ITEM.containsId(id)) {
//            itemBrewing = net.minecraft.util.registry.Registry.ITEM.get(id);
//?} else if =1.21.5 {
//    @Inject(method = "readNbt", at = @At("TAIL"))
//    private void anticrash$loadBrewingItem(net.minecraft.nbt.NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registries, CallbackInfo callbackInfo) {
//        net.minecraft.nbt.NbtCompound extension = com.atsukimc.anticrash.runtime.RuntimeExtensionNbt.read(nbt);
//        if (extension == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(extension, "ItemBrewing", 8) || brewTime <= 0) return;
//        net.minecraft.util.Identifier id = net.minecraft.util.Identifier.tryParse(extension.getString("ItemBrewing").orElseThrow());
//        if (id != null && net.minecraft.registry.Registries.ITEM.containsId(id)) {
//            itemBrewing = net.minecraft.registry.Registries.ITEM.get(id);
//?}
        }
    }
}
