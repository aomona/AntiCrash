package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.entity.Entity;
//?} else if >=26.1 {
//import net.minecraft.world.entity.Entity;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Read-only view of the entity state AntiCrash keeps in its NBT extension, for verification. */
@Mixin(Entity.class)
public interface EntityRuntimeAccessorMixin {
//? if (>=1.21 && <=1.21.10) {
    @Accessor("forceUpdateSupportingBlockPos")
    boolean anticrash$forceSupportUpdate();
    @Accessor("ridingCooldown")
//?} else if <=1.19.4 {
//    @Accessor("inNetherPortal")
//    boolean anticrash$isInNetherPortal();
//
//    @Accessor("netherPortalTime")
//    int anticrash$getNetherPortalTime();
//
//    @Accessor("ridingCooldown")
//?} else if (>=1.20 && <=1.20.6) {
//    @Accessor("forceUpdateSupportingBlockPos")
//    boolean anticrash$forceSupportUpdate();
//    @Accessor("inNetherPortal")
//    boolean anticrash$isInNetherPortal();
//
//    @Accessor("netherPortalTime")
//    int anticrash$getNetherPortalTime();
//
//    @Accessor("ridingCooldown")
//?} else if >=26.1 {
//    @Accessor("lastKnownSpeed") net.minecraft.world.phys.Vec3 anticrash$movement();
//    @Accessor("lastKnownSpeed") void anticrash$setMovement(net.minecraft.world.phys.Vec3 value);
//    @Accessor("lastKnownPosition") net.minecraft.world.phys.Vec3 anticrash$lastPos();
//    @Accessor("lastKnownPosition") void anticrash$setLastPos(net.minecraft.world.phys.Vec3 value);
//    @Accessor("onGroundNoBlocks")
//    boolean anticrash$forceSupportUpdate();
//    @Accessor("boardingCooldown")
//?} else if =1.21.11 {
//    @Accessor("movement") net.minecraft.util.math.Vec3d anticrash$movement();
//    @Accessor("movement") void anticrash$setMovement(net.minecraft.util.math.Vec3d value);
//    @Accessor("lastPos") net.minecraft.util.math.Vec3d anticrash$lastPos();
//    @Accessor("lastPos") void anticrash$setLastPos(net.minecraft.util.math.Vec3d value);
//    @Accessor("forceUpdateSupportingBlockPos")
//    boolean anticrash$forceSupportUpdate();
//    @Accessor("ridingCooldown")
//?}
    int anticrash$getRidingCooldown();

//? if <=1.21.11 {
    @Accessor("firstUpdate")
//?} else if >=26.1 {
//    @Accessor("firstTick")
//?}
    boolean anticrash$isFirstUpdate();

//? if <=1.21.11 {
    @Accessor("touchingWater")
//?} else if >=26.1 {
//    @Accessor("wasTouchingWater")
//?}
    boolean anticrash$isTouchingWater();

//? if <=1.21.11 {
    /** The counter new entities take their network id from. */
    @Accessor("CURRENT_ID")
    static java.util.concurrent.atomic.AtomicInteger anticrash$currentId() {
        throw new AssertionError();
    }
//?} else if >=26.2 {
//?} else if =26.1 {
//    /** The counter new entities take their network id from. */
//    @Accessor("ENTITY_COUNTER")
//    static java.util.concurrent.atomic.AtomicInteger anticrash$currentId() {
//        throw new AssertionError();
//    }
//?}
}
