package com.atsukimc.anticrash.mixin;

//? if <=1.21.11 {
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.world.storage.ChunkDataAccess;
//?} else if >=26.1 {
//import net.minecraft.world.level.entity.EntityPersistentStorage;
//import net.minecraft.world.level.entity.PersistentEntitySectionManager;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <=1.21.11 {
@Mixin(ServerEntityManager.class)
//?} else if >=26.1 {
//@Mixin(PersistentEntitySectionManager.class)
//?}
public interface ServerEntityManagerAccessorMixin {
//? if <=1.21.11 {
    @Accessor("dataAccess")
    ChunkDataAccess<?> anticrash$getDataAccess();
//?} else if >=26.1 {
//    @Accessor("permanentStorage")
//    EntityPersistentStorage<?> anticrash$getDataAccess();
//?}
}
