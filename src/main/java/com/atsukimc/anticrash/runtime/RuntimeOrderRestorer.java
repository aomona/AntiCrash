package com.atsukimc.anticrash.runtime;

import com.atsukimc.anticrash.mixin.ChunkTicketAccessorMixin;
import com.atsukimc.anticrash.mixin.ChunkTicketManagerAccessorMixin;
import com.atsukimc.anticrash.mixin.EntityListAccessorMixin;
import com.atsukimc.anticrash.mixin.ServerWorldAccessorMixin;
import com.atsukimc.anticrash.mixin.WorldAccessorMixin;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
//? if (>=1.18 && <=1.21.11) {
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

//?} else if >=26.1 {
//?} else if =1.16.1 {
//import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
//import net.minecraft.entity.Entity;
//import net.minecraft.server.world.ChunkTicket;
//import net.minecraft.server.world.ChunkTicketManager;
//import net.minecraft.server.world.ChunkTicketType;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.collection.SortedArraySet;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.world.chunk.BlockEntityTickInvoker;
//
//?}
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//? if <=1.21.11 {
//?} else if >=26.1 {
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.Ticket;
//import net.minecraft.server.level.TicketType;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.TicketStorage;
//import net.minecraft.world.level.block.entity.TickingBlockEntity;
//?}

/**
 * Puts the tick order and the short-lived tickets of the checkpoint back before the first
 * resumed tick. After a restart Minecraft rebuilds these from the order chunks happened to load
 * in, which changes which hopper, piston or TNT acts first and so changes what a machine does.
 *
 * <p>Everything here is tolerant: whatever still exists is put in the checkpoint's order, and
 * anything the checkpoint did not know (a player who joined during recovery) keeps its place at
 * the end. Nothing is created, removed or ticked.</p>
 */
public final class RuntimeOrderRestorer {
    public record Result(int restored, int unknownLive, int missing) {
    }

    private static final Pattern BLOCK_POS = Pattern.compile("x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)");
//? if >=1.21.4 {
    private static final Pattern CHUNK_POS = Pattern.compile("\\[(-?\\d+),\\s*(-?\\d+)\\]");
//?} else if <=1.21.3 {
//?}

    private RuntimeOrderRestorer() {
    }

    private static String tickerKey(int x, int y, int z, String name) {
        return x + "," + y + "," + z + "|" + name;
    }

    /** @return null when the live lists are not in a state that can be reordered safely */
//? if <=1.21.11 {
    public static Result blockEntityTickers(ServerWorld world, List<BlockEntityTickerSnapshot> expected) {
//?} else if >=26.1 {
//    public static Result blockEntityTickers(ServerLevel world, List<BlockEntityTickerSnapshot> expected) {
//?}
        WorldAccessorMixin accessor = (WorldAccessorMixin) world;
//? if <=1.21.11 {
        List<BlockEntityTickInvoker> current = accessor.anticrash$getBlockEntityTickers();
        List<BlockEntityTickInvoker> pending = accessor.anticrash$getPendingBlockEntityTickers();
        Map<String, BlockEntityTickInvoker> live = new LinkedHashMap<>();
        for (List<BlockEntityTickInvoker> list : List.of(current, pending)) {
            for (BlockEntityTickInvoker ticker : list) {
//?} else if >=26.1 {
//        List<TickingBlockEntity> current = accessor.anticrash$getBlockEntityTickers();
//        List<TickingBlockEntity> pending = accessor.anticrash$getPendingBlockEntityTickers();
//        Map<String, TickingBlockEntity> live = new LinkedHashMap<>();
//        for (List<TickingBlockEntity> list : List.of(current, pending)) {
//            for (TickingBlockEntity ticker : list) {
//?}
                // Vanilla drops a removed ticker unrun on its next pass; it carries no order.
                if (BlockEntityTickerSnapshot.removed(ticker)) continue;
                BlockPos pos = ticker.getPos();
//? if <=1.21.11 {
                if (live.put(tickerKey(pos.getX(), pos.getY(), pos.getZ(), ticker.getName()), ticker) != null) {
//?} else if >=26.1 {
//                if (live.put(tickerKey(pos.getX(), pos.getY(), pos.getZ(), ticker.getType()), ticker) != null) {
//?}
                    return null;
                }
            }
        }
        // World#tickBlockEntities appends PENDING to CURRENT and then runs CURRENT in order, so
        // where a live ticker sits today does not matter, only the two lists' final contents.
//? if <=1.21.11 {
        List<BlockEntityTickInvoker> orderedCurrent = new ArrayList<>();
        List<BlockEntityTickInvoker> orderedPending = new ArrayList<>();
//?} else if >=26.1 {
//        List<TickingBlockEntity> orderedCurrent = new ArrayList<>();
//        List<TickingBlockEntity> orderedPending = new ArrayList<>();
//?}
        int missing = 0;
        for (BlockEntityTickerSnapshot snapshot : expected) {
//? if <=1.21.11 {
            BlockEntityTickInvoker ticker = live.remove(tickerKey(snapshot.x, snapshot.y, snapshot.z, snapshot.name));
//?} else if >=26.1 {
//            TickingBlockEntity ticker = live.remove(tickerKey(snapshot.x, snapshot.y, snapshot.z, snapshot.name));
//?}
            if (ticker == null) {
                missing++;
            } else if (BlockEntityTickerSnapshot.PENDING.equals(snapshot.queue)) {
                orderedPending.add(ticker);
            } else {
                orderedCurrent.add(ticker);
            }
        }
        int restored = orderedCurrent.size() + orderedPending.size();
        int unknown = live.size();
        orderedPending.addAll(live.values());
        current.clear();
        current.addAll(orderedCurrent);
        pending.clear();
        pending.addAll(orderedPending);
        return new Result(restored, unknown, missing);
    }

    /** @return null while the entity list is being iterated */
//? if <=1.21.11 {
    public static Result entities(ServerWorld world, List<EntityRuntimeSnapshot> expected) {
//?} else if >=26.1 {
//    public static Result entities(ServerLevel world, List<EntityRuntimeSnapshot> expected) {
//?}
        EntityListAccessorMixin accessor =
                (EntityListAccessorMixin) ((ServerWorldAccessorMixin) world).anticrash$getEntityList();
        if (accessor.anticrash$getIterating() != null) return null;
        Int2ObjectMap<Entity> entities = accessor.anticrash$getEntities();
        Map<String, Entity> live = new LinkedHashMap<>();
        for (Entity entity : entities.values()) {
//? if <=1.21.11 {
            if (live.put(entity.getUuidAsString(), entity) != null) return null;
//?} else if >=26.1 {
//            if (live.put(entity.getStringUUID(), entity) != null) return null;
//?}
        }
        List<Entity> ordered = new ArrayList<>(live.size());
        int missing = 0;
        for (EntityRuntimeSnapshot snapshot : expected) {
            Entity entity = snapshot.uuid == null ? null : live.remove(snapshot.uuid);
            if (entity == null) missing++;
            else ordered.add(entity);
        }
        int restored = ordered.size();
        int unknown = live.size();
        ordered.addAll(live.values());
        // The map is insertion-ordered and keyed by network id; EntityList iterates it as is.
        entities.clear();
//? if >=1.18 {
        for (Entity entity : ordered) entities.put(entity.getId(), entity);
//?} else if =1.16.1 {
//        for (Entity entity : ordered) entities.put(entity.getEntityId(), entity);
//?}
        return new Result(restored, unknown, missing);
    }

    /**
     * Re-creates the checkpoint's expiring tickets with the lifetime they had left: PORTAL (300
     * ticks after something used a portal), ENDER_PEARL (40 ticks) and UNKNOWN
     * (1 tick after a chunk was fetched by code such as a hopper or an entity looking into the
     * next chunk). Vanilla saves none of them. Each is added through the same ChunkTicketManager
     * method Vanilla uses for that type and expires by itself, so nothing can leak. LIGHT tickets
     * are owned by the lighting engine, which removes them itself, and are never re-created.
     *
     * <p>Call it right before the first resumed tick: the manager ages tickets whenever the chunk
     * manager is pumped, also while recovery is frozen. Adding a ticket that already exists only
     * refreshes it, so calling this twice is harmless.</p>
     *
     * @return the number of tickets restored
     */
    private static String expiringKey(String dimension, int x, int z, String type, int level, String argument) {
        // A post-teleport ticket's argument is a network entity id, which a restart renumbers.
        return dimension + "|" + x + "," + z + "|" + type + "|" + level + "|"
                + ("post_teleport".equals(type) ? "-" : argument);
    }

    /** The key of every expiring ticket of the checkpoint that {@link #expiringTickets} re-creates. */
    static java.util.Set<String> expiringKeys(List<TicketLedgerSnapshot.Entry> ledger) {
        java.util.Set<String> keys = new java.util.HashSet<>();
        for (TicketLedgerSnapshot.Entry entry : ledger) {
            if (entry == null || entry.type == null || entry.remainingTicks == null) continue;
            keys.add(expiringKey(entry.dimension, entry.x, entry.z, entry.type, entry.level, entry.argument));
        }
        return keys;
    }

    /**
     * Removes the short-lived tickets that appeared while recovery was frozen.
     *
     * <p>Every chunk lookup on the server thread that misses a four-entry cache leaves a one-tick
     * UNKNOWN ticket behind, and such a lookup of a chunk that is not loaded loads it. Restoring
     * FakePlayers, a player joining, or a command during the freeze all do that, and nothing ages
     * those tickets until the world ticks again. They are not part of the checkpoint: left alone,
     * the first tick would start with a few chunks loaded that the checkpoint did not have.</p>
     *
     * @return the number of tickets removed
     */
//? if <=1.21.11 {
    public static int removeStrayExpiringTickets(Map<String, ServerWorld> worlds,
//?} else if >=26.1 {
//    public static int removeStrayExpiringTickets(Map<String, ServerLevel> worlds,
//?}
                                                 List<TicketLedgerSnapshot.Entry> ledger) {
        java.util.Set<String> expected = expiringKeys(ledger);
        int removed = 0;
//? if (>=1.21.5 && <=1.21.11) {
        for (Map.Entry<String, ServerWorld> world : worlds.entrySet()) {
            ChunkTicketManager manager = ((com.atsukimc.anticrash.mixin.ServerChunkManagerAccessorMixin)
                    world.getValue().getChunkManager()).anticrash$getTicketManager();
//?} else if <=1.20.6 {
//        for (Map.Entry<String, ServerWorld> world : worlds.entrySet()) {
//            ChunkTicketManager manager = world.getValue().getChunkManager().threadedAnvilChunkStorage.getTicketManager();
//?} else if (>=1.21 && <=1.21.4) {
//        for (Map.Entry<String, ServerWorld> world : worlds.entrySet()) {
//            ChunkTicketManager manager = world.getValue().getChunkManager().chunkLoadingManager.getTicketManager();
//?} else if >=26.1 {
//        for (Map.Entry<String, ServerLevel> world : worlds.entrySet()) {
//            TicketStorage manager = ((com.atsukimc.anticrash.mixin.ServerChunkManagerAccessorMixin)
//                    world.getValue().getChunkSource()).anticrash$getTicketManager();
//?}
            ChunkTicketManagerAccessorMixin accessor = (ChunkTicketManagerAccessorMixin) manager;
            List<Object[]> stray = new ArrayList<>();
            for (var position : accessor.anticrash$getTicketsByPosition().long2ObjectEntrySet()) {
//? if (>=1.21.5 && <=1.21.11) {
                ChunkPos chunk = new ChunkPos(position.getLongKey());
                for (ChunkTicket ticket : position.getValue()) {
                    ChunkTicketType type = ticket.getType();
                    if (type != ChunkTicketType.UNKNOWN && type != ChunkTicketType.ENDER_PEARL
                            && type != ChunkTicketType.PORTAL) continue;
                    String name = net.minecraft.registry.Registries.TICKET_TYPE.getId(type).getPath();
                    if (!expected.contains(expiringKey(world.getKey(), chunk.x, chunk.z, name,
                            ticket.getLevel(), "-"))) {
                        stray.add(new Object[]{ticket, chunk});
//?} else if (>=1.18 && <=1.21.3) {
//                ChunkPos chunk = new ChunkPos(position.getLongKey());
//                for (ChunkTicket<?> ticket : position.getValue()) {
//                    ChunkTicketType<?> type = ticket.getType();
//                    if (type != ChunkTicketType.UNKNOWN && type != ChunkTicketType.POST_TELEPORT
//                            && type != ChunkTicketType.PORTAL) continue;
//                    Object argument = ((ChunkTicketAccessorMixin) (Object) ticket).anticrash$getArgument();
//                    if (!expected.contains(expiringKey(world.getKey(), chunk.x, chunk.z, String.valueOf(type),
//                            ticket.getLevel(), String.valueOf(argument)))) {
//                        stray.add(new Object[]{type, chunk, ticket.getLevel(), argument});
//?} else if >=26.1 {
//                ChunkPos chunk = ChunkPos.unpack(position.getLongKey());
//                for (Ticket ticket : position.getValue()) {
//                    TicketType type = ticket.getType();
//                    if (type != TicketType.UNKNOWN && type != TicketType.ENDER_PEARL
//                            && type != TicketType.PORTAL) continue;
//                    String name = net.minecraft.core.registries.BuiltInRegistries.TICKET_TYPE.getKey(type).getPath();
//                    if (!expected.contains(expiringKey(world.getKey(), chunk.x(), chunk.z(), name,
//                            ticket.getTicketLevel(), "-"))) {
//                        stray.add(new Object[]{ticket, chunk});
//?} else if =1.16.1 {
//                ChunkPos chunk = new ChunkPos(position.getLongKey());
//                for (ChunkTicket<?> ticket : position.getValue()) {
//                    ChunkTicketType<?> type = ticket.getType();
//                    if (type != ChunkTicketType.field_14032 && type != ChunkTicketType.field_19347
//                            && type != ChunkTicketType.field_19280) continue;
//                    Object argument = ((ChunkTicketAccessorMixin) (Object) ticket).anticrash$getArgument();
//                    if (!expected.contains(expiringKey(world.getKey(), chunk.x, chunk.z, String.valueOf(type),
//                            ticket.getLevel(), String.valueOf(argument)))) {
//                        stray.add(new Object[]{type, chunk, ticket.getLevel(), argument});
//?} else if =1.21.4 {
//                ChunkPos chunk = new ChunkPos(position.getLongKey());
//                for (ChunkTicket<?> ticket : position.getValue()) {
//                    ChunkTicketType<?> type = ticket.getType();
//                    if (type != ChunkTicketType.UNKNOWN && type != ChunkTicketType.ENDER_PEARL
//                            && type != ChunkTicketType.PORTAL) continue;
//                    Object argument = ((ChunkTicketAccessorMixin) (Object) ticket).anticrash$getArgument();
//                    if (!expected.contains(expiringKey(world.getKey(), chunk.x, chunk.z, String.valueOf(type),
//                            ticket.getLevel(), String.valueOf(argument)))) {
//                        stray.add(new Object[]{type, chunk, ticket.getLevel(), argument});
//?}
                    }
                }
            }
            for (Object[] ticket : stray) {
//? if (>=1.21.5 && <=1.21.11) {
                manager.removeTicket((ChunkTicket) ticket[0], (ChunkPos) ticket[1]);
//?} else if <=1.21.4 {
//                removeTicket(manager, (ChunkTicketType<?>) ticket[0], (ChunkPos) ticket[1], (Integer) ticket[2], ticket[3]);
//?} else if >=26.1 {
//                manager.removeTicket((Ticket) ticket[0], (ChunkPos) ticket[1]);
//?}
                removed++;
            }
        }
        return removed;
//? if >=1.21.5 {
//?} else if <=1.21.4 {
//    }
//
//    @SuppressWarnings("unchecked")
//    private static <T> void removeTicket(ChunkTicketManager manager, ChunkTicketType<?> type, ChunkPos chunk,
//                                         int level, Object argument) {
//        manager.removeTicketWithLevel((ChunkTicketType<T>) type, chunk, level, (T) argument);
//?}
    }

//? if <=1.21.11 {
    public static int expiringTickets(Map<String, ServerWorld> worlds, List<TicketLedgerSnapshot.Entry> ledger) {
//?} else if >=26.1 {
//    public static int expiringTickets(Map<String, ServerLevel> worlds, List<TicketLedgerSnapshot.Entry> ledger) {
//?}
        int restored = 0;
        for (TicketLedgerSnapshot.Entry entry : ledger) {
            if (entry == null || entry.type == null || entry.remainingTicks == null
                    || entry.remainingTicks < 0 || entry.argument == null) continue;
//? if <=1.21.11 {
            ServerWorld world = worlds.get(entry.dimension);
//?} else if >=26.1 {
//            ServerLevel world = worlds.get(entry.dimension);
//?}
            if (world == null || entry.level < 0 || entry.level > 64) continue;
            ChunkPos chunk = new ChunkPos(entry.x, entry.z);
//? if (>=1.21.5 && <=1.21.11) {
            ChunkTicketManager manager = ((com.atsukimc.anticrash.mixin.ServerChunkManagerAccessorMixin)
                    world.getChunkManager()).anticrash$getTicketManager();
            ChunkTicketType type = switch (entry.type) {
                case "portal" -> ChunkTicketType.PORTAL;
                case "ender_pearl" -> ChunkTicketType.ENDER_PEARL;
                case "unknown" -> ChunkTicketType.UNKNOWN;
                default -> null;
            };
            if (type == null) continue;
            // Public addition deduplicates by type identity + level. PORTAL is now persisted by
            // Vanilla; refresh the existing ticket, rather than introducing a second ledger entry.
            manager.addTicket(type, chunk, entry.level);
//?} else if (>=1.18 && <=1.20.6) {
//            ChunkTicketManager manager = world.getChunkManager().threadedAnvilChunkStorage.getTicketManager();
//            ChunkTicketType<?> type;
//            Object argument;
//            switch (entry.type) {
//                case "portal" -> {
//                    Matcher matcher = BLOCK_POS.matcher(entry.argument);
//                    if (!matcher.find() || entry.level > 33) continue;
//                    BlockPos portal = new BlockPos(Integer.parseInt(matcher.group(1)),
//                            Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
//                    manager.addTicket(ChunkTicketType.PORTAL, chunk, 33 - entry.level, portal);
//                    type = ChunkTicketType.PORTAL;
//                    argument = portal;
//                }
//                case "post_teleport" -> {
//                    Integer id;
//                    try {
//                        id = Integer.valueOf(entry.argument.trim());
//                    } catch (NumberFormatException invalid) {
//                        continue;
//                    }
//                    if (entry.level > 33) continue;
//                    manager.addTicket(ChunkTicketType.POST_TELEPORT, chunk, 33 - entry.level, id);
//                    type = ChunkTicketType.POST_TELEPORT;
//                    argument = id;
//                }
//                case "unknown" -> {
//                    manager.addTicketWithLevel(ChunkTicketType.UNKNOWN, chunk, entry.level, chunk);
//                    type = ChunkTicketType.UNKNOWN;
//                    argument = chunk;
//                }
//                default -> {
//                    continue;
//                }
//            }
//?} else if (>=1.21 && <=1.21.3) {
//            ChunkTicketManager manager = world.getChunkManager().chunkLoadingManager.getTicketManager();
//            ChunkTicketType<?> type;
//            Object argument;
//            switch (entry.type) {
//                case "portal" -> {
//                    Matcher matcher = BLOCK_POS.matcher(entry.argument);
//                    if (!matcher.find() || entry.level > 33) continue;
//                    BlockPos portal = new BlockPos(Integer.parseInt(matcher.group(1)),
//                            Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
//                    manager.addTicket(ChunkTicketType.PORTAL, chunk, 33 - entry.level, portal);
//                    type = ChunkTicketType.PORTAL;
//                    argument = portal;
//                }
//                case "post_teleport" -> {
//                    Integer id;
//                    try {
//                        id = Integer.valueOf(entry.argument.trim());
//                    } catch (NumberFormatException invalid) {
//                        continue;
//                    }
//                    if (entry.level > 33) continue;
//                    manager.addTicket(ChunkTicketType.POST_TELEPORT, chunk, 33 - entry.level, id);
//                    type = ChunkTicketType.POST_TELEPORT;
//                    argument = id;
//                }
//                case "unknown" -> {
//                    manager.addTicketWithLevel(ChunkTicketType.UNKNOWN, chunk, entry.level, chunk);
//                    type = ChunkTicketType.UNKNOWN;
//                    argument = chunk;
//                }
//                default -> {
//                    continue;
//                }
//            }
//?} else if >=26.1 {
//            TicketStorage manager = ((com.atsukimc.anticrash.mixin.ServerChunkManagerAccessorMixin)
//                    world.getChunkSource()).anticrash$getTicketManager();
//            TicketType type = switch (entry.type) {
//                case "portal" -> TicketType.PORTAL;
//                case "ender_pearl" -> TicketType.ENDER_PEARL;
//                case "unknown" -> TicketType.UNKNOWN;
//                default -> null;
//            };
//            if (type == null) continue;
//            // Public addition deduplicates by type identity + level. PORTAL is now persisted by
//            // Vanilla; refresh the existing ticket, rather than introducing a second ledger entry.
//            manager.addTicketWithRadius(type, chunk, entry.level);
//?} else if =1.16.1 {
//            ChunkTicketManager manager = world.getChunkManager().threadedAnvilChunkStorage.getTicketManager();
//            ChunkTicketType<?> type;
//            Object argument;
//            switch (entry.type) {
//                case "portal" -> {
//                    Matcher matcher = BLOCK_POS.matcher(entry.argument);
//                    if (!matcher.find() || entry.level > 33) continue;
//                    BlockPos portal = new BlockPos(Integer.parseInt(matcher.group(1)),
//                            Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
//                    manager.addTicket(ChunkTicketType.field_19280, chunk, 33 - entry.level, portal);
//                    type = ChunkTicketType.field_19280;
//                    argument = portal;
//                }
//                case "post_teleport" -> {
//                    Integer id;
//                    try {
//                        id = Integer.valueOf(entry.argument.trim());
//                    } catch (NumberFormatException invalid) {
//                        continue;
//                    }
//                    if (entry.level > 33) continue;
//                    manager.addTicket(ChunkTicketType.field_19347, chunk, 33 - entry.level, id);
//                    type = ChunkTicketType.field_19347;
//                    argument = id;
//                }
//                case "unknown" -> {
//                    manager.addTicketWithLevel(ChunkTicketType.field_14032, chunk, entry.level, chunk);
//                    type = ChunkTicketType.field_14032;
//                    argument = chunk;
//                }
//                default -> {
//                    continue;
//                }
//            }
//?} else if =1.21.4 {
//            ChunkTicketManager manager = world.getChunkManager().chunkLoadingManager.getTicketManager();
//            ChunkTicketType<?> type;
//            Object argument;
//            switch (entry.type) {
//                case "portal" -> {
//                    Matcher matcher = BLOCK_POS.matcher(entry.argument);
//                    if (!matcher.find() || entry.level > 33) continue;
//                    BlockPos portal = new BlockPos(Integer.parseInt(matcher.group(1)),
//                            Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
//                    manager.addTicket(ChunkTicketType.PORTAL, chunk, 33 - entry.level, portal);
//                    type = ChunkTicketType.PORTAL;
//                    argument = portal;
//                }
//                case "ender_pearl" -> {
//                    // POST_TELEPORT no longer exists. This is a distinct 40-tick ticket whose
//                    // argument is ChunkPos, not the old teleporting entity's network id.
//                    Matcher matcher = CHUNK_POS.matcher(entry.argument);
//                    if (!matcher.matches()) throw new IllegalArgumentException("Invalid ender-pearl ticket argument");
//                    ChunkPos pearl = new ChunkPos(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
//                    manager.addTicketWithLevel(ChunkTicketType.ENDER_PEARL, chunk, entry.level, pearl);
//                    type = ChunkTicketType.ENDER_PEARL;
//                    argument = pearl;
//                }
//                case "unknown" -> {
//                    manager.addTicketWithLevel(ChunkTicketType.UNKNOWN, chunk, entry.level, chunk);
//                    type = ChunkTicketType.UNKNOWN;
//                    argument = chunk;
//                }
//                default -> {
//                    continue;
//                }
//            }
//?}
            ChunkTicketManagerAccessorMixin accessor = (ChunkTicketManagerAccessorMixin) manager;
//? if (>=1.21.5 && <=1.21.11) {
            List<ChunkTicket> tickets = accessor.anticrash$getTicketsByPosition().get(chunk.toLong());
//?} else if <=1.21.4 {
//            SortedArraySet<ChunkTicket<?>> tickets = accessor.anticrash$getTicketsByPosition().get(chunk.toLong());
//?} else if >=26.1 {
//            List<Ticket> tickets = accessor.anticrash$getTicketsByPosition().get(chunk.pack());
//?}
            if (tickets == null) continue;
//? if (>=1.21.5 && <=1.21.11) {
            long expiry = type.expiryTicks();
            for (ChunkTicket ticket : tickets) {
//?} else if <=1.21.4 {
//            long expiry = type.getExpiryTicks();
//            for (ChunkTicket<?> ticket : tickets) {
//?} else if >=26.1 {
//            long expiry = type.timeout();
//            for (Ticket ticket : tickets) {
//?}
                ChunkTicketAccessorMixin ticketAccessor = (ChunkTicketAccessorMixin) (Object) ticket;
//? if (>=1.21.5 && <=1.21.11) {
                if (ticketAccessor.anticrash$getType() == type && ticket.getLevel() == entry.level) {
                    if (entry.remainingTicks > expiry) throw new IllegalArgumentException("Invalid remaining ticket lifetime");
                    ticketAccessor.anticrash$setTicksLeft(entry.remainingTicks);
//?} else if <=1.21.4 {
//                if (ticketAccessor.anticrash$getType() == type && ticket.getLevel() == entry.level
//                        && argument.equals(ticketAccessor.anticrash$getArgument())) {
//                    ticketAccessor.anticrash$setTickCreated(tickCreatedFor(
//                            accessor.anticrash$getAge(), expiry, entry.remainingTicks));
//?} else if >=26.1 {
//                if (ticketAccessor.anticrash$getType() == type && ticket.getTicketLevel() == entry.level) {
//                    if (entry.remainingTicks > expiry) throw new IllegalArgumentException("Invalid remaining ticket lifetime");
//                    ticketAccessor.anticrash$setTicksLeft(entry.remainingTicks);
//?}
                    restored++;
                }
            }
        }
        return restored;
//? if >=1.21.5 {
//?} else if <=1.21.4 {
//    }
//
//    /**
//     * Vanilla drops a ticket once {@code age - tickCreated > expiry}; a ticket with
//     * {@code remaining} ticks left therefore satisfies {@code age - tickCreated == expiry - remaining}.
//     */
//    static long tickCreatedFor(long age, long expiry, long remaining) {
//        return age - (expiry - Math.min(expiry, Math.max(0L, remaining)));
//?}
    }

    /**
     * True when every ticket of the checkpoint that Vanilla keeps without expiry (player, forced,
     * start, dragon) exists again at the same level. Then the chunks the checkpoint had loaded are
     * held by the same tickets as before and AntiCrash's own recovery tickets can go before the
     * first tick, instead of keeping chunks loaded that were about to unload.
     */
//? if <=1.21.11 {
    public static boolean permanentTicketsCovered(Iterable<ServerWorld> worlds, List<TicketLedgerSnapshot.Entry> ledger) {
//?} else if >=26.1 {
//    public static boolean permanentTicketsCovered(Iterable<ServerLevel> worlds, List<TicketLedgerSnapshot.Entry> ledger) {
//?}
        return permanentTicketsCovered(TicketLedgerSnapshot.capture(worlds), ledger);
    }

    /** {@code current} is the live ledger; tickets that expire and light tickets are not waited for. */
    public static boolean permanentTicketsCovered(List<TicketLedgerSnapshot.Entry> current,
                                                  List<TicketLedgerSnapshot.Entry> ledger) {
        return missingPermanentTickets(current, ledger) == 0;
    }

    /** How many permanent tickets of the checkpoint do not exist in {@code current}. */
    public static int missingPermanentTickets(List<TicketLedgerSnapshot.Entry> current,
                                              List<TicketLedgerSnapshot.Entry> ledger) {
        java.util.Set<String> live = new java.util.HashSet<>();
        for (TicketLedgerSnapshot.Entry entry : current) live.add(permanentKey(entry));
        int missing = 0;
        for (TicketLedgerSnapshot.Entry entry : ledger) {
            if (entry == null || entry.expiryTicks != 0 || "light".equals(entry.type)) continue;
            if (!live.contains(permanentKey(entry))) missing++;
        }
        return missing;
    }

    private static String permanentKey(TicketLedgerSnapshot.Entry entry) {
        return entry.dimension + "|" + entry.x + "," + entry.z + "|" + entry.type + "|" + entry.level;
    }

//? if <=1.21.11 {
    public static Map<String, ServerWorld> worlds(Iterable<ServerWorld> worlds) {
        Map<String, ServerWorld> byId = new HashMap<>();
        for (ServerWorld world : worlds) byId.put(world.getRegistryKey().getValue().toString(), world);
//?} else if >=26.1 {
//    public static Map<String, ServerLevel> worlds(Iterable<ServerLevel> worlds) {
//        Map<String, ServerLevel> byId = new HashMap<>();
//        for (ServerLevel world : worlds) byId.put(world.dimension().identifier().toString(), world);
//?}
        return byId;
    }
}
