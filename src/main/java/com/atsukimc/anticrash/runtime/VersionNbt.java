//? if >=1.21.5 {
package com.atsukimc.anticrash.runtime;

import net.minecraft.nbt.*;

/** 1.21.5 removed type-aware contains and permits heterogeneous lists. Never erase malformed
 * recovery data by accepting a wrong type as an empty/default value. */
public final class VersionNbt {
    private VersionNbt() { }
    /** Sparse true-only flags default to false only when absent, never when malformed. */
//? if (>=1.21.5 && <=1.21.11) {
    public static boolean sparseFlag(NbtCompound nbt, String key) {
//?} else if >=26.1 {
//    public static boolean sparseFlag(CompoundTag nbt, String key) {
//?}
        return !nbt.contains(key) ? false : nbt.getBoolean(key).orElseThrow(
                () -> new IllegalArgumentException("Invalid boolean recovery flag: " + key));
    }

//? if (>=1.21.5 && <=1.21.11) {
    public static NbtElement item(net.minecraft.item.ItemStack stack,
                                 net.minecraft.registry.RegistryWrapper.WrapperLookup registries) {
        return net.minecraft.item.ItemStack.OPTIONAL_CODEC.encodeStart(registries.getOps(NbtOps.INSTANCE), stack)
//?} else if >=26.1 {
//    public static Tag item(net.minecraft.world.item.ItemStack stack,
//                                 net.minecraft.core.HolderLookup.Provider registries) {
//        return net.minecraft.world.item.ItemStack.OPTIONAL_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack)
//?}
                .getOrThrow();
    }

//? if (>=1.21.5 && <=1.21.11) {
    public static NbtElement effect(net.minecraft.entity.effect.StatusEffectInstance effect) {
//?} else if >=26.1 {
//    public static Tag effect(net.minecraft.world.effect.MobEffectInstance effect) {
//?}
        // StatusEffect.ENTRY_CODEC uses the static STATUS_EFFECT registry, as verified in bytecode.
//? if (>=1.21.5 && <=1.21.11) {
        return net.minecraft.entity.effect.StatusEffectInstance.CODEC.encodeStart(NbtOps.INSTANCE, effect).getOrThrow();
//?} else if >=26.1 {
//        return net.minecraft.world.effect.MobEffectInstance.CODEC.encodeStart(NbtOps.INSTANCE, effect).getOrThrow();
//?}
    }

//? if (>=1.21.5 && <=1.21.11) {
    public static net.minecraft.entity.effect.StatusEffectInstance effect(NbtCompound nbt) {
        return net.minecraft.entity.effect.StatusEffectInstance.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow();
//?} else if >=26.1 {
//    public static net.minecraft.world.effect.MobEffectInstance effect(CompoundTag nbt) {
//        return net.minecraft.world.effect.MobEffectInstance.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow();
//?}
    }

//? if (>=1.21.5 && <=1.21.11) {
    public static boolean contains(NbtCompound nbt, String key, int type) {
        NbtElement value = nbt.get(key);
        return value != null && (type == 99 ? value instanceof AbstractNbtNumber : value.getType() == type);
//?} else if >=26.1 {
//    public static boolean contains(CompoundTag nbt, String key, int type) {
//        Tag value = nbt.get(key);
//        return value != null && (type == 99 ? value instanceof NumericTag : value.getId() == type);
//?}
    }

//? if (>=1.21.5 && <=1.21.11) {
    public static NbtList list(NbtCompound nbt, String key, int type) {
        NbtElement value = nbt.get(key);
        if (value == null) return new NbtList();
        if (!(value instanceof NbtList list)) throw new IllegalArgumentException("Expected NBT list: " + key);
        for (NbtElement entry : list) {
            if (entry.getType() != type) throw new IllegalArgumentException("Wrong NBT list element type: " + key);
//?} else if >=26.1 {
//    public static ListTag list(CompoundTag nbt, String key, int type) {
//        Tag value = nbt.get(key);
//        if (value == null) return new ListTag();
//        if (!(value instanceof ListTag list)) throw new IllegalArgumentException("Expected NBT list: " + key);
//        for (Tag entry : list) {
//            if (entry.getId() != type) throw new IllegalArgumentException("Wrong NBT list element type: " + key);
//?}
        }
        return list;
    }
}
//?}
