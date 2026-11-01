//? if >=1.18 {
package com.atsukimc.anticrash.runtime;

//? if (>=1.18 && <=1.21.11) {
import net.minecraft.nbt.NbtCompound;
//?} else if >=26.1 {
//?}
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
//? if (>=1.18 && <=1.21.11) {
//?} else if >=26.1 {
//
//import net.minecraft.nbt.CompoundTag;
//?}

class RuntimeExtensionNbtTest {
    @AfterEach
    void reset() {
        RuntimeExtensionNbt.entitySave(false);
        RuntimeExtensionNbt.entityUnload(false);
        RuntimeExtensionNbt.loadedChunkSave(false);
    }

    @Test
    void extensionIsOneVersionedCompoundBesideVanillaKeys() {
//? if (>=1.18 && <=1.21.11) {
        NbtCompound nbt = new NbtCompound();
//?} else if >=26.1 {
//        CompoundTag nbt = new CompoundTag();
//?}
        nbt.putFloat("progress", 0.5f);
        RuntimeExtensionNbt.write(nbt, extension -> extension.putFloat("Progress", 1.0f));

//? if (>=1.21.5 && <=1.21.11) {
        assertEquals(0.5f, nbt.getFloat("progress").orElseThrow());
        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//?} else if (>=1.18 && <=1.21.4) {
//        assertEquals(0.5f, nbt.getFloat("progress"));
//        NbtCompound extension = RuntimeExtensionNbt.read(nbt);
//?} else if >=26.1 {
//        assertEquals(0.5f, nbt.getFloat("progress").orElseThrow());
//        CompoundTag extension = RuntimeExtensionNbt.read(nbt);
//?}
        assertNotNull(extension);
//? if >=1.21.5 {
        assertEquals(1.0f, extension.getFloat("Progress").orElseThrow());
        assertEquals(RuntimeExtensionNbt.VERSION, nbt.getCompoundOrEmpty(RuntimeExtensionNbt.KEY).getInt("v").orElseThrow());
//?} else if (>=1.18 && <=1.21.4) {
//        assertEquals(1.0f, extension.getFloat("Progress"));
//        assertEquals(RuntimeExtensionNbt.VERSION, nbt.getCompound(RuntimeExtensionNbt.KEY).getInt("v"));
//?}
    }

    @Test
    void nothingIsAddedWhenThereIsNothingToSave() {
//? if (>=1.18 && <=1.21.11) {
        NbtCompound nbt = new NbtCompound();
//?} else if >=26.1 {
//        CompoundTag nbt = new CompoundTag();
//?}
        RuntimeExtensionNbt.write(nbt, extension -> { });
        assertFalse(nbt.contains(RuntimeExtensionNbt.KEY));
        assertNull(RuntimeExtensionNbt.read(nbt));

        RuntimeExtensionNbt.write(nbt, extension -> extension.putInt("TeleportCooldown", 7));
        RuntimeExtensionNbt.write(nbt, extension -> extension.remove("TeleportCooldown"));
        assertFalse(nbt.contains(RuntimeExtensionNbt.KEY));
    }

    @Test
    void aNewerFormatIsIgnoredInsteadOfGuessed() {
//? if (>=1.18 && <=1.21.11) {
        NbtCompound nbt = new NbtCompound();
        NbtCompound future = new NbtCompound();
//?} else if >=26.1 {
//        CompoundTag nbt = new CompoundTag();
//        CompoundTag future = new CompoundTag();
//?}
        future.putInt("v", RuntimeExtensionNbt.VERSION + 1);
        future.putFloat("Progress", 1.0f);
        nbt.put(RuntimeExtensionNbt.KEY, future);
        assertNull(RuntimeExtensionNbt.read(nbt));
    }

    @Test
    void entityStateIsWrittenOnlyForEntitiesThatStayLoaded() {
        assertFalse(RuntimeExtensionNbt.savingLoadedEntities());
        RuntimeExtensionNbt.entitySave(true);
        assertTrue(RuntimeExtensionNbt.savingLoadedEntities());
        // Vanilla unloading a section saves it too; that reload must behave as Vanilla's does.
        RuntimeExtensionNbt.entityUnload(true);
        assertFalse(RuntimeExtensionNbt.savingLoadedEntities());
        RuntimeExtensionNbt.entityUnload(false);
        RuntimeExtensionNbt.entitySave(false);
        assertFalse(RuntimeExtensionNbt.savingLoadedEntities());

        assertFalse(RuntimeExtensionNbt.savingLoadedChunk());
        RuntimeExtensionNbt.loadedChunkSave(true);
        assertTrue(RuntimeExtensionNbt.savingLoadedChunk());
    }

    /**
     * Permanent regression test. Entity#copyFrom (a dimension change) is writeData on the old entity
     * followed by readNbt on the new one. An earlier build wrote the pending portal state on every
     * writeData, so the copy arrived "inside a portal" and went straight back. The extension may only
     * be written inside an entity chunk save of entities that stay loaded.
     */
    @Test
    void portalStateIsNeverWrittenDuringADimensionChangeCopy() {
        // Entity#copyFrom, /data get, spawn eggs, structure blocks: no save scope at all.
        assertFalse(RuntimeExtensionNbt.savingLoadedEntities());
        // A chunk being serialised on this thread does not open the entity scope either.
        RuntimeExtensionNbt.loadedChunkSave(true);
        assertFalse(RuntimeExtensionNbt.savingLoadedEntities());
        // The old entity is removed with CHANGED_DIMENSION; a section unloading at that moment
        // is the only save that can overlap, and it is excluded as well.
        RuntimeExtensionNbt.entitySave(true);
        RuntimeExtensionNbt.entityUnload(true);
        assertFalse(RuntimeExtensionNbt.savingLoadedEntities());
    }

    @Test
    void scopeBelongsToTheSavingThreadOnly() throws Exception {
        RuntimeExtensionNbt.entitySave(true);
        boolean[] other = new boolean[1];
        Thread thread = new Thread(() -> other[0] = RuntimeExtensionNbt.savingLoadedEntities());
        thread.start();
        thread.join();
        assertFalse(other[0]);
        assertTrue(RuntimeExtensionNbt.savingLoadedEntities());
    }

    /** Every mixin that adds to a Vanilla writeData must be gated by one of the two save scopes. */
    @Test
//? if >=1.21.6 {
    void everyWriteDataInjectionIsGatedByASaveScope() throws Exception {
//?} else if (>=1.18 && <=1.21.5) {
//    void everyWriteNbtInjectionIsGatedByASaveScope() throws Exception {
//?}
        String mixins = System.getProperty("anticrash.mixinSources");
        assertNotNull(mixins, "Gradle must supply this target's complete mixin source list");
        assertFalse(mixins.isBlank());
        int injections = 0;
        for (String name : mixins.split(java.io.File.pathSeparator)) {
            java.nio.file.Path file = java.nio.file.Path.of(name);
            String source = com.atsukimc.anticrash.audit.SourceText.active(file);
            int at = 0;
//? if (>=1.21.6 && <=1.21.11) {
            while ((at = source.indexOf("@Inject(method = \"writeData\"", at)) >= 0) {
//?} else if (>=1.18 && <=1.21.5) {
//            while ((at = source.indexOf("@Inject(method = \"writeNbt\"", at)) >= 0) {
//?} else if >=26.1 {
//            var matcher = java.util.regex.Pattern.compile(
//                    "@Inject\\(method = \\\"(?:saveAdditional|addAdditionalSaveData|saveWithoutId)\\\"")
//                    .matcher(source);
//            while (matcher.find(at)) {
//                at = matcher.start();
//?}
                injections++;
                int body = source.indexOf('{', at);
                String head = source.substring(body, Math.min(source.length(), body + 200));
                assertTrue(head.contains("savingLoadedChunk()) return;")
                                || head.contains("savingLoadedEntities()) return;"),
//? if >=1.21.6 {
                        file.getFileName() + " writes to writeData outside a save scope");
//?} else if (>=1.18 && <=1.21.5) {
//                        file.getFileName() + " writes to writeNbt outside a save scope");
//?}
                at = body;
            }
        }
//? if >=1.21.6 {
        // Sculk listener NBT is owned by Vanilla since 1.19, leaving four extension hooks.
        assertTrue(injections >= 4, "writeData injections found: " + injections);
//?} else if (>=1.19 && <=1.21.5) {
//        // Sculk listener NBT is owned by Vanilla since 1.19, leaving four extension hooks.
//        assertTrue(injections >= 4, "writeNbt injections found: " + injections);
//?} else if (>=1.18 && <=1.18.2) {
//        assertTrue(injections >= 5, "writeNbt injections found: " + injections);
//?}
    }
}
//?}
