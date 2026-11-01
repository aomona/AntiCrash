package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketType;
//?} else if >=26.1 {
//import net.minecraft.server.level.Ticket;
//import net.minecraft.server.level.TicketType;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Read-only access to ticket metadata; the ticket manager is never rewritten. */
//? if <=1.21.11 {
@Mixin(ChunkTicket.class)
//?} else if >=26.1 {
//@Mixin(Ticket.class)
//?}
public interface ChunkTicketAccessorMixin {
    @Accessor("type")
//? if (>=1.21.5 && <=1.21.11) {
    ChunkTicketType anticrash$getType();
//?} else if <=1.21.4 {
//    ChunkTicketType<?> anticrash$getType();
//?} else if >=26.1 {
//    TicketType anticrash$getType();
//?}

//? if >=1.21.5 {
    @Accessor("ticksLeft")
    long anticrash$getTicksLeft();
//?} else if <=1.21.4 {
//    @Accessor("argument")
//    Object anticrash$getArgument();
//?}

//? if >=1.21.5 {
    @Accessor("ticksLeft")
    void anticrash$setTicksLeft(long ticksLeft);
//?} else if <=1.21.4 {
//    @Accessor("tickCreated")
//    long anticrash$getTickCreated();
//
//    @Accessor("tickCreated")
//    void anticrash$setTickCreated(long tickCreated);
//?}
}
