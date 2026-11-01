//? if >=1.21.6 {
package com.atsukimc.anticrash.runtime;

//? if (>=1.21.6 && <=1.21.11) {
//?} else if >=26.1 {
//import net.minecraft.core.HolderLookup;
//?}
import net.minecraft.nbt.*;
//? if (>=1.21.6 && <=1.21.11) {
import net.minecraft.storage.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.entity.player.PlayerAbilities;
//?} else if >=26.1 {
//import net.minecraft.world.level.storage.*;
//import net.minecraft.world.entity.player.Abilities;
//import net.minecraft.world.level.storage.TagValueInput;
//?}
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VersionStorageTest {
    @org.junit.jupiter.api.AfterEach void clearScope() {
        RuntimeExtensionNbt.loadedChunkSave(false); RuntimeExtensionNbt.entitySave(false);
    }
//? if (>=1.21.6 && <=1.21.11) {
    private final RegistryWrapper.WrapperLookup registries = RegistryWrapper.WrapperLookup.of(java.util.stream.Stream.empty());
//?} else if >=26.1 {
//    private final HolderLookup.Provider registries = HolderLookup.Provider.create(java.util.stream.Stream.empty());
//?}
    @Test void viewExtensionRoundTripPreservesPistonStateAndMergesKeys() {
        RuntimeExtensionNbt.loadedChunkSave(true);
        var view = VersionStorage.writer(registries);
        RuntimeExtensionNbt.write(view, n -> n.putFloat("Progress", 0.5f));
        RuntimeExtensionNbt.write(view, n -> n.putLong("SavedWorldTime", 31));
//? if (>=1.21.6 && <=1.21.11) {
        var read = NbtReadView.create(new VersionStorage.ThrowingReporter("test"), registries, view.getNbt());
//?} else if >=26.1 {
//        var read = TagValueInput.create(new VersionStorage.ThrowingReporter("test"), registries, view.buildResult());
//?}
        var extension = RuntimeExtensionNbt.read(read);
        assertNotNull(extension); assertEquals(0.5f, extension.getFloat("Progress").orElseThrow());
        assertEquals(31, extension.getLong("SavedWorldTime").orElseThrow());
    }
    @Test void malformedViewDataCannotBeIgnored() {
//? if (>=1.21.6 && <=1.21.11) {
        var nbt = new NbtCompound(); nbt.putString(RuntimeExtensionNbt.KEY, "invalid");
        var read = NbtReadView.create(new VersionStorage.ThrowingReporter("test"), registries, nbt);
//?} else if >=26.1 {
//        var nbt = new CompoundTag(); nbt.putString(RuntimeExtensionNbt.KEY, "invalid");
//        var read = TagValueInput.create(new VersionStorage.ThrowingReporter("test"), registries, nbt);
//?}
        assertThrows(IllegalStateException.class, () -> RuntimeExtensionNbt.read(read));
    }
    @Test void encodingFailureIsNotAnIncompleteSuccessfulSnapshot() {
        var view = VersionStorage.writer(registries);
        var rejecting = com.mojang.serialization.Codec.STRING.flatXmap(
                s -> com.mojang.serialization.DataResult.error(() -> "reject"),
                s -> com.mojang.serialization.DataResult.error(() -> "reject"));
//? if (>=1.21.6 && <=1.21.11) {
        assertThrows(IllegalStateException.class, () -> view.put("invalid", rejecting, "bad"));
//?} else if >=26.1 {
//        assertThrows(IllegalStateException.class, () -> view.store("invalid", rejecting, "bad"));
//?}
    }
    @Test void abilitiesPreserveFlyingAndSpeedsWithoutAWorld() {
//? if (>=1.21.6 && <=1.21.11) {
        var abilities = new PlayerAbilities(); abilities.flying = true; abilities.allowFlying = true;
        abilities.setFlySpeed(0.3f); abilities.setWalkSpeed(0.17f);
        var restored = new PlayerAbilities(); restored.unpack(VersionStorage.abilities(VersionStorage.abilities(abilities)));
        assertTrue(restored.flying); assertTrue(restored.allowFlying);
        assertEquals(0.3f, restored.getFlySpeed()); assertEquals(0.17f, restored.getWalkSpeed());
        assertThrows(IllegalArgumentException.class, () -> VersionStorage.abilities(new NbtCompound()));
//?} else if >=26.1 {
//        var abilities = new Abilities(); abilities.flying = true; abilities.mayfly = true;
//        abilities.setFlyingSpeed(0.3f); abilities.setWalkingSpeed(0.17f);
//        var restored = new Abilities(); restored.apply(VersionStorage.abilities(VersionStorage.abilities(abilities)));
//        assertTrue(restored.flying); assertTrue(restored.mayfly);
//        assertEquals(0.3f, restored.getFlyingSpeed()); assertEquals(0.17f, restored.getWalkingSpeed());
//        assertThrows(IllegalArgumentException.class, () -> VersionStorage.abilities(new CompoundTag()));
//?}
    }
    @Test void invalidInventoryDoesNotPartiallyApply() {
//? if (>=1.21.6 && <=1.21.11) {
        var inventory = new net.minecraft.inventory.SimpleInventory(27);
        var nbt = new NbtList(); nbt.add(NbtString.of("invalid"));
//?} else if >=26.1 {
//        var inventory = new net.minecraft.world.SimpleContainer(27);
//        var nbt = new ListTag(); nbt.add(StringTag.valueOf("invalid"));
//?}
        assertThrows(RuntimeException.class, () -> VersionStorage.inventory(nbt, 27, registries));
        assertTrue(inventory.isEmpty());
    }
    @Test void emptyEnderChestRoundTripsItsFixedSlotShape() {
//? if (>=1.21.6 && <=1.21.11) {
        var inventory = new net.minecraft.inventory.SimpleInventory(27);
//?} else if >=26.1 {
//        var inventory = new net.minecraft.world.SimpleContainer(27);
//?}
        var encoded = VersionStorage.inventory(inventory, registries);
        var decoded = VersionStorage.inventory(encoded, 27, registries);
        assertEquals(27, decoded.size());
        VersionStorage.applyInventory(inventory, decoded); assertTrue(inventory.isEmpty());
    }
}
//?}
