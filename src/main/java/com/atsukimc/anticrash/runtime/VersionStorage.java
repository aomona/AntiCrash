//? if >=1.21.6 {
package com.atsukimc.anticrash.runtime;

//? if (>=1.21.6 && <=1.21.11) {
//?} else if >=26.1 {
//import net.minecraft.core.HolderLookup;
//?}
import net.minecraft.nbt.*;
//? if (>=1.21.6 && <=1.21.11) {
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.ItemStack;
//?} else if >=26.1 {
//import net.minecraft.util.ProblemReporter;
//import net.minecraft.world.Container;
//import net.minecraft.world.ItemStackWithSlot;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Abilities;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.storage.TagValueOutput;
//?}
import java.util.*;

/** 1.21.6 view adapters. Codec failures must abort capture/validation, never silently drop data. */
public final class VersionStorage {
    private VersionStorage() { }
//? if (>=1.21.6 && <=1.21.11) {
    public record ThrowingReporter(String path) implements ErrorReporter {
        public ErrorReporter makeChild(ErrorReporter.Context context) {
            return new ThrowingReporter(path + "/" + context.getName());
//?} else if >=26.1 {
//    public record ThrowingReporter(String path) implements ProblemReporter {
//        public ProblemReporter forChild(ProblemReporter.PathElement context) {
//            return new ThrowingReporter(path + "/" + context.get());
//?}
        }
//? if (>=1.21.6 && <=1.21.11) {
        public void report(ErrorReporter.Error error) {
            throw new IllegalStateException("AntiCrash serialization failed at " + path + ": " + error.getMessage());
//?} else if >=26.1 {
//        public void report(ProblemReporter.Problem error) {
//            throw new IllegalStateException("AntiCrash serialization failed at " + path + ": " + error.description());
//?}
        }
    }
//? if (>=1.21.6 && <=1.21.11) {
    public static NbtWriteView writer(RegistryWrapper.WrapperLookup registries) {
        return NbtWriteView.create(new ThrowingReporter("snapshot"), registries);
//?} else if >=26.1 {
//    public static TagValueOutput writer(HolderLookup.Provider registries) {
//        return TagValueOutput.createWithContext(new ThrowingReporter("snapshot"), registries);
//?}
    }
//? if (>=1.21.6 && <=1.21.11) {
    public static NbtCompound entity(Entity entity) {
        var view = writer(entity.getRegistryManager());
        return entity.saveData(view) ? view.getNbt() : null;
//?} else if >=26.1 {
//    public static CompoundTag entity(Entity entity) {
//        var view = writer(entity.registryAccess());
//        return entity.save(view) ? view.buildResult() : null;
//?}
    }
//? if (>=1.21.6 && <=1.21.11) {
    public static NbtCompound abilities(PlayerAbilities abilities) {
        var nbt = new NbtCompound(); nbt.put("abilities", PlayerAbilities.Packed.CODEC, abilities.pack()); return nbt;
//?} else if >=26.1 {
//    public static CompoundTag abilities(Abilities abilities) {
//        var nbt = new CompoundTag(); nbt.store("abilities", Abilities.Packed.CODEC, abilities.pack()); return nbt;
//?}
    }
//? if (>=1.21.6 && <=1.21.11) {
    public static PlayerAbilities.Packed abilities(NbtCompound nbt) {
        return nbt.get("abilities", PlayerAbilities.Packed.CODEC).orElseThrow(
//?} else if >=26.1 {
//    public static Abilities.Packed abilities(CompoundTag nbt) {
//        return nbt.read("abilities", Abilities.Packed.CODEC).orElseThrow(
//?}
                () -> new IllegalArgumentException("Invalid player abilities"));
    }
//? if (>=1.21.6 && <=1.21.11) {
    public static NbtList inventory(Inventory inventory, RegistryWrapper.WrapperLookup registries) {
        var list = new NbtList(); var ops = registries.getOps(NbtOps.INSTANCE);
        for (int slot = 0; slot < inventory.size(); slot++) {
            var stack = inventory.getStack(slot);
            if (!stack.isEmpty()) list.add(StackWithSlot.CODEC.encodeStart(ops, new StackWithSlot(slot, stack)).getOrThrow());
//?} else if >=26.1 {
//    public static ListTag inventory(Container inventory, HolderLookup.Provider registries) {
//        var list = new ListTag(); var ops = registries.createSerializationContext(NbtOps.INSTANCE);
//        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
//            var stack = inventory.getItem(slot);
//            if (!stack.isEmpty()) list.add(ItemStackWithSlot.CODEC.encodeStart(ops, new ItemStackWithSlot(slot, stack)).getOrThrow());
//?}
        }
        return list;
    }
//? if (>=1.21.6 && <=1.21.11) {
    public static List<ItemStack> inventory(NbtList encoded, int size, RegistryWrapper.WrapperLookup registries) {
//?} else if >=26.1 {
//    public static List<ItemStack> inventory(ListTag encoded, int size, HolderLookup.Provider registries) {
//?}
        if (encoded.size() > size) throw new IllegalArgumentException("Too many inventory entries");
        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(size, ItemStack.EMPTY));
//? if (>=1.21.6 && <=1.21.11) {
        Set<Integer> slots = new HashSet<>(); var ops = registries.getOps(NbtOps.INSTANCE);
//?} else if >=26.1 {
//        Set<Integer> slots = new HashSet<>(); var ops = registries.createSerializationContext(NbtOps.INSTANCE);
//?}
        for (var element : encoded) {
//? if (>=1.21.6 && <=1.21.11) {
            var entry = StackWithSlot.CODEC.parse(ops, element).getOrThrow();
            if (!entry.isValidSlot(size) || !slots.add(entry.slot())) throw new IllegalArgumentException("Invalid/duplicate inventory slot");
//?} else if >=26.1 {
//            var entry = ItemStackWithSlot.CODEC.parse(ops, element).getOrThrow();
//            if (!entry.isValidInContainer(size) || !slots.add(entry.slot())) throw new IllegalArgumentException("Invalid/duplicate inventory slot");
//?}
            stacks.set(entry.slot(), entry.stack());
        }
        return stacks;
    }
//? if (>=1.21.6 && <=1.21.11) {
    public static void applyInventory(Inventory inventory, List<ItemStack> stacks) {
        if (inventory.size() != stacks.size()) throw new IllegalArgumentException("Inventory shape changed");
        for (int i = 0; i < stacks.size(); i++) inventory.setStack(i, stacks.get(i));
        inventory.markDirty();
//?} else if >=26.1 {
//    public static void applyInventory(Container inventory, List<ItemStack> stacks) {
//        if (inventory.getContainerSize() != stacks.size()) throw new IllegalArgumentException("Inventory shape changed");
//        for (int i = 0; i < stacks.size(); i++) inventory.setItem(i, stacks.get(i));
//        inventory.setChanged();
//?}
    }
}
//?}
