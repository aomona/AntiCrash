package com.atsukimc.anticrash.player;

//? if (>=1.18.1 && <=1.21.11) {
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
//?} else if >=26.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.EndTag;
//import net.minecraft.nbt.NbtAccounter;
//?} else if =1.16.1 {
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.EndTag;
//?} else if =1.18 {
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtNull;
//?}
import net.minecraft.nbt.NbtIo;
//? if (>=1.20.4 && <=1.21.11) {
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtTypes;

//?} else if (>=1.18 && <=1.20.3) {
//import net.minecraft.nbt.NbtTagSizeTracker;
//import net.minecraft.nbt.NbtTypes;
//
//?} else if >=26.1 {
//import net.minecraft.nbt.Tag;
//import net.minecraft.nbt.TagTypes;
//?} else if =1.16.1 {
//import net.minecraft.nbt.PositionTracker;
//import net.minecraft.nbt.Tag;
//import net.minecraft.nbt.TagReaders;
//?}
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;

public final class NbtCodec {
    private static final int MAX_NBT_BYTES = 16 * 1024 * 1024;
    private static final int MAX_ENCODED_CHARS = ((MAX_NBT_BYTES + 2) / 3) * 4;

    private NbtCodec() {
    }

//? if (>=1.18 && <=1.21.11) {
    public static String encode(NbtElement element) throws IOException {
//?} else if =1.16.1 || >=26.1 {
//    public static String encode(Tag element) throws IOException {
//?}
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(bytes)) {
//? if <=1.21.11 {
            NbtIo.write(element, output);
//?} else if >=26.1 {
//            NbtIo.writeUnnamedTagWithFallback(element, output);
//?}
        }
        if (bytes.size() > MAX_NBT_BYTES) throw new IOException("NBT snapshot exceeds 16 MiB");
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }

//? if (>=1.18 && <=1.21.11) {
    public static NbtElement decode(String encoded) throws IOException {
//?} else if =1.16.1 || >=26.1 {
//    public static Tag decode(String encoded) throws IOException {
//?}
        if (encoded == null || encoded.isBlank()) return null;
        if (encoded.length() > MAX_ENCODED_CHARS) throw new IOException("Encoded NBT exceeds 16 MiB");
        final byte[] bytes;
        try { bytes = Base64.getDecoder().decode(encoded); }
        catch (IllegalArgumentException exception) { throw new IOException("Encoded NBT is not valid Base64", exception); }
        if (bytes.length > MAX_NBT_BYTES) throw new IOException("Decoded NBT exceeds 16 MiB");
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes))) {
            int type = input.readUnsignedByte();
//? if (>=1.18.1 && <=1.21.11) {
            if (type == NbtElement.END_TYPE) {
//?} else if >=26.1 {
//            if (type == Tag.TAG_END) {
//?} else if =1.16.1 {
//            if (type == Tag.END_TYPE) {
//?} else if =1.18 {
//            if (type == NbtElement.NULL_TYPE) {
//?}
                if (input.available() != 0) throw new IOException("Encoded NBT contains trailing bytes");
//? if (>=1.18.1 && <=1.21.11) {
                return NbtEnd.INSTANCE;
//?} else if =1.16.1 || >=26.1 {
//                return EndTag.INSTANCE;
//?} else if =1.18 {
//                return NbtNull.INSTANCE;
//?}
            }
            input.readUTF(); // root name; encode() writes the empty name used by Vanilla NbtIo
//? if (>=1.18 && <=1.21.11) {
            final NbtElement element;
//?} else if =1.16.1 || >=26.1 {
//            final Tag element;
//?}
            try {
//? if (>=1.20.4 && <=1.21.11) {
                element = NbtTypes.byId(type).read(input, NbtSizeTracker.of(MAX_NBT_BYTES));
//?} else if (>=1.18 && <=1.20.1) {
//                element = NbtTypes.byId(type).read(input, 0, new NbtTagSizeTracker(MAX_NBT_BYTES));
//?} else if >=26.1 {
//                element = TagTypes.getType(type).load(input, NbtAccounter.create(MAX_NBT_BYTES));
//?} else if (>=1.20.2 && <=1.20.3) {
//                element = NbtTypes.byId(type).read(input, NbtTagSizeTracker.of(MAX_NBT_BYTES));
//?} else if =1.16.1 {
//                element = TagReaders.of(type).read(input, 0, new PositionTracker(MAX_NBT_BYTES));
//?}
            } catch (RuntimeException exception) {
                throw new IOException("NBT exceeds its allocation or structural limit", exception);
            }
            if (input.available() != 0) throw new IOException("Encoded NBT contains trailing bytes");
            return element;
        }
    }

//? if (>=1.18 && <=1.21.11) {
    public static NbtCompound decodeCompound(String encoded) throws IOException {
        NbtElement element = decode(encoded);
        if (!(element instanceof NbtCompound compound)) throw new IOException("Encoded NBT is not a compound");
//?} else if =1.16.1 || >=26.1 {
//    public static CompoundTag decodeCompound(String encoded) throws IOException {
//        Tag element = decode(encoded);
//        if (!(element instanceof CompoundTag compound)) throw new IOException("Encoded NBT is not a compound");
//?}
        return compound;
    }
}
