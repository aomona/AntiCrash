//? if >=1.19 {
package com.atsukimc.anticrash.audit;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

/** Offline evidence for version-dependent semantic assumptions, without starting a server. */
class VersionAdapterBytecodeTest {
//? if (>=1.21.6 && <=1.21.8) {
    @Test void ticketsUseCountdownAndSeparateLoadingAndSimulation() throws Exception {
        var type = net.minecraft.server.world.ChunkTicketType.ENDER_PEARL;
        assertEquals(40, type.expiryTicks());
        assertTrue(type.isForLoading()); assertTrue(type.isForSimulation());
        assertTrue(net.minecraft.server.world.ChunkTicketType.PORTAL.persist());
        assertEquals("minecraft:ender_pearl", net.minecraft.registry.Registries.TICKET_TYPE.getId(type).toString());
        var ticket = read("net/minecraft/server/world/ChunkTicket");
        assertTrue(ticket.fields.stream().anyMatch(f -> f.name.equals("ticksLeft")));
        assertFalse(ticket.fields.stream().anyMatch(f -> f.name.equals("argument") || f.name.equals("tickCreated")));
        var recovery = com.atsukimc.anticrash.recovery.ChunkRecoveryManager.RECOVERY_TICKET;
        assertFalse(recovery.persist());
        assertNotEquals(net.minecraft.server.world.ChunkTicketType.START, recovery);
    }
    @Test void newPickPacketsAreRoutedThroughTheServerThread() throws Exception {
        var handler = read("net/minecraft/server/network/ServerPlayNetworkHandler");
        for (String name : new String[]{"onPickItemFromBlock", "onPickItemFromEntity"})
            assertTrue(invokes(handler, name, "net/minecraft/network/NetworkThreadUtils", "forceMainThread"));
        assertFalse(handler.methods.stream().anyMatch(m -> m.name.equals("onPickFromInventory")));
    }
    @Test void furnacePersistsAllFourRenamedTimers() throws Exception {
        var furnace = read("net/minecraft/block/entity/AbstractFurnaceBlockEntity");
        for (String field : new String[]{"litTimeRemaining", "litTotalTime", "cookingTimeSpent", "cookingTotalTime"}) {
            assertTrue(furnace.fields.stream().anyMatch(f -> f.name.equals(field)));
            for (String method : new String[]{"readData", "writeData"})
                assertTrue(furnace.methods.stream().filter(m -> m.name.equals(method)).anyMatch(m -> {
                    for (var insn : m.instructions)
                        if (insn instanceof FieldInsnNode f && f.owner.equals(furnace.name) && f.name.equals(field)) return true;
                    return false;
                }), field + ": " + method);
        }
    }
//?} else if (>=1.19 && <=1.21.3) {
//?} else if (>=1.21.9 && <=1.21.11) {
//    @Test void modernSpawnPointPreservesBothAngles() {
//        var spawn=net.minecraft.world.WorldProperties.SpawnPoint.create(net.minecraft.world.World.OVERWORLD,
//                new net.minecraft.util.math.BlockPos(-330,120,122),87.5f,-22.25f);
//        var respawn=new net.minecraft.server.network.ServerPlayerEntity.Respawn(spawn,true);
//        var encoded=net.minecraft.server.network.ServerPlayerEntity.Respawn.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,respawn).getOrThrow();
//        var decoded=net.minecraft.server.network.ServerPlayerEntity.Respawn.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,encoded).getOrThrow();
//        assertEquals(respawn,decoded);
//        assertEquals(-22.25f,decoded.respawnData().pitch());
//    }
//    @Test void borderIsPersistentStateAndRidingHasAnEventFreeRestorationPath() throws Exception {
//        assertTrue(net.minecraft.world.PersistentState.class.isAssignableFrom(net.minecraft.world.border.WorldBorder.class));
//        var entity=read("net/minecraft/entity/Entity");
//        assertTrue(entity.methods.stream().anyMatch(m->m.name.equals("startRiding") && m.desc.equals("(Lnet/minecraft/entity/Entity;ZZ)Z")));
//        var projectile=read("net/minecraft/entity/projectile/ProjectileEntity");
//        assertTrue(projectile.fields.stream().anyMatch(f->f.name.equals("checkedForLeftOwner") && f.desc.equals("Z")));
//    }
//    @Test void ticketsUseCountdownAndSeparateLoadingAndSimulation() throws Exception {
//        var type = net.minecraft.server.world.ChunkTicketType.ENDER_PEARL;
//        assertEquals(40, type.expiryTicks());
//        assertTrue(type.isForLoading()); assertTrue(type.isForSimulation());
//        assertTrue(net.minecraft.server.world.ChunkTicketType.PORTAL.shouldSerialize());
//        assertEquals("minecraft:ender_pearl", net.minecraft.registry.Registries.TICKET_TYPE.getId(type).toString());
//        var ticket = read("net/minecraft/server/world/ChunkTicket");
//        assertTrue(ticket.fields.stream().anyMatch(f -> f.name.equals("ticksLeft")));
//        assertFalse(ticket.fields.stream().anyMatch(f -> f.name.equals("argument") || f.name.equals("tickCreated")));
//        var recovery = com.atsukimc.anticrash.recovery.ChunkRecoveryManager.RECOVERY_TICKET;
//        assertFalse(recovery.shouldSerialize());
//        assertNotEquals(net.minecraft.server.world.ChunkTicketType.DRAGON, recovery);
//    }
//    @Test void newPickPacketsAreRoutedThroughTheServerThread() throws Exception {
//        var handler = read("net/minecraft/server/network/ServerPlayNetworkHandler");
//        for (String name : new String[]{"onPickItemFromBlock", "onPickItemFromEntity"})
//            assertTrue(invokes(handler, name, "net/minecraft/network/NetworkThreadUtils", "forceMainThread"));
//        assertFalse(handler.methods.stream().anyMatch(m -> m.name.equals("onPickFromInventory")));
//    }
//    @Test void furnacePersistsAllFourRenamedTimers() throws Exception {
//        var furnace = read("net/minecraft/block/entity/AbstractFurnaceBlockEntity");
//        for (String field : new String[]{"litTimeRemaining", "litTotalTime", "cookingTimeSpent", "cookingTotalTime"}) {
//            assertTrue(furnace.fields.stream().anyMatch(f -> f.name.equals(field)));
//            for (String method : new String[]{"readData", "writeData"})
//                assertTrue(furnace.methods.stream().filter(m -> m.name.equals(method)).anyMatch(m -> {
//                    for (var insn : m.instructions)
//                        if (insn instanceof FieldInsnNode f && f.owner.equals(furnace.name) && f.name.equals(field)) return true;
//                    return false;
//                }), field + ": " + method);
//        }
//    }
//?} else if >=26.1 {
//    @Test void modernSpawnPointPreservesBothAngles() {
//        var spawn=net.minecraft.world.level.storage.LevelData.RespawnData.of(net.minecraft.world.level.Level.OVERWORLD,
//                new net.minecraft.core.BlockPos(-330,120,122),87.5f,-22.25f);
//        var respawn=new net.minecraft.server.level.ServerPlayer.RespawnConfig(spawn,true);
//        var encoded=net.minecraft.server.level.ServerPlayer.RespawnConfig.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE,respawn).getOrThrow();
//        var decoded=net.minecraft.server.level.ServerPlayer.RespawnConfig.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,encoded).getOrThrow();
//        assertEquals(respawn,decoded);
//        assertEquals(-22.25f,decoded.respawnData().pitch());
//    }
//    @Test void borderIsPersistentStateAndRidingHasAnEventFreeRestorationPath() throws Exception {
//        assertTrue(net.minecraft.world.level.saveddata.SavedData.class.isAssignableFrom(net.minecraft.world.level.border.WorldBorder.class));
//        var entity=read("net/minecraft/world/entity/Entity");
//        assertTrue(entity.methods.stream().anyMatch(m->m.name.equals("startRiding") && m.desc.equals("(Lnet/minecraft/world/entity/Entity;ZZ)Z")));
//        var projectile=read("net/minecraft/world/entity/projectile/Projectile");
//        assertTrue(projectile.fields.stream().anyMatch(f->f.name.equals("leftOwnerChecked") && f.desc.equals("Z")));
//    }
//    @Test void ticketsUseCountdownAndSeparateLoadingAndSimulation() throws Exception {
//        var type = net.minecraft.server.level.TicketType.ENDER_PEARL;
//        assertEquals(40, type.timeout());
//        assertTrue(type.doesLoad()); assertTrue(type.doesSimulate());
//        assertTrue(net.minecraft.server.level.TicketType.PORTAL.persist());
//        assertEquals("minecraft:ender_pearl", net.minecraft.core.registries.BuiltInRegistries.TICKET_TYPE.getKey(type).toString());
//        var ticket = read("net/minecraft/server/level/Ticket");
//        assertTrue(ticket.fields.stream().anyMatch(f -> f.name.equals("ticksLeft")));
//        assertFalse(ticket.fields.stream().anyMatch(f -> f.name.equals("argument") || f.name.equals("tickCreated")));
//        var recovery = com.atsukimc.anticrash.recovery.ChunkRecoveryManager.RECOVERY_TICKET;
//        assertFalse(recovery.persist());
//        assertNotEquals(net.minecraft.server.level.TicketType.DRAGON, recovery);
//    }
//    @Test void newPickPacketsAreRoutedThroughTheServerThread() throws Exception {
//        var handler = read("net/minecraft/server/network/ServerGamePacketListenerImpl");
//        for (String name : new String[]{"handlePickItemFromBlock", "handlePickItemFromEntity"})
//            assertTrue(invokes(handler, name, "net/minecraft/network/protocol/PacketUtils", "ensureRunningOnSameThread"));
//        assertFalse(handler.methods.stream().anyMatch(m -> m.name.equals("onPickFromInventory")));
//    }
//    @Test void furnacePersistsAllFourRenamedTimers() throws Exception {
//        var furnace = read("net/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity");
//        for (String field : new String[]{"litTimeRemaining", "litTotalTime", "cookingTimer", "cookingTotalTime"}) {
//            assertTrue(furnace.fields.stream().anyMatch(f -> f.name.equals(field)));
//            for (String method : new String[]{"loadAdditional", "saveAdditional"})
//                assertTrue(furnace.methods.stream().filter(m -> m.name.equals(method)).anyMatch(m -> {
//                    for (var insn : m.instructions)
//                        if (insn instanceof FieldInsnNode f && f.owner.equals(furnace.name) && f.name.equals(field)) return true;
//                    return false;
//                }), field + ": " + method);
//        }
//    }
//?} else if =1.21.4 {
//    @Test void enderPearlTicketHasChunkArgumentAndFortyTickExpiry() {
//        assertEquals(40, net.minecraft.server.world.ChunkTicketType.ENDER_PEARL.getExpiryTicks());
//        net.minecraft.server.world.ChunkTicketType<net.minecraft.util.math.ChunkPos> type =
//                net.minecraft.server.world.ChunkTicketType.ENDER_PEARL;
//        assertEquals("ender_pearl", type.toString());
//    }
//    @Test void newPickPacketsAreRoutedThroughTheServerThread() throws Exception {
//        var handler = read("net/minecraft/server/network/ServerPlayNetworkHandler");
//        for (String name : new String[]{"onPickItemFromBlock", "onPickItemFromEntity"})
//            assertTrue(invokes(handler, name, "net/minecraft/network/NetworkThreadUtils", "forceMainThread"));
//        assertFalse(handler.methods.stream().anyMatch(m -> m.name.equals("onPickFromInventory")));
//    }
//    @Test void furnacePersistsAllFourRenamedTimers() throws Exception {
//        var furnace = read("net/minecraft/block/entity/AbstractFurnaceBlockEntity");
//        for (String field : new String[]{"litTimeRemaining", "litTotalTime", "cookingTimeSpent", "cookingTotalTime"}) {
//            assertTrue(furnace.fields.stream().anyMatch(f -> f.name.equals(field)));
//            for (String method : new String[]{"readNbt", "writeNbt"})
//                assertTrue(furnace.methods.stream().filter(m -> m.name.equals(method)).anyMatch(m -> {
//                    for (var insn : m.instructions)
//                        if (insn instanceof FieldInsnNode f && f.owner.equals(furnace.name) && f.name.equals(field)) return true;
//                    return false;
//                }), field + ": " + method);
//        }
//    }
//?} else if =1.21.5 {
//    @Test void ticketsUseCountdownAndSeparateLoadingAndSimulation() throws Exception {
//        var type = net.minecraft.server.world.ChunkTicketType.ENDER_PEARL;
//        assertEquals(40, type.expiryTicks());
//        assertTrue(type.isForLoading()); assertTrue(type.isForSimulation());
//        assertTrue(net.minecraft.server.world.ChunkTicketType.PORTAL.persist());
//        assertEquals("minecraft:ender_pearl", net.minecraft.registry.Registries.TICKET_TYPE.getId(type).toString());
//        var ticket = read("net/minecraft/server/world/ChunkTicket");
//        assertTrue(ticket.fields.stream().anyMatch(f -> f.name.equals("ticksLeft")));
//        assertFalse(ticket.fields.stream().anyMatch(f -> f.name.equals("argument") || f.name.equals("tickCreated")));
//        var recovery = com.atsukimc.anticrash.recovery.ChunkRecoveryManager.RECOVERY_TICKET;
//        assertFalse(recovery.persist());
//        assertNotEquals(net.minecraft.server.world.ChunkTicketType.START, recovery);
//    }
//    @Test void newPickPacketsAreRoutedThroughTheServerThread() throws Exception {
//        var handler = read("net/minecraft/server/network/ServerPlayNetworkHandler");
//        for (String name : new String[]{"onPickItemFromBlock", "onPickItemFromEntity"})
//            assertTrue(invokes(handler, name, "net/minecraft/network/NetworkThreadUtils", "forceMainThread"));
//        assertFalse(handler.methods.stream().anyMatch(m -> m.name.equals("onPickFromInventory")));
//    }
//    @Test void furnacePersistsAllFourRenamedTimers() throws Exception {
//        var furnace = read("net/minecraft/block/entity/AbstractFurnaceBlockEntity");
//        for (String field : new String[]{"litTimeRemaining", "litTotalTime", "cookingTimeSpent", "cookingTotalTime"}) {
//            assertTrue(furnace.fields.stream().anyMatch(f -> f.name.equals(field)));
//            for (String method : new String[]{"readNbt", "writeNbt"})
//                assertTrue(furnace.methods.stream().filter(m -> m.name.equals(method)).anyMatch(m -> {
//                    for (var insn : m.instructions)
//                        if (insn instanceof FieldInsnNode f && f.owner.equals(furnace.name) && f.name.equals(field)) return true;
//                    return false;
//                }), field + ": " + method);
//        }
//    }
//?}
    private ClassNode read(String name) throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(name + ".class")) {
            assertNotNull(in, name);
            ClassNode node = new ClassNode(); new ClassReader(in).accept(node, 0); return node;
        }
    }
    private boolean invokes(ClassNode node, String method, String owner, String called) {
        return node.methods.stream().filter(m -> m.name.equals(method)).anyMatch(m -> {
            for (var insn : m.instructions) {
                if (insn instanceof MethodInsnNode i && i.owner.equals(owner) && i.name.equals(called)) return true;
            }
            return false;
        });
    }
    @Test void vanillaOwnsSensorSerializationSince119() throws Exception {
//? if (>=1.21.6 && <=1.21.11) {
        var sensor = read("net/minecraft/block/entity/SculkSensorBlockEntity");
        assertTrue(invokes(sensor, "writeData", "net/minecraft/storage/WriteView", "put"));
        assertTrue(invokes(sensor, "readData", "net/minecraft/storage/ReadView", "read"));
        var listener = read("net/minecraft/world/event/Vibrations$ListenerData");
        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("vibrationSelector") && f.desc.endsWith("/VibrationSelector;")));
        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("vibration")));
//?} else if (>=1.20 && <=1.21.4) {
//        var sensor = read("net/minecraft/block/entity/SculkSensorBlockEntity");
//        assertTrue(invokes(sensor, "writeNbt", "com/mojang/serialization/Codec", "encodeStart"));
//        assertTrue(invokes(sensor, "readNbt", "com/mojang/serialization/Codec", "parse"));
//        var listener = read("net/minecraft/world/event/Vibrations$ListenerData");
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("vibrationSelector") && f.desc.endsWith("/VibrationSelector;")));
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("vibration")));
//?} else if (>=1.19 && <=1.19.2) {
//        var sensor = read("net/minecraft/block/entity/SculkSensorBlockEntity");
//        assertTrue(invokes(sensor, "writeNbt", "com/mojang/serialization/Codec", "encodeStart"));
//        assertTrue(invokes(sensor, "readNbt", "com/mojang/serialization/Codec", "parse"));
//        var listener = read("net/minecraft/world/event/listener/VibrationListener");
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("distance") && f.desc.equals("F")));
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("vibration")));
//?} else if >=26.1 {
//        var sensor = read("net/minecraft/world/level/block/entity/SculkSensorBlockEntity");
//        assertTrue(invokes(sensor, "saveAdditional", "net/minecraft/world/level/storage/ValueOutput", "store"));
//        assertTrue(invokes(sensor, "loadAdditional", "net/minecraft/world/level/storage/ValueInput", "read"));
//        var listener = read("net/minecraft/world/level/gameevent/vibrations/VibrationSystem$Data");
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("selectionStrategy") && f.desc.endsWith("/VibrationSelector;")));
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("currentVibration")));
//?} else if (>=1.19.3 && <=1.19.4) {
//        var sensor = read("net/minecraft/block/entity/SculkSensorBlockEntity");
//        assertTrue(invokes(sensor, "writeNbt", "com/mojang/serialization/Codec", "encodeStart"));
//        assertTrue(invokes(sensor, "readNbt", "com/mojang/serialization/Codec", "parse"));
//        var listener = read("net/minecraft/world/event/listener/VibrationListener");
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("selector") && f.desc.endsWith("/VibrationSelector;")));
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("vibration")));
//?} else if =1.21.5 {
//        var sensor = read("net/minecraft/block/entity/SculkSensorBlockEntity");
//        assertTrue(invokes(sensor, "writeNbt", "net/minecraft/nbt/NbtCompound", "put"));
//        assertTrue(invokes(sensor, "readNbt", "net/minecraft/nbt/NbtCompound", "get"));
//        var listener = read("net/minecraft/world/event/Vibrations$ListenerData");
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("vibrationSelector") && f.desc.endsWith("/VibrationSelector;")));
//        assertTrue(listener.fields.stream().anyMatch(f -> f.name.equals("vibration")));
//?}
    }
    @Test void predictionAckIsCumulativeAndUsedByVanilla() throws Exception {
//? if (>=1.19 && <=1.21.11) {
        var handler = read("net/minecraft/server/network/ServerPlayNetworkHandler");
        for (String method : new String[]{"onPlayerAction", "onPlayerInteractBlock", "onPlayerInteractItem"}) {
            assertTrue(invokes(handler, method, handler.name, "updateSequence"), method);
//?} else if >=26.1 {
//        var handler = read("net/minecraft/server/network/ServerGamePacketListenerImpl");
//        for (String method : new String[]{"handlePlayerAction", "handleUseItemOn", "handleUseItem"}) {
//            assertTrue(invokes(handler, method, handler.name, "ackBlockChangesUpTo"), method);
//?}
        }
//? if (>=1.19 && <=1.21.11) {
        assertTrue(invokes(handler, "updateSequence", "java/lang/Math", "max"));
//?} else if >=26.1 {
//        assertTrue(invokes(handler, "ackBlockChangesUpTo", "java/lang/Math", "max"));
//?}
    }
    @Test void recipeMatchCacheIsSemanticStateNotJustAConstructorValue() throws Exception {
//? if (>=1.21.6 && <=1.21.11) {
        var getter = read("net/minecraft/recipe/ServerRecipeManager$1");
        assertTrue(getter.fields.stream().anyMatch(f -> f.name.equals("id") && f.desc.endsWith("/RegistryKey;")));
        assertTrue(invokes(getter, "getFirstMatch", "net/minecraft/recipe/ServerRecipeManager", "getFirstMatch"));
    }
    @Test void tickingGetterIsNotAnExactLoadedChunkSet() throws Exception {
        var holder = read("net/minecraft/server/world/ChunkHolder");
        assertTrue(invokes(holder, "getWorldChunk", holder.name, "getTickingFuture"));
        var adapter = read("com/atsukimc/anticrash/runtime/VersionChunkView");
        assertTrue(invokes(adapter, "worldChunk", holder.name, "getLatest"));
    }
    @Test void newPortalAndTntRuntimeFieldsExistAndAreNotVanillaNbtAuthority() throws Exception {
        var portal = read("net/minecraft/world/dimension/PortalManager");
        for (String field : new String[]{"portal", "pos", "ticksInPortal", "inPortal"})
            assertTrue(portal.fields.stream().anyMatch(f -> f.name.equals(field)), field);
        var tnt = read("net/minecraft/entity/TntEntity");
        assertTrue(tnt.fields.stream().anyMatch(f -> f.name.equals("teleported") && f.desc.equals("Z")));
        for (var method : tnt.methods) {
            if (!method.name.equals("writeCustomData")) continue;
            for (var insn : method.instructions)
                if (insn instanceof FieldInsnNode f) assertNotEquals("teleported", f.name,
                        "If Vanilla gains this state, remove the duplicate extension after reviewing it");
        }
//?} else if (>=1.19 && <=1.20.6) {
//        var getter = read("net/minecraft/recipe/RecipeManager$1");
//        assertTrue(getter.fields.stream().anyMatch(f -> f.name.equals("id") && f.desc.endsWith("/Identifier;")));
//        assertTrue(invokes(getter, "getFirstMatch", "net/minecraft/recipe/RecipeManager", "getFirstMatch"));
//?} else if (>=1.21.2 && <=1.21.5) {
//        var getter = read("net/minecraft/recipe/ServerRecipeManager$1");
//        assertTrue(getter.fields.stream().anyMatch(f -> f.name.equals("id") && f.desc.endsWith("/RegistryKey;")));
//        assertTrue(invokes(getter, "getFirstMatch", "net/minecraft/recipe/ServerRecipeManager", "getFirstMatch"));
//    }
//    @Test void tickingGetterIsNotAnExactLoadedChunkSet() throws Exception {
//        var holder = read("net/minecraft/server/world/ChunkHolder");
//        assertTrue(invokes(holder, "getWorldChunk", holder.name, "getTickingFuture"));
//        var adapter = read("com/atsukimc/anticrash/runtime/VersionChunkView");
//        assertTrue(invokes(adapter, "worldChunk", holder.name, "getLatest"));
//    }
//    @Test void newPortalAndTntRuntimeFieldsExistAndAreNotVanillaNbtAuthority() throws Exception {
//        var portal = read("net/minecraft/world/dimension/PortalManager");
//        for (String field : new String[]{"portal", "pos", "ticksInPortal", "inPortal"})
//            assertTrue(portal.fields.stream().anyMatch(f -> f.name.equals(field)), field);
//        var tnt = read("net/minecraft/entity/TntEntity");
//        assertTrue(tnt.fields.stream().anyMatch(f -> f.name.equals("teleported") && f.desc.equals("Z")));
//        for (var method : tnt.methods) {
//            if (!method.name.equals("writeCustomDataToNbt")) continue;
//            for (var insn : method.instructions)
//                if (insn instanceof FieldInsnNode f) assertNotEquals("teleported", f.name,
//                        "If Vanilla gains this state, remove the duplicate extension after reviewing it");
//        }
//?} else if >=26.1 {
//        var getter = read("net/minecraft/world/item/crafting/RecipeManager$1");
//        assertTrue(getter.fields.stream().anyMatch(f -> f.name.equals("lastRecipe") && f.desc.endsWith("/ResourceKey;")));
//        assertTrue(invokes(getter, "getRecipeFor", "net/minecraft/world/item/crafting/RecipeManager", "getRecipeFor"));
//    }
//    @Test void tickingGetterIsNotAnExactLoadedChunkSet() throws Exception {
//        var holder = read("net/minecraft/server/level/ChunkHolder");
//        assertTrue(invokes(holder, "getTickingChunk", holder.name, "getTickingChunkFuture"));
//        var adapter = read("com/atsukimc/anticrash/runtime/VersionChunkView");
//        assertTrue(invokes(adapter, "worldChunk", holder.name, "getLatestChunk"));
//    }
//    @Test void newPortalAndTntRuntimeFieldsExistAndAreNotVanillaNbtAuthority() throws Exception {
//        var portal = read("net/minecraft/world/entity/PortalProcessor");
//        for (String field : new String[]{"portal", "entryPosition", "portalTime", "insidePortalThisTick"})
//            assertTrue(portal.fields.stream().anyMatch(f -> f.name.equals(field)), field);
//        var tnt = read("net/minecraft/world/entity/item/PrimedTnt");
//        assertTrue(tnt.fields.stream().anyMatch(f -> f.name.equals("usedPortal") && f.desc.equals("Z")));
//        for (var method : tnt.methods) {
//            if (!method.name.equals("addAdditionalSaveData")) continue;
//            for (var insn : method.instructions)
//                if (insn instanceof FieldInsnNode f) assertNotEquals("usedPortal", f.name,
//                        "If Vanilla gains this state, remove the duplicate extension after reviewing it");
//        }
//?} else if (>=1.21 && <=1.21.1) {
//        var getter = read("net/minecraft/recipe/RecipeManager$1");
//        assertTrue(getter.fields.stream().anyMatch(f -> f.name.equals("id") && f.desc.endsWith("/Identifier;")));
//        assertTrue(invokes(getter, "getFirstMatch", "net/minecraft/recipe/RecipeManager", "getFirstMatch"));
//    }
//    @Test void tickingGetterIsNotAnExactLoadedChunkSet() throws Exception {
//        var holder = read("net/minecraft/server/world/ChunkHolder");
//        assertTrue(invokes(holder, "getWorldChunk", holder.name, "getTickingFuture"));
//        var adapter = read("com/atsukimc/anticrash/runtime/VersionChunkView");
//        assertTrue(invokes(adapter, "worldChunk", holder.name, "getLatest"));
//    }
//    @Test void newPortalAndTntRuntimeFieldsExistAndAreNotVanillaNbtAuthority() throws Exception {
//        var portal = read("net/minecraft/world/dimension/PortalManager");
//        for (String field : new String[]{"portal", "pos", "ticksInPortal", "inPortal"})
//            assertTrue(portal.fields.stream().anyMatch(f -> f.name.equals(field)), field);
//        var tnt = read("net/minecraft/entity/TntEntity");
//        assertTrue(tnt.fields.stream().anyMatch(f -> f.name.equals("teleported") && f.desc.equals("Z")));
//        for (var method : tnt.methods) {
//            if (!method.name.equals("writeCustomDataToNbt")) continue;
//            for (var insn : method.instructions)
//                if (insn instanceof FieldInsnNode f) assertNotEquals("teleported", f.name,
//                        "If Vanilla gains this state, remove the duplicate extension after reviewing it");
//        }
//?}
    }
}
//?}
