//? if >=1.19 {
package com.atsukimc.anticrash.runtime;

import com.mojang.serialization.JsonOps;
//? if (>=1.20 && <=1.21.11) {
import net.minecraft.block.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.event.Vibrations;
//?} else if (>=1.19 && <=1.19.4) {
//import net.minecraft.block.entity.*;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.world.event.listener.VibrationListener;
//?} else if >=26.1 {
//import net.minecraft.world.level.block.entity.*;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
//import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
//?}

/** Version-native codecs include every persisted candidate, source UUID and spread cursor. */
public final class SculkRuntimeFingerprint {
    private SculkRuntimeFingerprint() {}
    public static String capture(BlockEntity entity) {
//? if (>=1.21.6 && <=1.21.11) {
        if (entity instanceof SculkCatalystBlockEntity catalyst) {
            var view = VersionStorage.writer(entity.getWorld().getRegistryManager());
            catalyst.getEventListener().getSpreadManager().writeData(view);
            return "catalyst|" + view.getNbt();
        } else if (!(entity instanceof Vibrations)) return null;
        return "listener|" + Vibrations.ListenerData.CODEC
                .encodeStart(JsonOps.INSTANCE, ((Vibrations) entity).getVibrationListenerData()).result()
//?} else if (>=1.20 && <=1.21.5) {
//        if (entity instanceof SculkCatalystBlockEntity catalyst) {
//            NbtCompound cursors = new NbtCompound();
//            catalyst.getEventListener().getSpreadManager().writeNbt(cursors);
//            return "catalyst|" + cursors;
//        } else if (!(entity instanceof Vibrations)) return null;
//        return "listener|" + Vibrations.ListenerData.CODEC
//                .encodeStart(JsonOps.INSTANCE, ((Vibrations) entity).getVibrationListenerData()).result()
//?} else if (>=1.19 && <=1.19.4) {
//        VibrationListener listener;
//        if (entity instanceof SculkSensorBlockEntity sensor) listener = sensor.getEventListener();
//        else if (entity instanceof SculkShriekerBlockEntity shrieker) listener = shrieker.getVibrationListener();
//        else if (entity instanceof SculkCatalystBlockEntity catalyst) {
//            NbtCompound cursors = new NbtCompound();
//            catalyst.getSpreadManager().writeNbt(cursors);
//            return "catalyst|" + cursors;
//        } else return null;
//        return "listener|" + VibrationListener.createCodec((VibrationListener.Callback) entity)
//                .encodeStart(JsonOps.INSTANCE, listener).result()
//?} else if >=26.1 {
//        if (entity instanceof SculkCatalystBlockEntity catalyst) {
//            var view = VersionStorage.writer(entity.getLevel().registryAccess());
//            catalyst.getListener().getSculkSpreader().save(view);
//            return "catalyst|" + view.buildResult();
//        } else if (!(entity instanceof VibrationSystem)) return null;
//        return "listener|" + VibrationSystem.Data.CODEC
//                .encodeStart(JsonOps.INSTANCE, ((VibrationSystem) entity).getVibrationData()).result()
//?}
                .orElseThrow(() -> new IllegalStateException("Sculk listener capture failed"));
    }
}
//?}
