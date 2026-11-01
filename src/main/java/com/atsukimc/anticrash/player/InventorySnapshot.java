package com.atsukimc.anticrash.player;
//? if <=1.21.11 {

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
//?} else if >=26.1 {
//?}

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//?}

public final class InventorySnapshot {
    public final List<String> main = new ArrayList<>();
    public final List<String> armor = new ArrayList<>();
    public final List<String> offhand = new ArrayList<>();
    public int selectedSlot;

//? if <=1.21.11 {
    public static InventorySnapshot capture(PlayerEntity player, boolean mainEnabled, boolean armorEnabled, boolean offhandEnabled, boolean selectedEnabled) throws IOException {
//?} else if >=26.1 {
//    public static InventorySnapshot capture(Player player, boolean mainEnabled, boolean armorEnabled, boolean offhandEnabled, boolean selectedEnabled) throws IOException {
//?}
        if (!(mainEnabled || armorEnabled || offhandEnabled || selectedEnabled)) return null;
        InventorySnapshot snapshot = new InventorySnapshot();
//? if (>=1.21.5 && <=1.21.11) {
        PlayerInventory inventory = player.getInventory();
        if (mainEnabled) for (ItemStack stack : inventory.getMainStacks()) snapshot.main.add(NbtCodec.encode(com.atsukimc.anticrash.runtime.VersionNbt.item(stack, player.getRegistryManager())));
        if (armorEnabled) for (ItemStack stack : equipmentStacks(inventory, 36, 4)) snapshot.armor.add(NbtCodec.encode(com.atsukimc.anticrash.runtime.VersionNbt.item(stack, player.getRegistryManager())));
        if (offhandEnabled) for (ItemStack stack : equipmentStacks(inventory, 40, 1)) snapshot.offhand.add(NbtCodec.encode(com.atsukimc.anticrash.runtime.VersionNbt.item(stack, player.getRegistryManager())));
        if (selectedEnabled) snapshot.selectedSlot = inventory.getSelectedSlot();
//?} else if (>=1.18 && <=1.20.4) {
//        PlayerInventory inventory = player.getInventory();
//        if (mainEnabled) for (ItemStack stack : inventory.main) snapshot.main.add(NbtCodec.encode(stack.writeNbt(new net.minecraft.nbt.NbtCompound())));
//        if (armorEnabled) for (ItemStack stack : inventory.armor) snapshot.armor.add(NbtCodec.encode(stack.writeNbt(new net.minecraft.nbt.NbtCompound())));
//        if (offhandEnabled) for (ItemStack stack : inventory.offHand) snapshot.offhand.add(NbtCodec.encode(stack.writeNbt(new net.minecraft.nbt.NbtCompound())));
//        if (selectedEnabled) snapshot.selectedSlot = inventory.selectedSlot;
//?} else if (>=1.20.5 && <=1.21.1) {
//        PlayerInventory inventory = player.getInventory();
//        if (mainEnabled) for (ItemStack stack : inventory.main) snapshot.main.add(NbtCodec.encode(stack.encodeAllowEmpty(player.getRegistryManager())));
//        if (armorEnabled) for (ItemStack stack : inventory.armor) snapshot.armor.add(NbtCodec.encode(stack.encodeAllowEmpty(player.getRegistryManager())));
//        if (offhandEnabled) for (ItemStack stack : inventory.offHand) snapshot.offhand.add(NbtCodec.encode(stack.encodeAllowEmpty(player.getRegistryManager())));
//        if (selectedEnabled) snapshot.selectedSlot = inventory.selectedSlot;
//?} else if (>=1.21.2 && <=1.21.4) {
//        PlayerInventory inventory = player.getInventory();
//        if (mainEnabled) for (ItemStack stack : inventory.main) snapshot.main.add(NbtCodec.encode(stack.toNbtAllowEmpty(player.getRegistryManager())));
//        if (armorEnabled) for (ItemStack stack : inventory.armor) snapshot.armor.add(NbtCodec.encode(stack.toNbtAllowEmpty(player.getRegistryManager())));
//        if (offhandEnabled) for (ItemStack stack : inventory.offHand) snapshot.offhand.add(NbtCodec.encode(stack.toNbtAllowEmpty(player.getRegistryManager())));
//        if (selectedEnabled) snapshot.selectedSlot = inventory.selectedSlot;
//?} else if >=26.1 {
//        Inventory inventory = player.getInventory();
//        if (mainEnabled) for (ItemStack stack : inventory.getNonEquipmentItems()) snapshot.main.add(NbtCodec.encode(com.atsukimc.anticrash.runtime.VersionNbt.item(stack, player.registryAccess())));
//        if (armorEnabled) for (ItemStack stack : equipmentStacks(inventory, 36, 4)) snapshot.armor.add(NbtCodec.encode(com.atsukimc.anticrash.runtime.VersionNbt.item(stack, player.registryAccess())));
//        if (offhandEnabled) for (ItemStack stack : equipmentStacks(inventory, 40, 1)) snapshot.offhand.add(NbtCodec.encode(com.atsukimc.anticrash.runtime.VersionNbt.item(stack, player.registryAccess())));
//        if (selectedEnabled) snapshot.selectedSlot = inventory.getSelectedSlot();
//?} else if =1.16.1 {
//        PlayerInventory inventory = player.getInventory();
//        if (mainEnabled) for (ItemStack stack : inventory.main) snapshot.main.add(NbtCodec.encode(stack.toTag(new net.minecraft.nbt.CompoundTag())));
//        if (armorEnabled) for (ItemStack stack : inventory.armor) snapshot.armor.add(NbtCodec.encode(stack.toTag(new net.minecraft.nbt.CompoundTag())));
//        if (offhandEnabled) for (ItemStack stack : inventory.offHand) snapshot.offhand.add(NbtCodec.encode(stack.toTag(new net.minecraft.nbt.CompoundTag())));
//        if (selectedEnabled) snapshot.selectedSlot = inventory.selectedSlot;
//?}
        return snapshot;
    }

//? if (>=1.21.5 && <=1.21.11) {
    public boolean restore(PlayerEntity player, boolean mainEnabled, boolean armorEnabled, boolean offhandEnabled, boolean selectedEnabled) throws IOException {
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> decodedMain = mainEnabled ? decodeList(main, inventory.getMainStacks().size(), "main", player.getRegistryManager()) : null;
        List<ItemStack> decodedArmor = armorEnabled ? decodeList(armor, 4, "armor", player.getRegistryManager()) : null;
        List<ItemStack> decodedOffhand = offhandEnabled ? decodeList(offhand, 1, "offhand", player.getRegistryManager()) : null;
//?} else if <=1.20.4 {
//    public boolean restore(PlayerEntity player, boolean mainEnabled, boolean armorEnabled, boolean offhandEnabled, boolean selectedEnabled) throws IOException {
//        PlayerInventory inventory = player.getInventory();
//        List<ItemStack> decodedMain = mainEnabled ? decodeList(main, inventory.main.size(), "main") : null;
//        List<ItemStack> decodedArmor = armorEnabled ? decodeList(armor, inventory.armor.size(), "armor") : null;
//        List<ItemStack> decodedOffhand = offhandEnabled ? decodeList(offhand, inventory.offHand.size(), "offhand") : null;
//?} else if (>=1.20.5 && <=1.21.4) {
//    public boolean restore(PlayerEntity player, boolean mainEnabled, boolean armorEnabled, boolean offhandEnabled, boolean selectedEnabled) throws IOException {
//        PlayerInventory inventory = player.getInventory();
//        List<ItemStack> decodedMain = mainEnabled ? decodeList(main, inventory.main.size(), "main", player.getRegistryManager()) : null;
//        List<ItemStack> decodedArmor = armorEnabled ? decodeList(armor, inventory.armor.size(), "armor", player.getRegistryManager()) : null;
//        List<ItemStack> decodedOffhand = offhandEnabled ? decodeList(offhand, inventory.offHand.size(), "offhand", player.getRegistryManager()) : null;
//?} else if >=26.1 {
//    public boolean restore(Player player, boolean mainEnabled, boolean armorEnabled, boolean offhandEnabled, boolean selectedEnabled) throws IOException {
//        Inventory inventory = player.getInventory();
//        List<ItemStack> decodedMain = mainEnabled ? decodeList(main, inventory.getNonEquipmentItems().size(), "main", player.registryAccess()) : null;
//        List<ItemStack> decodedArmor = armorEnabled ? decodeList(armor, 4, "armor", player.registryAccess()) : null;
//        List<ItemStack> decodedOffhand = offhandEnabled ? decodeList(offhand, 1, "offhand", player.registryAccess()) : null;
//?}
        if (selectedEnabled && !snapshotSlotValid(selectedSlot)) throw new IOException("Invalid selected hotbar slot: " + selectedSlot);

        // Decode and validate every enabled category before mutating a single slot.  A corrupt
        // optional item can therefore never leave a half-restored inventory.
        if (decodedMain != null) applyList(inventory, decodedMain, 0);
//? if (>=1.21.5 && <=1.21.11) {
        if (decodedArmor != null) applyList(inventory, decodedArmor, inventory.getMainStacks().size());
        if (decodedOffhand != null) applyList(inventory, decodedOffhand, inventory.getMainStacks().size() + 4);
        if (selectedEnabled) inventory.setSelectedSlot(selectedSlot);
//?} else if <=1.21.4 {
//        if (decodedArmor != null) applyList(inventory, decodedArmor, inventory.main.size());
//        if (decodedOffhand != null) applyList(inventory, decodedOffhand, inventory.main.size() + inventory.armor.size());
//        if (selectedEnabled) inventory.selectedSlot = selectedSlot;
//?} else if >=26.1 {
//        if (decodedArmor != null) applyList(inventory, decodedArmor, inventory.getNonEquipmentItems().size());
//        if (decodedOffhand != null) applyList(inventory, decodedOffhand, inventory.getNonEquipmentItems().size() + 4);
//        if (selectedEnabled) inventory.setSelectedSlot(selectedSlot);
//?}
        boolean changed = mainEnabled || armorEnabled || offhandEnabled || selectedEnabled;
//? if <=1.21.11 {
        if (changed) inventory.markDirty();
//?} else if >=26.1 {
//        if (changed) inventory.setChanged();
//?}
        return changed;
    }

    /** Decodes every enabled slot without mutating the live inventory. */
//? if <=1.21.11 {
    public void validate(PlayerEntity player, boolean mainEnabled, boolean armorEnabled,
//?} else if >=26.1 {
//    public void validate(Player player, boolean mainEnabled, boolean armorEnabled,
//?}
                         boolean offhandEnabled, boolean selectedEnabled) throws IOException {
//? if (>=1.21.5 && <=1.21.11) {
        PlayerInventory inventory = player.getInventory();
        if (mainEnabled) decodeList(main, inventory.getMainStacks().size(), "main", player.getRegistryManager());
        if (armorEnabled) decodeList(armor, 4, "armor", player.getRegistryManager());
        if (offhandEnabled) decodeList(offhand, 1, "offhand", player.getRegistryManager());
//?} else if <=1.20.4 {
//        PlayerInventory inventory = player.getInventory();
//        if (mainEnabled) decodeList(main, inventory.main.size(), "main");
//        if (armorEnabled) decodeList(armor, inventory.armor.size(), "armor");
//        if (offhandEnabled) decodeList(offhand, inventory.offHand.size(), "offhand");
//?} else if (>=1.20.5 && <=1.21.4) {
//        PlayerInventory inventory = player.getInventory();
//        if (mainEnabled) decodeList(main, inventory.main.size(), "main", player.getRegistryManager());
//        if (armorEnabled) decodeList(armor, inventory.armor.size(), "armor", player.getRegistryManager());
//        if (offhandEnabled) decodeList(offhand, inventory.offHand.size(), "offhand", player.getRegistryManager());
//?} else if >=26.1 {
//        Inventory inventory = player.getInventory();
//        if (mainEnabled) decodeList(main, inventory.getNonEquipmentItems().size(), "main", player.registryAccess());
//        if (armorEnabled) decodeList(armor, 4, "armor", player.registryAccess());
//        if (offhandEnabled) decodeList(offhand, 1, "offhand", player.registryAccess());
//?}
        if (selectedEnabled && !snapshotSlotValid(selectedSlot)) {
            throw new IOException("Invalid selected hotbar slot: " + selectedSlot);
        }
    }

    /** Validates the fixed Vanilla 1.18.2 PlayerInventory shape without a live player. */
    public void validatePortable(boolean mainEnabled, boolean armorEnabled,
                                 boolean offhandEnabled, boolean selectedEnabled) throws IOException {
//? if >=1.20.5 {
        if (mainEnabled) validateEncodedList(main, 36, "main");
        if (armorEnabled) validateEncodedList(armor, 4, "armor");
        if (offhandEnabled) validateEncodedList(offhand, 1, "offhand");
//?} else if <=1.20.4 {
//        if (mainEnabled) decodeList(main, 36, "main");
//        if (armorEnabled) decodeList(armor, 4, "armor");
//        if (offhandEnabled) decodeList(offhand, 1, "offhand");
//?}
        if (selectedEnabled && !snapshotSlotValid(selectedSlot)) {
            throw new IOException("Invalid selected hotbar slot: " + selectedSlot);
        }
    }

//? if (>=1.20.5 && <=1.21.11) {
    private static List<net.minecraft.nbt.NbtCompound> validateEncodedList(List<String> encoded, int expectedSize, String category) throws IOException {
//?} else if <=1.20.4 {
//    private static List<ItemStack> decodeList(List<String> encoded, int expectedSize, String category) throws IOException {
//?} else if >=26.1 {
//    private static List<net.minecraft.nbt.CompoundTag> validateEncodedList(List<String> encoded, int expectedSize, String category) throws IOException {
//?}
        if (encoded == null || encoded.size() != expectedSize) {
            throw new IOException("Inventory " + category + " entry count is "
                    + (encoded == null ? "null" : encoded.size()) + ", expected " + expectedSize);
        }
//? if (>=1.20.5 && <=1.21.11) {
        List<net.minecraft.nbt.NbtCompound> decoded = new ArrayList<>(expectedSize);
//?} else if <=1.20.4 {
//        List<ItemStack> decoded = new ArrayList<>(expectedSize);
//?} else if >=26.1 {
//        List<net.minecraft.nbt.CompoundTag> decoded = new ArrayList<>(expectedSize);
//?}
        for (String item : encoded) {
            try {
//? if (>=1.18 && <=1.21.11) {
                net.minecraft.nbt.NbtElement element = NbtCodec.decode(item);
                if (!(element instanceof net.minecraft.nbt.NbtCompound compound)) {
//?} else if =1.16.1 || >=26.1 {
//                net.minecraft.nbt.Tag element = NbtCodec.decode(item);
//                if (!(element instanceof net.minecraft.nbt.CompoundTag compound)) {
//?}
                    throw new IOException("Inventory " + category + " contains a non-compound item");
                }
//? if (>=1.21.5 && <=1.21.11) {
                // Pre-world inspection cannot resolve dynamic item components. Validate the bounded
                // envelope now; registry-aware decoding must finish before changing any player slot.
                if (!compound.isEmpty() && (!com.atsukimc.anticrash.runtime.VersionNbt.contains(compound, "id", 8)
                        || net.minecraft.util.Identifier.tryParse(compound.getString("id").orElseThrow()) == null
                        || !com.atsukimc.anticrash.runtime.VersionNbt.contains(compound, "count", 99) || compound.getInt("count").orElseThrow() < 1)) {
                    throw new IOException("Invalid component item envelope in " + category);
                }
                decoded.add(compound);
//?} else if (>=1.18 && <=1.20.4) {
//                decoded.add(ItemStack.fromNbt(compound));
//?} else if (>=1.20.5 && <=1.21.4) {
//                // Pre-world inspection cannot resolve dynamic item components. Validate the bounded
//                // envelope now; registry-aware decoding must finish before changing any player slot.
//                if (!compound.isEmpty() && (!compound.contains("id", 8)
//                        || net.minecraft.util.Identifier.tryParse(compound.getString("id")) == null
//                        || !compound.contains("count", 99) || compound.getInt("count") < 1)) {
//                    throw new IOException("Invalid component item envelope in " + category);
//                }
//                decoded.add(compound);
//?} else if >=26.1 {
//                // Pre-world inspection cannot resolve dynamic item components. Validate the bounded
//                // envelope now; registry-aware decoding must finish before changing any player slot.
//                if (!compound.isEmpty() && (!com.atsukimc.anticrash.runtime.VersionNbt.contains(compound, "id", 8)
//                        || net.minecraft.resources.Identifier.tryParse(compound.getString("id").orElseThrow()) == null
//                        || !com.atsukimc.anticrash.runtime.VersionNbt.contains(compound, "count", 99) || compound.getInt("count").orElseThrow() < 1)) {
//                    throw new IOException("Invalid component item envelope in " + category);
//                }
//                decoded.add(compound);
//?} else if =1.16.1 {
//                decoded.add(ItemStack.fromTag(compound));
//?}
            } catch (IOException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new IOException("Inventory " + category + " contains malformed item NBT", exception);
            }
        }
        return decoded;
    }

//? if (>=1.21.6 && <=1.21.11) {
    private static List<ItemStack> decodeList(List<String> encoded, int expectedSize, String category,
                                              net.minecraft.registry.RegistryWrapper.WrapperLookup registries) throws IOException {
        List<ItemStack> result = new ArrayList<>(expectedSize);
        for (var compound : validateEncodedList(encoded, expectedSize, category)) {
            if (compound.isEmpty()) result.add(ItemStack.EMPTY);
            else result.add(ItemStack.CODEC.parse(registries.getOps(net.minecraft.nbt.NbtOps.INSTANCE), compound).result().orElseThrow(
                    () -> new IOException("Unresolvable item/components in " + category)));
        }
        return result;
    }

    private static void applyList(PlayerInventory inventory, List<ItemStack> decoded, int offset) {
        for (int index = 0; index < decoded.size(); index++) inventory.setStack(offset + index, decoded.get(index));
//?} else if <=1.20.4 {
//    private static void applyList(PlayerInventory inventory, List<ItemStack> decoded, int offset) {
//        for (int index = 0; index < decoded.size(); index++) inventory.setStack(offset + index, decoded.get(index));
//?} else if (>=1.20.5 && <=1.21.5) {
//    private static List<ItemStack> decodeList(List<String> encoded, int expectedSize, String category,
//                                              net.minecraft.registry.RegistryWrapper.WrapperLookup registries) throws IOException {
//        List<ItemStack> result = new ArrayList<>(expectedSize);
//        for (var compound : validateEncodedList(encoded, expectedSize, category)) {
//            if (compound.isEmpty()) result.add(ItemStack.EMPTY);
//            else result.add(ItemStack.fromNbt(registries, compound).orElseThrow(
//                    () -> new IOException("Unresolvable item/components in " + category)));
//        }
//        return result;
//    }
//
//    private static void applyList(PlayerInventory inventory, List<ItemStack> decoded, int offset) {
//        for (int index = 0; index < decoded.size(); index++) inventory.setStack(offset + index, decoded.get(index));
//?} else if >=26.1 {
//    private static List<ItemStack> decodeList(List<String> encoded, int expectedSize, String category,
//                                              net.minecraft.core.HolderLookup.Provider registries) throws IOException {
//        List<ItemStack> result = new ArrayList<>(expectedSize);
//        for (var compound : validateEncodedList(encoded, expectedSize, category)) {
//            if (compound.isEmpty()) result.add(ItemStack.EMPTY);
//            else result.add(ItemStack.CODEC.parse(registries.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), compound).result().orElseThrow(
//                    () -> new IOException("Unresolvable item/components in " + category)));
//        }
//        return result;
//    }
//
//    private static void applyList(Inventory inventory, List<ItemStack> decoded, int offset) {
//        for (int index = 0; index < decoded.size(); index++) inventory.setItem(offset + index, decoded.get(index));
//?}
    }

//? if (>=1.21.5 && <=1.21.11) {
    private static List<ItemStack> equipmentStacks(PlayerInventory inventory, int offset, int count) {
        List<ItemStack> stacks = new ArrayList<>(count);
        for (int index = 0; index < count; index++) stacks.add(inventory.getStack(offset + index));
        return stacks;
    }

//?} else if <=1.21.4 {
//?} else if >=26.1 {
//    private static List<ItemStack> equipmentStacks(Inventory inventory, int offset, int count) {
//        List<ItemStack> stacks = new ArrayList<>(count);
//        for (int index = 0; index < count; index++) stacks.add(inventory.getItem(offset + index));
//        return stacks;
//    }
//
//?}
    private static boolean snapshotSlotValid(int slot) { return slot >= 0 && slot < 9; }
}
