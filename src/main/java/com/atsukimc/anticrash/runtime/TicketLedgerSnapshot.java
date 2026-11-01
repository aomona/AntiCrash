package com.atsukimc.anticrash.runtime;

import com.atsukimc.anticrash.mixin.ChunkTicketManagerAccessorMixin;
import com.atsukimc.anticrash.mixin.ChunkTicketAccessorMixin;
//? if <=1.21.11 {
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.ChunkPos;
//?} else if >=26.1 {
//?}
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.Ticket;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.TicketStorage;
//?}

/**
 * Read-only diagnostic view of the 1.18.2 ticket ledger.
 *
 * <p>Vanilla owns ticket expiry and propagation and recreates player, forced and spawn tickets by
 * itself. The ledger is compared after loading so ticket differences are visible. The one kind
 * written back is the portal ticket (see {@link RuntimeOrderRestorer#portalTickets}): it keeps
 * chunks loaded for 300 ticks after something used a portal and is not saved by Vanilla.</p>
 */
public final class TicketLedgerSnapshot {
    public int formatVersion = 1;
    public final List<Entry> entries = new ArrayList<>();

//? if <=1.21.11 {
    public static List<Entry> capture(Iterable<ServerWorld> worlds) {
//?} else if >=26.1 {
//    public static List<Entry> capture(Iterable<ServerLevel> worlds) {
//?}
        List<Entry> result = new ArrayList<>();
//? if (>=1.21.5 && <=1.21.11) {
        for (ServerWorld world : worlds) {
            ChunkTicketManager manager = ((com.atsukimc.anticrash.mixin.ServerChunkManagerAccessorMixin)
                    world.getChunkManager()).anticrash$getTicketManager();
//?} else if <=1.20.6 {
//        for (ServerWorld world : worlds) {
//            ChunkTicketManager manager = world.getChunkManager().threadedAnvilChunkStorage.getTicketManager();
//?} else if (>=1.21 && <=1.21.4) {
//        for (ServerWorld world : worlds) {
//            ChunkTicketManager manager = world.getChunkManager().chunkLoadingManager.getTicketManager();
//?} else if >=26.1 {
//        for (ServerLevel world : worlds) {
//            TicketStorage manager = ((com.atsukimc.anticrash.mixin.ServerChunkManagerAccessorMixin)
//                    world.getChunkSource()).anticrash$getTicketManager();
//?}
            ChunkTicketManagerAccessorMixin accessor = (ChunkTicketManagerAccessorMixin) manager;
//? if (>=1.21.5 && <=1.21.11) {
            Long2ObjectOpenHashMap<List<ChunkTicket>> tickets =
//?} else if <=1.21.4 {
//            Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> tickets =
//?} else if >=26.1 {
//            Long2ObjectOpenHashMap<List<Ticket>> tickets =
//?}
                    accessor.anticrash$getTicketsByPosition();
//? if (>=1.21.5 && <=1.21.11) {
            for (Long2ObjectMap.Entry<List<ChunkTicket>> positionEntry
//?} else if <=1.21.4 {
//            for (Long2ObjectMap.Entry<SortedArraySet<ChunkTicket<?>>> positionEntry
//?} else if >=26.1 {
//            for (Long2ObjectMap.Entry<List<Ticket>> positionEntry
//?}
                    : tickets.long2ObjectEntrySet()) {
//? if (>=1.21.5 && <=1.21.11) {
                ChunkPos pos = new ChunkPos(positionEntry.getLongKey());
                for (ChunkTicket ticket : positionEntry.getValue()) {
//?} else if <=1.21.4 {
//                ChunkPos pos = new ChunkPos(positionEntry.getLongKey());
//                for (ChunkTicket<?> ticket : positionEntry.getValue()) {
//?} else if >=26.1 {
//                ChunkPos pos = ChunkPos.unpack(positionEntry.getLongKey());
//                for (Ticket ticket : positionEntry.getValue()) {
//?}
                    ChunkTicketAccessorMixin ticketAccessor = (ChunkTicketAccessorMixin) (Object) ticket;
//? if (>=1.21.5 && <=1.21.11) {
                    if (ticket.getType() == com.atsukimc.anticrash.recovery.ChunkRecoveryManager.RECOVERY_TICKET) continue;
                    var typeId = net.minecraft.registry.Registries.TICKET_TYPE.getId(ticket.getType());
                    if (typeId == null) throw new IllegalStateException("Unregistered chunk ticket type");
                    String type = "minecraft".equals(typeId.getNamespace()) ? typeId.getPath() : typeId.toString();
//?} else if <=1.21.4 {
//                    String type = String.valueOf(ticketAccessor.anticrash$getType());
//?} else if >=26.1 {
//                    if (ticket.getType() == com.atsukimc.anticrash.recovery.ChunkRecoveryManager.RECOVERY_TICKET) continue;
//                    var typeId = net.minecraft.core.registries.BuiltInRegistries.TICKET_TYPE.getKey(ticket.getType());
//                    if (typeId == null) throw new IllegalStateException("Unregistered chunk ticket type");
//                    String type = "minecraft".equals(typeId.getNamespace()) ? typeId.getPath() : typeId.toString();
//?}
                    // The recovery ticket exists only in the new JVM and is deliberately not a
                    // checkpoint state category.
                    if ("anticrash_recovery".equals(type)) continue;
                    Entry entry = new Entry();
//? if <=1.21.11 {
                    entry.dimension = world.getRegistryKey().getValue().toString();
                    entry.x = pos.x;
                    entry.z = pos.z;
//?} else if >=26.1 {
//                    entry.dimension = world.dimension().identifier().toString();
//                    entry.x = pos.x();
//                    entry.z = pos.z();
//?}
                    entry.type = type;
//? if (>=1.21.5 && <=1.21.11) {
                    entry.level = ticket.getLevel();
                    entry.argument = "-"; // 1.21.5 no longer has per-ticket arguments or creation age.
                    entry.tickCreated = 0;
                    entry.expiryTicks = ticket.getType().expiryTicks();
//?} else if <=1.21.4 {
//                    entry.level = ticket.getLevel();
//                    entry.argument = String.valueOf(ticketAccessor.anticrash$getArgument());
//                    entry.tickCreated = ticketAccessor.anticrash$getTickCreated();
//                    entry.expiryTicks = ticket.getType().getExpiryTicks();
//?} else if >=26.1 {
//                    entry.level = ticket.getTicketLevel();
//                    entry.argument = "-"; // 1.21.5 no longer has per-ticket arguments or creation age.
//                    entry.tickCreated = 0;
//                    entry.expiryTicks = ticket.getType().timeout();
//?}
                    if (entry.expiryTicks > 0) {
//? if >=1.21.5 {
                        entry.remainingTicks = ticketAccessor.anticrash$getTicksLeft();
//?} else if <=1.21.4 {
//                        // Vanilla keeps a ticket while age - tickCreated <= expiry.
//                        entry.remainingTicks = entry.expiryTicks - (accessor.anticrash$getAge() - entry.tickCreated);
//?}
                    }
                    result.add(entry);
                }
            }
        }
        result.sort(Comparator.comparing(Entry::canonical));
        return result;
    }

    public static final class Entry {
        public String dimension;
        public int x;
        public int z;
        public String type;
        public int level;
        public String argument;
        public long tickCreated;
        public long expiryTicks;
        /** Ticks left before an expiring ticket lapses; null for permanent tickets and old capsules. */
        public Long remainingTicks;

        public String canonical() {
            return dimension + "|" + x + ',' + z + "|" + type + "|" + level + "|"
                    + argument + "|" + tickCreated + "|" + expiryTicks;
        }
    }
}
