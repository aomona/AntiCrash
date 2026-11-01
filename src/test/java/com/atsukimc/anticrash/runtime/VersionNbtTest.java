//? if >=1.21.5 {
package com.atsukimc.anticrash.runtime;

import net.minecraft.nbt.*;
//? if (>=1.21.5 && <=1.21.11) {
import net.minecraft.entity.effect.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
//?} else if >=26.1 {
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.effect.MobEffects;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.world.effect.*;
//?}
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VersionNbtTest {
    @Test void sparseFalseFlagsMayBeAbsentButCannotHaveAnInvalidType() {
//? if (>=1.21.5 && <=1.21.11) {
        var nbt=new NbtCompound();
//?} else if >=26.1 {
//        var nbt=new CompoundTag();
//?}
        assertFalse(VersionNbt.sparseFlag(nbt,"Ticked"));
        nbt.putBoolean("Ticked",true);
        assertTrue(VersionNbt.sparseFlag(nbt,"Ticked"));
        assertFalse(VersionNbt.sparseFlag(nbt,"Water"));
        nbt.putBoolean("Water",false);
        assertFalse(VersionNbt.sparseFlag(nbt,"Water"));
        nbt.putString("Water","invalid");
        assertThrows(IllegalArgumentException.class,()->VersionNbt.sparseFlag(nbt,"Water"));
    }
    @Test void wrongTypeIsNotAnEmptyRecoveryList() {
//? if (>=1.21.5 && <=1.21.11) {
        NbtCompound nbt = new NbtCompound(); nbt.putString("Entities", "bad");
//?} else if >=26.1 {
//        CompoundTag nbt = new CompoundTag(); nbt.putString("Entities", "bad");
//?}
        assertFalse(VersionNbt.contains(nbt, "Entities", 9));
        assertThrows(IllegalArgumentException.class, () -> VersionNbt.list(nbt, "Entities", 10));
        assertTrue(VersionNbt.list(nbt, "missing", 10).isEmpty());
    }
    @Test void heterogeneousListsCannotDiscardInvalidPassengers() {
//? if (>=1.21.5 && <=1.21.11) {
        NbtList list = new NbtList(); list.add(new NbtCompound()); list.add(NbtString.of("bad"));
        NbtCompound nbt = new NbtCompound(); nbt.put("Passengers", list);
//?} else if >=26.1 {
//        ListTag list = new ListTag(); list.add(new CompoundTag()); list.add(StringTag.valueOf("bad"));
//        CompoundTag nbt = new CompoundTag(); nbt.put("Passengers", list);
//?}
        assertThrows(IllegalArgumentException.class, () -> VersionNbt.list(nbt, "Passengers", 10));
    }
    @Test void numericContainsStillRecognizesAllNumericTypes() {
//? if (>=1.21.5 && <=1.21.11) {
        NbtCompound nbt = new NbtCompound(); nbt.putDouble("n", 2);
//?} else if >=26.1 {
//        CompoundTag nbt = new CompoundTag(); nbt.putDouble("n", 2);
//?}
        assertTrue(VersionNbt.contains(nbt, "n", 99)); assertFalse(VersionNbt.contains(nbt, "n", 8));
    }
    @Test void statusEffectCodecPreservesTimingAndFlags() {
//? if (>=1.21.5 && <=1.21.11) {
        var effect = new StatusEffectInstance(StatusEffects.SPEED, 37, 2, true, false, false);
        var restored = VersionNbt.effect((NbtCompound) VersionNbt.effect(effect));
        assertEquals(effect.getEffectType(), restored.getEffectType()); assertEquals(37, restored.getDuration());
//?} else if >=26.1 {
//        var effect = new MobEffectInstance(MobEffects.SPEED, 37, 2, true, false, false);
//        var restored = VersionNbt.effect((CompoundTag) VersionNbt.effect(effect));
//        assertEquals(effect.getEffect(), restored.getEffect()); assertEquals(37, restored.getDuration());
//?}
        assertEquals(2, restored.getAmplifier()); assertTrue(restored.isAmbient());
//? if (>=1.21.5 && <=1.21.11) {
        assertFalse(restored.shouldShowParticles()); assertFalse(restored.shouldShowIcon());
        assertThrows(RuntimeException.class, () -> VersionNbt.effect(new NbtCompound()));
//?} else if >=26.1 {
//        assertFalse(restored.isVisible()); assertFalse(restored.showIcon());
//        assertThrows(RuntimeException.class, () -> VersionNbt.effect(new CompoundTag()));
//?}
    }
    @Test void emptyInventoryItemRemainsAnEmptyCompound() {
//? if (>=1.21.5 && <=1.21.11) {
        assertTrue(((NbtCompound) VersionNbt.item(ItemStack.EMPTY,
                RegistryWrapper.WrapperLookup.of(java.util.stream.Stream.empty()))).isEmpty());
//?} else if >=26.1 {
//        assertTrue(((CompoundTag) VersionNbt.item(ItemStack.EMPTY,
//                HolderLookup.Provider.create(java.util.stream.Stream.empty()))).isEmpty());
//?}
    }
}
//?}
