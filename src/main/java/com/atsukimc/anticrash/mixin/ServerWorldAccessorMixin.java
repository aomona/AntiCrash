package com.atsukimc.anticrash.mixin;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
//? if <=1.21.11 {
import net.minecraft.entity.Entity;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.EntityList;
//?} else if >=26.1 {
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.BlockEventData;
//import net.minecraft.world.level.entity.EntityTickList;
//import net.minecraft.world.level.entity.PersistentEntitySectionManager;
//?}

//? if <=1.21.11 {
@Mixin(ServerWorld.class)
//?} else if >=26.1 {
//@Mixin(ServerLevel.class)
//?}
public interface ServerWorldAccessorMixin {
//? if <=1.21.11 {
    @Accessor("syncedBlockEventQueue")
    ObjectLinkedOpenHashSet<BlockEvent> anticrash$getSyncedBlockEventQueue();
//?} else if >=26.1 {
//    @Accessor("blockEvents")
//    ObjectLinkedOpenHashSet<BlockEventData> anticrash$getSyncedBlockEventQueue();
//?}

//? if <=1.21.11 {
    @Accessor("blockEventQueue")
    List<BlockEvent> anticrash$getBlockEventQueue();
//?} else if >=26.1 {
//    @Accessor("blockEventsToReschedule")
//    List<BlockEventData> anticrash$getBlockEventQueue();
//?}

    @Accessor("entityManager")
//? if <=1.21.11 {
    ServerEntityManager<Entity> anticrash$getEntityManager();
//?} else if >=26.1 {
//    PersistentEntitySectionManager<Entity> anticrash$getEntityManager();
//?}

//? if <=1.21.11 {
    @Accessor("entityList")
    EntityList anticrash$getEntityList();
//?} else if >=26.1 {
//    @Accessor("entityTickList")
//    EntityTickList anticrash$getEntityList();
//?}

    /** Ticks this dimension has had neither a player nor a forced chunk; entities stop at 300. */
//? if <=1.21.11 {
    @Accessor("idleTimeout")
//?} else if >=26.1 {
//    @Accessor("emptyTime")
//?}
    int anticrash$getIdleTimeout();

//? if <=1.21.11 {
    @Accessor("idleTimeout")
//?} else if >=26.1 {
//    @Accessor("emptyTime")
//?}
    void anticrash$setIdleTimeout(int ticks);
}
