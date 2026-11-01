//? if >=1.18 {
package com.atsukimc.anticrash.player;

//? if (>=1.18 && <=1.21.11) {
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
//?} else if >=26.1 {
//?}
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
//? if (>=1.18 && <=1.21.11) {
//?} else if >=26.1 {
//import net.minecraft.nbt.ListTag;
//import net.minecraft.nbt.StringTag;
//import net.minecraft.nbt.Tag;
//?}

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NbtCodecTest {
    @Test void emptyRootRoundTripsAndTrailingBytesAreRejected() throws Exception {
//? if (>=1.18 && <=1.21.11) {
        NbtElement empty = NbtCodec.decode("AA==");
        assertEquals(0, empty.getType());
//?} else if >=26.1 {
//        Tag empty = NbtCodec.decode("AA==");
//        assertEquals(0, empty.getId());
//?}
        assertEquals("AA==", NbtCodec.encode(empty));
        assertThrows(IOException.class, () -> NbtCodec.decode("AAA="));
    }

    @Test
    void roundTripsAListRootUsedByEnderChestSnapshots() throws Exception {
//? if (>=1.18 && <=1.21.11) {
        NbtList list = new NbtList();
        list.add(NbtString.of("slot"));
//?} else if >=26.1 {
//        ListTag list = new ListTag();
//        list.add(StringTag.valueOf("slot"));
//?}

//? if (>=1.18 && <=1.21.11) {
        NbtList decoded = assertInstanceOf(NbtList.class,
//?} else if >=26.1 {
//        ListTag decoded = assertInstanceOf(ListTag.class,
//?}
                NbtCodec.decode(NbtCodec.encode(list)));

//? if >=1.21.5 {
        assertEquals("slot", decoded.getString(0).orElseThrow());
//?} else if (>=1.18 && <=1.21.4) {
//        assertEquals("slot", decoded.getString(0));
//?}
    }

    @Test
    void hostileArrayLengthIsRejectedBeforeAHeapSizedAllocation() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(bytes)) {
//? if (>=1.18 && <=1.21.11) {
            output.writeByte(NbtElement.BYTE_ARRAY_TYPE);
//?} else if >=26.1 {
//            output.writeByte(Tag.TAG_BYTE_ARRAY);
//?}
            output.writeUTF("");
            output.writeInt(Integer.MAX_VALUE);
        }
        String encoded = Base64.getEncoder().encodeToString(bytes.toByteArray());

        assertThrows(IOException.class, () -> NbtCodec.decode(encoded));
    }
}
//?}
