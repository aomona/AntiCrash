package com.atsukimc.anticrash.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
//? if <=1.21.11 {
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.util.collection.SortedArraySet;
//?} else if >=26.1 {
//import net.minecraft.server.level.Ticket;
//import net.minecraft.world.level.TicketStorage;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Read-only ticket ledger accessor verified against the 1.18.2 mapped class. */
//? if <=1.21.11 {
@Mixin(ChunkTicketManager.class)
//?} else if >=26.1 {
//@Mixin(TicketStorage.class)
//?}
public interface ChunkTicketManagerAccessorMixin {
//? if (>=1.21.5 && <=1.21.11) {
    @Accessor("tickets")
    Long2ObjectOpenHashMap<java.util.List<ChunkTicket>> anticrash$getTicketsByPosition();
//?} else if <=1.21.4 {
//    @Accessor("ticketsByPosition")
//    Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> anticrash$getTicketsByPosition();
//
//    @Accessor("age")
//    long anticrash$getAge();
//?} else if >=26.1 {
//    @Accessor("tickets")
//    Long2ObjectOpenHashMap<java.util.List<Ticket>> anticrash$getTicketsByPosition();
//?}
}
