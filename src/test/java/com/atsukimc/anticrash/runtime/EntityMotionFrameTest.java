//? if >=1.21.11 {
package com.atsukimc.anticrash.runtime;
//? if >=26.1 {
//?} else if =1.21.11 {
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.util.math.Vec3d;
//?}
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//? if >=26.1 {

import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
//?} else if =1.21.11 {
//?}
class EntityMotionFrameTest {
    @Test void fullAndNullableHistoryRoundTrip() {
//? if >=26.1 {
        for(var frame:new EntityMotionFrame[]{new EntityMotionFrame(new Vec3(0.25,-0.5,2),new Vec3(-330,120,122),true),
                new EntityMotionFrame(Vec3.ZERO,null,false)})
//?} else if =1.21.11 {
//        for(var frame:new EntityMotionFrame[]{new EntityMotionFrame(new Vec3d(0.25,-0.5,2),new Vec3d(-330,120,122),true),
//                new EntityMotionFrame(Vec3d.ZERO,null,false)})
//?}
            assertEquals(frame,EntityMotionFrame.read(frame.write()));
    }
    @Test void invalidHistoryCannotBeRestored() {
//? if >=26.1 {
        assertThrows(IllegalArgumentException.class,()->new EntityMotionFrame(new Vec3(Double.NaN,0,0),null,false));
        assertThrows(IllegalArgumentException.class,()->new EntityMotionFrame(Vec3.ZERO,new Vec3(0,Double.POSITIVE_INFINITY,0),true));
        assertThrows(RuntimeException.class,()->EntityMotionFrame.read(new CompoundTag()));
//?} else if =1.21.11 {
//        assertThrows(IllegalArgumentException.class,()->new EntityMotionFrame(new Vec3d(Double.NaN,0,0),null,false));
//        assertThrows(IllegalArgumentException.class,()->new EntityMotionFrame(Vec3d.ZERO,new Vec3d(0,Double.POSITIVE_INFINITY,0),true));
//        assertThrows(RuntimeException.class,()->EntityMotionFrame.read(new NbtCompound()));
//?}
    }
}
//?}
