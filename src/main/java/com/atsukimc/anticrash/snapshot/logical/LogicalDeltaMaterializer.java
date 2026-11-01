package com.atsukimc.anticrash.snapshot.logical;

import com.atsukimc.anticrash.player.NbtCodec;
import com.atsukimc.anticrash.snapshot.FileFacts;
import com.atsukimc.anticrash.snapshot.JavaIncrementalBackend;
import com.atsukimc.anticrash.snapshot.SnapshotFile;
import com.atsukimc.anticrash.snapshot.SnapshotResult;
import com.atsukimc.anticrash.util.Hashing;
//? if (>=1.18 && <=1.21.11) {
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
//?} else if =1.16.1 || >=26.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//?}
import net.minecraft.nbt.NbtIo;
//? if (>=1.18 && <=1.21.11) {
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;

//?} else if >=26.1 {
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.chunk.storage.RegionFile;
//?} else if =1.16.1 {
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.world.storage.RegionFile;
//import java.io.DataInputStream;
//?}
import java.io.DataOutputStream;
import java.io.IOException;
//? if >=1.18 {
//?} else if =1.16.1 {
//import java.io.OutputStream;
//?}
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Background-only materializer. It consumes immutable NBT bytes and an already committed parent;
 * it never receives a Minecraft server/world/chunk object. Copy-on-write is mandatory because a
 * parent checkpoint may use hard links internally.
 */
public final class LogicalDeltaMaterializer {
    private LogicalDeltaMaterializer() {
    }

    public static SnapshotResult materialize(Path parentWorld, Path destinationWorld,
                                             Map<String, SnapshotFile> parentFiles,
                                             LogicalDelta delta) throws IOException {
        com.atsukimc.anticrash.snapshot.CheckpointWriteGuard.requireMutable(destinationWorld);
        if (parentWorld == null || !Files.isDirectory(parentWorld, LinkOption.NOFOLLOW_LINKS)
                || Files.isSymbolicLink(parentWorld)) {
            throw new IOException("Logical delta parent world is unavailable");
        }
        if (delta == null || !delta.exact) throw new IOException(
                "Logical delta is not exact: " + (delta == null ? "null" : delta.failureReason));
        SnapshotResult view = new JavaIncrementalBackend(() -> 1).snapshot(
                parentWorld, destinationWorld, parentWorld,
                parentFiles == null ? Map.of() : parentFiles, Map.of());
        Set<String> changed = new HashSet<>();
        for (LogicalChunkDelta chunk : delta.chunks) {
            if (chunk == null || !"PRESENT".equals(chunk.state)) continue;
            Path file = applyRegionNbt(destinationWorld, chunk.dimension, chunk.packedPosition,
                    "region", chunk.payloadBase64);
            changed.add(relative(destinationWorld, file));
        }
        Set<String> replacedEntityChunks = new HashSet<>();
        for (LogicalEntityChunkDelta chunk : delta.entityChunks) {
            replacedEntityChunks.add(chunk.key());
//? if (>=1.18 && <=1.21.11) {
            NbtCompound root = NbtCodec.decodeCompound(chunk.payloadBase64);
//?} else if =1.16.1 || >=26.1 {
//            CompoundTag root = NbtCodec.decodeCompound(chunk.payloadBase64);
//?}
            Path file;
//? if >=1.21.5 {
            if (com.atsukimc.anticrash.runtime.VersionNbt.list(root, "Entities", 10).isEmpty()) {
//?} else if <=1.21.4 {
//            if (root.getList("Entities", 10).isEmpty()) {
//?}
                // Vanilla deletes an empty entity chunk instead of storing an empty list. Do the
                // same, and never create an entity region only to record that it is empty.
                file = deleteRegionChunk(destinationWorld, chunk.dimension, chunk.packedPosition,
                        "entities");
            } else {
                file = applyRegionNbt(destinationWorld, chunk.dimension, chunk.packedPosition,
                        "entities", chunk.payloadBase64);
            }
            if (file != null) changed.add(relative(destinationWorld, file));
        }
        for (LogicalTombstone tombstone : delta.tombstones) {
            if (tombstone == null || tombstone.dimension == null
                    || tombstone.key == null || tombstone.packedPosition == null
                    || (!"ENTITY_REMOVED".equals(tombstone.kind)
                    && !"ENTITY_MOVED_FROM".equals(tombstone.kind))) continue;
            // A replaced entity chunk already holds the exact list at the cut. Applying a
            // tombstone to it could delete an entity that moved away and came back.
            if (replacedEntityChunks.contains(ChunkMutationTracker.key(tombstone.dimension,
                    tombstone.packedPosition))) continue;
            Path file = removeEntity(destinationWorld, tombstone.dimension,
                    tombstone.packedPosition, tombstone.key);
            if (file != null) changed.add(relative(destinationWorld, file));
        }
        String metadata = delta.componentStatus.get("LEVEL_DAT_PAYLOAD");
        if (metadata != null) {
            Path level = destinationWorld.resolve("level.dat");
            copyOnWrite(level);
//? if (>=1.18 && <=1.21.11) {
            NbtCompound root = NbtCodec.decodeCompound(metadata);
//?} else if =1.16.1 || >=26.1 {
//            CompoundTag root = NbtCodec.decodeCompound(metadata);
//?}
            Files.createDirectories(level.getParent());
            try (var output = Files.newOutputStream(level)) {
                NbtIo.writeCompressed(root, output);
            }
            changed.add("level.dat");
        }
        for (String path : changed) refresh(view, destinationWorld, path);
        view.backend = "LOGICAL_DELTA";
        view.changedFiles = changed.size();
        view.reusedFiles = Math.max(0, view.files.size() - changed.size());
        view.totalBytes = 0L;
        for (SnapshotFile file : view.files) view.totalBytes += Math.max(0L, file.size);
        return view;
    }

    private static Path applyRegionNbt(Path world, String dimension, long packed,
                                       String directoryName, String encoded) throws IOException {
//? if <=1.21.11 {
        ChunkPos chunk = new ChunkPos(packed);
//?} else if >=26.1 {
//        ChunkPos chunk = ChunkPos.unpack(packed);
//?}
        Path dimensionRoot = dimensionRoot(world, dimension);
        Path directory = dimensionRoot.resolve(directoryName);
        Files.createDirectories(directory);
//? if <=1.21.11 {
        Path file = directory.resolve("r." + (chunk.x >> 5) + "." + (chunk.z >> 5) + ".mca");
//?} else if >=26.1 {
//        Path file = directory.resolve("r." + (chunk.x() >> 5) + "." + (chunk.z() >> 5) + ".mca");
//?}
        copyOnWrite(file);
        // 1.18.2 RegionFile takes (regionFilePath, regionDirectory, dsync).  Passing these in
        // the intuitive directory/file order throws "Expected directory" before any delta is
        // applied, so keep the mapped constructor order explicit here.
//? if (>=1.20.5 && <=1.21.11) {
        try (RegionFile region = new RegionFile(com.atsukimc.anticrash.snapshot.RegionStorageAdapter.KEY, file, directory, false)) {
            try (DataOutputStream output = region.getChunkOutputStream(chunk)) {
                NbtIo.write(NbtCodec.decodeCompound(encoded), output);
//?} else if <=1.20.4 {
//        try (RegionFile region = new RegionFile(file, directory, false)) {
//            try (DataOutputStream output = region.getChunkOutputStream(chunk)) {
//                NbtIo.write(NbtCodec.decodeCompound(encoded), output);
//?} else if >=26.1 {
//        try (RegionFile region = new RegionFile(com.atsukimc.anticrash.snapshot.RegionStorageAdapter.KEY, file, directory, false)) {
//            try (DataOutputStream output = region.getChunkDataOutputStream(chunk)) {
//                NbtIo.writeUnnamedTagWithFallback(NbtCodec.decodeCompound(encoded), output);
//?}
            }
//? if <=1.21.11 {
            region.sync();
//?} else if >=26.1 {
//            region.flush();
//?}
        }
        return file;
    }

    private static Path deleteRegionChunk(Path world, String dimension, long packed,
                                          String directoryName) throws IOException {
//? if <=1.21.11 {
        ChunkPos chunk = new ChunkPos(packed);
//?} else if >=26.1 {
//        ChunkPos chunk = ChunkPos.unpack(packed);
//?}
        Path directory = dimensionRoot(world, dimension).resolve(directoryName);
//? if <=1.21.11 {
        Path file = directory.resolve("r." + (chunk.x >> 5) + "." + (chunk.z >> 5) + ".mca");
//?} else if >=26.1 {
//        Path file = directory.resolve("r." + (chunk.x() >> 5) + "." + (chunk.z() >> 5) + ".mca");
//?}
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(file)) {
            return null;
        }
//? if >=1.20.5 {
        try (RegionFile probe = new RegionFile(com.atsukimc.anticrash.snapshot.RegionStorageAdapter.KEY, file, directory, false)) {
//?} else if <=1.20.4 {
//        try (RegionFile probe = new RegionFile(file, directory, false)) {
//?}
            if (!probe.hasChunk(chunk)) return null;
        }
        copyOnWrite(file);
//? if >=1.20.5 {
        try (RegionFile region = new RegionFile(com.atsukimc.anticrash.snapshot.RegionStorageAdapter.KEY, file, directory, false)) {
//?} else if <=1.20.4 {
//        try (RegionFile region = new RegionFile(file, directory, false)) {
//?}
            // Mapped as method_31740 in Yarn 1.18.2+build.4: RegionFile#delete(ChunkPos).
//? if (>=1.19.2 && <=1.21.11) {
            region.delete(chunk);
            region.sync();
//?} else if <=1.19.1 {
//            region.method_31740(chunk);
//            region.sync();
//?} else if >=26.1 {
//            region.clear(chunk);
//            region.flush();
//?}
        }
        return file;
    }

    private static Path removeEntity(Path world, String dimension, long packed,
                                     String uuidText) throws IOException {
//? if <=1.21.11 {
        ChunkPos chunk = new ChunkPos(packed);
//?} else if >=26.1 {
//        ChunkPos chunk = ChunkPos.unpack(packed);
//?}
        Path directory = dimensionRoot(world, dimension).resolve("entities");
//? if <=1.21.11 {
        Path file = directory.resolve("r." + (chunk.x >> 5) + "." + (chunk.z >> 5) + ".mca");
//?} else if >=26.1 {
//        Path file = directory.resolve("r." + (chunk.x() >> 5) + "." + (chunk.z() >> 5) + ".mca");
//?}
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(file)) {
            return null;
        }
        copyOnWrite(file);
//? if >=1.20.5 {
        try (RegionFile region = new RegionFile(com.atsukimc.anticrash.snapshot.RegionStorageAdapter.KEY, file, directory, false)) {
//?} else if <=1.20.4 {
//        try (RegionFile region = new RegionFile(file, directory, false)) {
//?}
            if (!region.hasChunk(chunk)) return file;
//? if (>=1.18 && <=1.21.11) {
            NbtCompound root;
            try (var input = region.getChunkInputStream(chunk)) {
//?} else if >=26.1 {
//            CompoundTag root;
//            try (var input = region.getChunkDataInputStream(chunk)) {
//?} else if =1.16.1 {
//            CompoundTag root;
//            try (var input = region.getChunkInputStream(chunk)) {
//?}
                if (input == null) return file;
//? if (>=1.20.4 && <=1.21.11) {
                root = NbtIo.readCompound(input, net.minecraft.nbt.NbtSizeTracker.of(16 * 1024 * 1024));
//?} else if <=1.20.1 {
//                root = NbtIo.read(input);
//?} else if >=26.1 {
//                root = NbtIo.read(input, net.minecraft.nbt.NbtAccounter.create(16 * 1024 * 1024));
//?} else if (>=1.20.2 && <=1.20.3) {
//                root = NbtIo.readCompound(input, net.minecraft.nbt.NbtTagSizeTracker.of(16 * 1024 * 1024));
//?}
            }
//? if (>=1.21.5 && <=1.21.11) {
            if (root == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(root, "Entities", 9)) return file;
            NbtList entities = com.atsukimc.anticrash.runtime.VersionNbt.list(root, "Entities", 10);
            NbtList retained = new NbtList();
//?} else if (>=1.18 && <=1.21.4) {
//            if (root == null || !root.contains("Entities", 9)) return file;
//            NbtList entities = root.getList("Entities", 10);
//            NbtList retained = new NbtList();
//?} else if >=26.1 {
//            if (root == null || !com.atsukimc.anticrash.runtime.VersionNbt.contains(root, "Entities", 9)) return file;
//            ListTag entities = com.atsukimc.anticrash.runtime.VersionNbt.list(root, "Entities", 10);
//            ListTag retained = new ListTag();
//?} else if =1.16.1 {
//            if (root == null || !root.contains("Entities", 9)) return file;
//            ListTag entities = root.getList("Entities", 10);
//            ListTag retained = new ListTag();
//?}
            for (int index = 0; index < entities.size(); index++) {
//? if (>=1.21.5 && <=1.21.11) {
                NbtCompound entity = entities.getCompound(index).orElseThrow();
//?} else if (>=1.18 && <=1.21.4) {
//                NbtCompound entity = entities.getCompound(index);
//?} else if >=26.1 {
//                CompoundTag entity = entities.getCompound(index).orElseThrow();
//?} else if =1.16.1 {
//                CompoundTag entity = entities.getCompound(index);
//?}
                if (!matchesUuid(entity, uuidText)) retained.add(entity.copy());
            }
            root.put("Entities", retained);
//? if <=1.21.11 {
            try (DataOutputStream output = region.getChunkOutputStream(chunk)) {
                NbtIo.write(root, output);
//?} else if >=26.1 {
//            try (DataOutputStream output = region.getChunkDataOutputStream(chunk)) {
//                NbtIo.writeUnnamedTagWithFallback(root, output);
//?}
            }
//? if <=1.21.11 {
            region.sync();
//?} else if >=26.1 {
//            region.flush();
//?}
        }
        return file;
    }

//? if (>=1.21.5 && <=1.21.11) {
    private static boolean matchesUuid(NbtCompound entity, String uuidText) {
        if (uuidText == null || entity.get("UUID", net.minecraft.util.Uuids.INT_STREAM_CODEC).isEmpty()) return false;
//?} else if (>=1.18 && <=1.21.4) {
//    private static boolean matchesUuid(NbtCompound entity, String uuidText) {
//        if (uuidText == null || !entity.containsUuid("UUID")) return false;
//?} else if >=26.1 {
//    private static boolean matchesUuid(CompoundTag entity, String uuidText) {
//        if (uuidText == null || entity.read("UUID", net.minecraft.core.UUIDUtil.CODEC).isEmpty()) return false;
//?} else if =1.16.1 {
//    private static boolean matchesUuid(CompoundTag entity, String uuidText) {
//        if (uuidText == null || !entity.containsUuid("UUID")) return false;
//?}
        try {
//? if (>=1.21.5 && <=1.21.11) {
            return uuidText.equalsIgnoreCase(entity.get("UUID", net.minecraft.util.Uuids.INT_STREAM_CODEC).orElseThrow().toString());
//?} else if <=1.21.4 {
//            return uuidText.equalsIgnoreCase(entity.getUuid("UUID").toString());
//?} else if >=26.1 {
//            return uuidText.equalsIgnoreCase(entity.read("UUID", net.minecraft.core.UUIDUtil.CODEC).orElseThrow().toString());
//?}
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static Path dimensionRoot(Path world, String dimension) throws IOException {
        if ("minecraft:overworld".equals(dimension)) return world;
        if ("minecraft:the_nether".equals(dimension)) return world.resolve("DIM-1");
        if ("minecraft:the_end".equals(dimension)) return world.resolve("DIM1");
        Identifier identifier = Identifier.tryParse(dimension);
        if (identifier == null || identifier.getNamespace().contains("..")
                || identifier.getPath().contains("..")) {
            throw new IOException("Unsafe dimension id in logical delta: " + dimension);
        }
        return world.resolve("dimensions").resolve(identifier.getNamespace()).resolve(identifier.getPath());
    }

    private static void copyOnWrite(Path file) throws IOException {
        if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) return;
        if (Files.isSymbolicLink(file) || !Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Unsafe logical delta target: " + file);
        }
        Path temporary = file.resolveSibling(file.getFileName() + ".anticrash-cow");
        Files.deleteIfExists(temporary);
        Files.copy(file, temporary, StandardCopyOption.COPY_ATTRIBUTES);
        try {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException unsupported) {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String relative(Path root, Path file) {
        return root.relativize(file).toString().replace('\\', '/');
    }

    private static void refresh(SnapshotResult result, Path root, String relative) throws IOException {
        Path file = root.resolve(Path.of(relative)).normalize();
        if (!file.startsWith(root.normalize())) {
            throw new IOException("Unsafe logical delta relative path: " + relative);
        }
        FileFacts facts = FileFacts.read(file);
        SnapshotFile replacement = new SnapshotFile(relative, facts.size(), facts.modifiedTime(),
                facts.changeTime(), Hashing.sha256(file));
        for (int index = 0; index < result.files.size(); index++) {
            if (relative.equals(result.files.get(index).path)) {
                result.files.set(index, replacement);
                return;
            }
        }
        result.files.add(replacement);
    }
}
