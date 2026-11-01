package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.ExtensionCarrier;
//? if <=1.21.11 {
import net.minecraft.world.chunk.WorldChunk;
//?} else if >=26.1 {
//import net.minecraft.world.level.chunk.LevelChunk;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

//? if <=1.21.11 {
@Mixin(WorldChunk.class)
//?} else if >=26.1 {
//@Mixin(LevelChunk.class)
//?}
public abstract class WorldChunkExtensionMixin implements ExtensionCarrier {
    @Unique private boolean anticrash$extensionOnDisk;

    @Override
    public boolean anticrash$extensionOnDisk() {
        return anticrash$extensionOnDisk;
    }

    @Override
    public void anticrash$setExtensionOnDisk(boolean onDisk) {
        anticrash$extensionOnDisk = onDisk;
    }
}
