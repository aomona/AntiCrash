package com.atsukimc.anticrash.snapshot;
//? if (>=1.18 && <=1.21.11) {

import net.minecraft.nbt.NbtCompound;
//?} else if =1.16.1 || >=26.1 {
//?}

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
//? if (>=1.18 && <=1.21.11) {
//?} else if =1.16.1 || >=26.1 {
//import net.minecraft.nbt.CompoundTag;
//?}

/**
 * The queued NbtCompounds a checkpoint cut took from the storage IO queues and will write into its
 * generation after the server resumes. Identity, not equality: only these exact objects are shared
 * with the IO thread.
 */
public final class SealedNbt {
//? if (>=1.18 && <=1.21.11) {
    private static final Set<NbtCompound> SEALED = Collections.newSetFromMap(new IdentityHashMap<>());
//?} else if =1.16.1 || >=26.1 {
//    private static final Set<CompoundTag> SEALED = Collections.newSetFromMap(new IdentityHashMap<>());
//?}
    private static volatile boolean any;

    private SealedNbt() {
    }

//? if (>=1.18 && <=1.21.11) {
    public static boolean isSealed(NbtCompound nbt) {
//?} else if =1.16.1 || >=26.1 {
//    public static boolean isSealed(CompoundTag nbt) {
//?}
        if (!any || nbt == null) return false;
        synchronized (SEALED) {
            return SEALED.contains(nbt);
        }
    }

    static void seal(Collection<PendingRegionWrite> writes) {
        synchronized (SEALED) {
            // Only one checkpoint cut is ever outstanding; whatever an abandoned one left is stale.
            SEALED.clear();
            for (PendingRegionWrite write : writes) if (write.nbt() != null) SEALED.add(write.nbt());
            any = !SEALED.isEmpty();
        }
    }

    public static void clear() {
        synchronized (SEALED) {
            SEALED.clear();
            any = false;
        }
    }
}
