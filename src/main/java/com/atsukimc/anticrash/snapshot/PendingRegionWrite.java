package com.atsukimc.anticrash.snapshot;

//? if (>=1.18 && <=1.21.11) {
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;

//?} else if >=26.1 {
//?} else if =1.16.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.util.math.ChunkPos;
//
//?}
import java.nio.file.Path;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.ChunkPos;
//?}

/**
 * A chunk, entity or POI write that was queued but not yet on disk at a checkpoint cut.
 *
 * @param directory the absolute region storage directory it belongs to
 * @param nbt       the queued data; null deletes the chunk, as in Vanilla
 */
//? if (>=1.18 && <=1.21.11) {
public record PendingRegionWrite(Path directory, ChunkPos pos, NbtCompound nbt) {
//?} else if =1.16.1 || >=26.1 {
//public record PendingRegionWrite(Path directory, ChunkPos pos, CompoundTag nbt) {
//?}
    public Path regionFile() {
        return directory.resolve("r." + pos.getRegionX() + "." + pos.getRegionZ() + ".mca");
    }

    public Path externalChunkFile() {
//? if <=1.21.11 {
        return directory.resolve("c." + pos.x + "." + pos.z + ".mcc");
//?} else if >=26.1 {
//        return directory.resolve("c." + pos.x() + "." + pos.z() + ".mcc");
//?}
    }
}
