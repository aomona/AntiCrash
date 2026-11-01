package com.atsukimc.anticrash.mixin;

import com.atsukimc.anticrash.runtime.RuntimeExtensionNbt;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//? if <=1.21.11 {
import net.minecraft.server.world.ServerEntityManager;
//?} else if >=26.1 {
//import net.minecraft.world.level.entity.PersistentEntitySectionManager;
//?}
import org.spongepowered.asm.mixin.Mixin;

/** Entities saved because their section is being unloaded keep Vanilla's data only. */
//? if <=1.21.11 {
@Mixin(ServerEntityManager.class)
//?} else if >=26.1 {
//@Mixin(PersistentEntitySectionManager.class)
//?}
public abstract class ServerEntityManagerUnloadMixin {
//? if <=1.21.11 {
    @WrapMethod(method = "unload(J)Z")
//?} else if >=26.1 {
//    @WrapMethod(method = "processChunkUnload(J)Z")
//?}
    private boolean anticrash$scopeUnload(long chunkPos, Operation<Boolean> original) {
        RuntimeExtensionNbt.entityUnload(true);
        try {
            return original.call(chunkPos);
        } finally {
            RuntimeExtensionNbt.entityUnload(false);
        }
    }
}
