//? if <=1.19.2 {
package com.atsukimc.anticrash.mixin;

//? if (>=1.19 && <=1.19.2) {
import net.minecraft.world.event.listener.VibrationListener;
//?} else if <=1.18.2 {
//import net.minecraft.world.event.GameEvent;
//import net.minecraft.world.event.listener.SculkSensorListener;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if (>=1.19 && <=1.19.2) {
/** 1.19 Vanilla persists vibration/source UUIDs/distance/delay via the listener codec. */
@Mixin(VibrationListener.class)
//?} else if <=1.18.2 {
//import java.util.Optional;
//
//@Mixin(SculkSensorListener.class)
//?}
public interface SculkSensorListenerAccessorMixin {
//? if (>=1.19 && <=1.19.2) {
    @Accessor("vibration")
    VibrationListener.Vibration anticrash$getVibration();
//?} else if <=1.18.2 {
//    @Accessor("event")
//    Optional<GameEvent> anticrash$getEvent();
//
//    @Accessor("event")
//    void anticrash$setEvent(Optional<GameEvent> event);
//?}

    @Accessor("distance")
//? if (>=1.19 && <=1.19.2) {
    float anticrash$getDistance();
//?} else if <=1.18.2 {
//    int anticrash$getDistance();
//
//    @Accessor("distance")
//    void anticrash$setDistance(int distance);
//?}

    @Accessor("delay")
    int anticrash$getDelay();

//? if (>=1.19 && <=1.19.2) {
//?} else if <=1.18.2 {
//    @Accessor("delay")
//    void anticrash$setDelay(int delay);
//?}
}
//?}
