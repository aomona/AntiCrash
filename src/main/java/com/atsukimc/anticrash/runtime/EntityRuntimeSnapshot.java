package com.atsukimc.anticrash.runtime;
//? if (>=1.19.3 && <=1.21.11) {

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
//?} else if <=1.19.2 {
//
//import net.minecraft.entity.Entity;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.registry.Registry;
//?} else if >=26.1 {
//?}

import java.util.ArrayList;
import java.util.List;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.Identifier;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.ChunkPos;
//?}

/**
 * Safe, observational representation of the ServerWorld EntityList iteration order.
 *
 * <p>EntityList is actively used by ServerWorld#tick. Its internal map is not replaced during
 * recovery: Minecraft's own add/remove invariants remain authoritative. This snapshot is used to
 * detect an order change and stop STRICT recovery rather than silently reshuffling live entities.</p>
 */
public final class EntityRuntimeSnapshot {
    public int formatVersion = 1;
    public String dimension;
    public String uuid;
    public String type;
    public int networkEntityId;
    public int chunkX;
    public int chunkZ;
    public int order;
    /** EntityList can retain a removed entry until the owning tick removes it. Keep its count/order. */
    public boolean removed;

    public String canonical() {
        return canonicalAt(order);
    }

    /** Canonical form with a comparison-local order after optional entity filtering. */
    public String canonicalAt(int comparisonOrder) {
        return comparisonOrder + "|" + dimension + "|" + uuid + "|" + type + "|"
                + networkEntityId + "|" + chunkX + ',' + chunkZ + "|removed=" + removed;
    }

//? if <=1.21.11 {
    public static List<EntityRuntimeSnapshot> capture(ServerWorld world) {
//?} else if >=26.1 {
//    public static List<EntityRuntimeSnapshot> capture(ServerLevel world) {
//?}
        List<EntityRuntimeSnapshot> result = new ArrayList<>();
//? if <=1.21.11 {
        String dimension = world.getRegistryKey().getValue().toString();
//?} else if >=26.1 {
//        String dimension = world.dimension().identifier().toString();
//?}
        ((com.atsukimc.anticrash.mixin.ServerWorldAccessorMixin) world)
                .anticrash$getEntityList().forEach(entity -> {
                    if (entity == null) return;
                    EntityRuntimeSnapshot snapshot = new EntityRuntimeSnapshot();
                    snapshot.dimension = dimension;
//? if (>=1.19.3 && <=1.21.11) {
                    snapshot.uuid = entity.getUuidAsString();
                    Identifier id = Registries.ENTITY_TYPE.getId(entity.getType());
//?} else if <=1.19.2 {
//                    snapshot.uuid = entity.getUuidAsString();
//                    Identifier id = Registry.ENTITY_TYPE.getId(entity.getType());
//?} else if >=26.1 {
//                    snapshot.uuid = entity.getStringUUID();
//                    Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
//?}
                    snapshot.type = id == null ? entity.getType().toString() : id.toString();
//? if (>=1.18 && <=1.21.11) {
                    snapshot.networkEntityId = entity.getId();
                    ChunkPos chunk = entity.getChunkPos();
                    snapshot.chunkX = chunk.x;
                    snapshot.chunkZ = chunk.z;
//?} else if >=26.1 {
//                    snapshot.networkEntityId = entity.getId();
//                    ChunkPos chunk = entity.chunkPosition();
//                    snapshot.chunkX = chunk.x();
//                    snapshot.chunkZ = chunk.z();
//?} else if =1.16.1 {
//                    snapshot.networkEntityId = entity.getEntityId();
//                    ChunkPos chunk = entity.getChunkPos();
//                    snapshot.chunkX = chunk.x;
//                    snapshot.chunkZ = chunk.z;
//?}
                    snapshot.order = result.size();
                    snapshot.removed = entity.isRemoved();
                    result.add(snapshot);
                });
        return result;
    }
}
