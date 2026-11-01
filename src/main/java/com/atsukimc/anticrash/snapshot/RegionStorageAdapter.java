//? if >=1.20.5 {
package com.atsukimc.anticrash.snapshot;

//? if (>=1.20.5 && <=1.21.11) {
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.storage.StorageKey;
//?} else if >=26.1 {
//import net.minecraft.core.registries.Registries;
//import net.minecraft.resources.Identifier;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
//?}

/** The StorageKey labels diagnostics/JFR; detached file contents/coordinates stay authoritative. */
public final class RegionStorageAdapter {
//? if (>=1.21 && <=1.21.11) {
    public static final StorageKey KEY = new StorageKey("anticrash-checkpoint",
            RegistryKey.of(RegistryKeys.WORLD, Identifier.of("anticrash", "offline")), "checkpoint-overlay");
//?} else if >=26.1 {
//    public static final RegionStorageInfo KEY = new RegionStorageInfo("anticrash-checkpoint",
//            ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath("anticrash", "offline")), "checkpoint-overlay");
//?} else if (>=1.20.5 && <=1.20.6) {
//    public static final StorageKey KEY = new StorageKey("anticrash-checkpoint",
//            RegistryKey.of(RegistryKeys.WORLD, new Identifier("anticrash", "offline")), "checkpoint-overlay");
//?}
    private RegionStorageAdapter() {}
}
//?}
