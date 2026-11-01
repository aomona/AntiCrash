package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.world.storage.RegionBasedStorage;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.world.level.chunk.storage.RegionFileStorage;
//?}

//? if <=1.21.11 {
@Mixin(RegionBasedStorage.class)
//?} else if >=26.1 {
//@Mixin(RegionFileStorage.class)
//?}
public interface RegionBasedStorageAccessorMixin {
//? if <=1.21.11 {
    @Accessor("directory")
//?} else if >=26.1 {
//    @Accessor("folder")
//?}
    Path anticrash$getDirectory();
}
