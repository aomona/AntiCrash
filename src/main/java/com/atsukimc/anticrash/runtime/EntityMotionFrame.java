//? if >=1.21.11 {
package com.atsukimc.anticrash.runtime;

import com.atsukimc.anticrash.mixin.EntityRuntimeAccessorMixin;
//? if >=26.1 {
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
//?} else if =1.21.11 {
//import net.minecraft.entity.Entity;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.util.math.Vec3d;
//?}

/** 1.21.11 kinetic movement history is not velocity and is not saved by Vanilla. */
//? if >=26.1 {
public record EntityMotionFrame(Vec3 movement,Vec3 lastPosition,boolean knockedBack) {
//?} else if =1.21.11 {
//public record EntityMotionFrame(Vec3d movement,Vec3d lastPosition,boolean knockedBack) {
//?}
    public EntityMotionFrame {
        if(!finite(movement) || (lastPosition!=null && !finite(lastPosition)))
            throw new IllegalArgumentException("Invalid entity motion history");
    }
//? if >=26.1 {
    private static boolean finite(Vec3 vector) {
//?} else if =1.21.11 {
//    private static boolean finite(Vec3d vector) {
//?}
        return vector!=null && Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z);
    }
    public static EntityMotionFrame capture(Entity entity) {
        var accessor=(EntityRuntimeAccessorMixin)entity;
//? if =26.3 {
        return new EntityMotionFrame(accessor.anticrash$movement(),accessor.anticrash$lastPos(),entity.syncVelocity);
//?} else if (>=26.1 && <=26.2) {
//        return new EntityMotionFrame(accessor.anticrash$movement(),accessor.anticrash$lastPos(),entity.hurtMarked);
//?} else if =1.21.11 {
//        return new EntityMotionFrame(accessor.anticrash$movement(),accessor.anticrash$lastPos(),entity.knockedBack);
//?}
    }
//? if >=26.1 {
    public CompoundTag write() {
        var nbt=new CompoundTag(); nbt.store("Movement",Vec3.CODEC,movement);
//?} else if =1.21.11 {
//    public NbtCompound write() {
//        var nbt=new NbtCompound(); nbt.put("Movement",Vec3d.CODEC,movement);
//?}
        nbt.putBoolean("HasLast",lastPosition!=null);
//? if >=26.1 {
        if(lastPosition!=null) nbt.store("Last",Vec3.CODEC,lastPosition);
//?} else if =1.21.11 {
//        if(lastPosition!=null) nbt.put("Last",Vec3d.CODEC,lastPosition);
//?}
        nbt.putBoolean("KnockedBack",knockedBack); return nbt;
    }
//? if >=26.1 {
    public static EntityMotionFrame read(CompoundTag nbt) {
        return new EntityMotionFrame(nbt.read("Movement",Vec3.CODEC).orElseThrow(),
                nbt.getBoolean("HasLast").orElseThrow()?nbt.read("Last",Vec3.CODEC).orElseThrow():null,
//?} else if =1.21.11 {
//    public static EntityMotionFrame read(NbtCompound nbt) {
//        return new EntityMotionFrame(nbt.get("Movement",Vec3d.CODEC).orElseThrow(),
//                nbt.getBoolean("HasLast").orElseThrow()?nbt.get("Last",Vec3d.CODEC).orElseThrow():null,
//?}
                nbt.getBoolean("KnockedBack").orElseThrow());
    }
    public void apply(Entity entity) {
        var accessor=(EntityRuntimeAccessorMixin)entity;
        accessor.anticrash$setMovement(movement); accessor.anticrash$setLastPos(lastPosition);
//? if =26.3 {
        entity.syncVelocity=knockedBack;
//?} else if (>=26.1 && <=26.2) {
//        entity.hurtMarked=knockedBack;
//?} else if =1.21.11 {
//        entity.knockedBack=knockedBack;
//?}
    }
}
//?}
