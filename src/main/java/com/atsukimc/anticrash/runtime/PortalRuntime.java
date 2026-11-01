//? if >=1.21 {
package com.atsukimc.anticrash.runtime;

import com.atsukimc.anticrash.mixin.PortalManagerAccessorMixin;
//? if (>=1.21 && <=1.21.11) {
import net.minecraft.block.Block;
import net.minecraft.block.Portal;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.PortalManager;
//?} else if >=26.1 {
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.entity.PortalProcessor;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Portal;
//?}

public final class PortalRuntime {
    private PortalRuntime() {}
//? if (>=1.21 && <=1.21.11) {
    public static String type(PortalManager manager) {
//?} else if >=26.1 {
//    public static String type(PortalProcessor manager) {
//?}
        Portal portal = ((PortalManagerAccessorMixin) manager).anticrash$getPortal();
//? if (>=1.21 && <=1.21.11) {
        return portal instanceof Block block ? Registries.BLOCK.getId(block).toString() : null;
//?} else if >=26.1 {
//        return portal instanceof Block block ? BuiltInRegistries.BLOCK.getKey(block).toString() : null;
//?}
    }
//? if (>=1.21 && <=1.21.11) {
    public static NbtCompound write(PortalManager manager) {
//?} else if >=26.1 {
//    public static CompoundTag write(PortalProcessor manager) {
//?}
        String type = type(manager);
        if (type == null) throw new IllegalStateException("UNSUPPORTED non-block Portal implementation");
//? if (>=1.21.5 && <=1.21.11) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("Type", type); nbt.put("Pos", net.minecraft.util.math.BlockPos.CODEC, manager.getPortalPos());
        nbt.putInt("Time", manager.getTicksInPortal()); nbt.putBoolean("In", manager.isInPortal());
//?} else if (>=1.21 && <=1.21.4) {
//        NbtCompound nbt = new NbtCompound();
//        nbt.putString("Type", type); nbt.put("Pos", NbtHelper.fromBlockPos(manager.getPortalPos()));
//        nbt.putInt("Time", manager.getTicksInPortal()); nbt.putBoolean("In", manager.isInPortal());
//?} else if >=26.1 {
//        CompoundTag nbt = new CompoundTag();
//        nbt.putString("Type", type); nbt.store("Pos", net.minecraft.core.BlockPos.CODEC, manager.getEntryPosition());
//        nbt.putInt("Time", manager.getPortalTime()); nbt.putBoolean("In", manager.isInsidePortalThisTick());
//?}
        return nbt;
    }
//? if (>=1.21.5 && <=1.21.11) {
    public static PortalManager read(NbtCompound nbt) {
        Identifier id = Identifier.tryParse(nbt.getString("Type").orElseThrow());
        if (id == null || !Registries.BLOCK.containsId(id) || !(Registries.BLOCK.get(id) instanceof Portal portal))
//?} else if (>=1.21 && <=1.21.4) {
//    public static PortalManager read(NbtCompound nbt) {
//        Identifier id = Identifier.tryParse(nbt.getString("Type"));
//        if (id == null || !Registries.BLOCK.containsId(id) || !(Registries.BLOCK.get(id) instanceof Portal portal))
//?} else if >=26.1 {
//    public static PortalProcessor read(CompoundTag nbt) {
//        Identifier id = Identifier.tryParse(nbt.getString("Type").orElseThrow());
//        if (id == null || !BuiltInRegistries.BLOCK.containsKey(id) || !(BuiltInRegistries.BLOCK.getValue(id) instanceof Portal portal))
//?}
            throw new IllegalArgumentException("Invalid saved Portal type");
//? if (>=1.21.5 && <=1.21.11) {
        var pos = nbt.get("Pos", net.minecraft.util.math.BlockPos.CODEC).orElseThrow(
//?} else if (>=1.21 && <=1.21.4) {
//        var pos = NbtHelper.toBlockPos(nbt, "Pos").orElseThrow(
//?} else if >=26.1 {
//        var pos = nbt.read("Pos", net.minecraft.core.BlockPos.CODEC).orElseThrow(
//?}
                () -> new IllegalArgumentException("Missing saved Portal position"));
//? if >=1.21.5 {
        int ticks = nbt.getInt("Time").orElseThrow();
//?} else if (>=1.21 && <=1.21.4) {
//        int ticks = nbt.getInt("Time");
//?}
        if (ticks < 0) throw new IllegalArgumentException("Negative saved Portal time");
//? if (>=1.21.5 && <=1.21.11) {
        PortalManager manager = new PortalManager(portal, pos);
        manager.setInPortal(nbt.getBoolean("In").orElseThrow());
//?} else if (>=1.21 && <=1.21.4) {
//        PortalManager manager = new PortalManager(portal, pos);
//        manager.setInPortal(nbt.getBoolean("In"));
//?} else if >=26.1 {
//        PortalProcessor manager = new PortalProcessor(portal, pos);
//        manager.setAsInsidePortalThisTick(nbt.getBoolean("In").orElseThrow());
//?}
        ((PortalManagerAccessorMixin) manager).anticrash$setTicksInPortal(ticks);
        return manager;
    }
//? if (>=1.21 && <=1.21.11) {
    public static String canonical(PortalManager manager) {
        return type(manager) + "|pos=" + manager.getPortalPos().asLong() + "|ticks="
                + manager.getTicksInPortal() + "|in=" + manager.isInPortal();
//?} else if >=26.1 {
//    public static String canonical(PortalProcessor manager) {
//        return type(manager) + "|pos=" + manager.getEntryPosition().asLong() + "|ticks="
//                + manager.getPortalTime() + "|in=" + manager.isInsidePortalThisTick();
//?}
    }
}
//?}
