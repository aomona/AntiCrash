package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.world.BlockView;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.block.RedstoneTorchBlock;
//?}

/** Read/write is restricted to replacing the new world's own burnout history during recovery. */
@Mixin(RedstoneTorchBlock.class)
public interface RedstoneTorchBlockAccessorMixin {
//? if <=1.21.11 {
    @Accessor("BURNOUT_MAP")
    static Map<BlockView, List<RedstoneTorchBlock.BurnoutEntry>> anticrash$getBurnoutMap() {
//?} else if >=26.1 {
//    @Accessor("RECENT_TOGGLES")
//    static Map<BlockGetter, List<RedstoneTorchBlock.Toggle>> anticrash$getBurnoutMap() {
//?}
        throw new AssertionError("Mixin accessor");
    }
}
