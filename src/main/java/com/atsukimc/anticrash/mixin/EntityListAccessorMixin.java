package com.atsukimc.anticrash.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
//? if <=1.21.11 {
import net.minecraft.entity.Entity;
import net.minecraft.world.EntityList;
//?} else if >=26.1 {
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.entity.EntityTickList;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <=1.21.11 {
@Mixin(EntityList.class)
//?} else if >=26.1 {
//@Mixin(EntityTickList.class)
//?}
public interface EntityListAccessorMixin {
//? if <=1.21.11 {
    @Accessor("entities")
//?} else if >=26.1 {
//    @Accessor("active")
//?}
    Int2ObjectMap<Entity> anticrash$getEntities();

//? if <=1.21.11 {
    @Accessor("iterating")
//?} else if >=26.1 {
//    @Accessor("iterated")
//?}
    Int2ObjectMap<Entity> anticrash$getIterating();
}
