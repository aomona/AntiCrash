package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.TickingBlockEntity;
//?}

//? if <=1.21.11 {
@Mixin(World.class)
//?} else if >=26.1 {
//@Mixin(Level.class)
//?}
public interface WorldAccessorMixin {
    @Accessor("blockEntityTickers")
//? if <=1.21.11 {
    List<BlockEntityTickInvoker> anticrash$getBlockEntityTickers();
//?} else if >=26.1 {
//    List<TickingBlockEntity> anticrash$getBlockEntityTickers();
//?}

    @Accessor("pendingBlockEntityTickers")
//? if <=1.21.11 {
    List<BlockEntityTickInvoker> anticrash$getPendingBlockEntityTickers();
//?} else if >=26.1 {
//    List<TickingBlockEntity> anticrash$getPendingBlockEntityTickers();
//?}

    /** Next global sub-tick sequence value. Calling World#getTickOrder would increment it. */
//? if <=1.21.11 {
    @Accessor("tickOrder")
//?} else if >=26.1 {
//    @Accessor("subTickCount")
//?}
    long anticrash$getNextTickOrder();

//? if <=1.21.11 {
    @Accessor("tickOrder")
//?} else if >=26.1 {
//    @Accessor("subTickCount")
//?}
    void anticrash$setNextTickOrder(long nextTickOrder);

    /** Mapped Minecraft 1.18.2 field; unlike java.util.Random internals this is safe to expose. */
//? if <=1.21.11 {
    @Accessor("lcgBlockSeed")
//?} else if >=26.1 {
//    @Accessor("randValue")
//?}
    int anticrash$getLcgBlockSeed();

//? if <=1.21.11 {
    @Accessor("lcgBlockSeed")
//?} else if >=26.1 {
//    @Accessor("randValue")
//?}
    void anticrash$setLcgBlockSeed(int seed);

//? if <=1.21.11 {
    @Accessor("rainGradient")
//?} else if >=26.1 {
//    @Accessor("rainLevel")
//?}
    float anticrash$getRainGradient();

//? if <=1.21.11 {
    @Accessor("rainGradient")
//?} else if >=26.1 {
//    @Accessor("rainLevel")
//?}
    void anticrash$setRainGradientValue(float value);

//? if (>=1.21.5 && <=1.21.11) {
    @Accessor("lastRainGradient")
//?} else if <=1.21.4 {
//    @Accessor("rainGradientPrev")
//?} else if >=26.1 {
//    @Accessor("oRainLevel")
//?}
    void anticrash$setRainGradientPrev(float value);

//? if <=1.21.11 {
    @Accessor("thunderGradient")
//?} else if >=26.1 {
//    @Accessor("thunderLevel")
//?}
    float anticrash$getThunderGradient();

//? if <=1.21.11 {
    @Accessor("thunderGradient")
//?} else if >=26.1 {
//    @Accessor("thunderLevel")
//?}
    void anticrash$setThunderGradientValue(float value);

//? if (>=1.21.5 && <=1.21.11) {
    @Accessor("lastThunderGradient")
//?} else if <=1.21.4 {
//    @Accessor("thunderGradientPrev")
//?} else if >=26.1 {
//    @Accessor("oThunderLevel")
//?}
    void anticrash$setThunderGradientPrev(float value);
}
