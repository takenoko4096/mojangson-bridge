package com.gmail.takenokoii78.nbt;

import com.gmail.takenokoii78.mojangson.MojangsonValue;
import com.gmail.takenokoii78.mojangson.MojangsonValueType;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.*;
import org.jspecify.annotations.NullMarked;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

@NullMarked
public final class NbtWriter {
    public static final byte TAG_END = 0;
    public static final byte TAG_BYTE = 1;
    public static final byte TAG_SHORT = 2;
    public static final byte TAG_INT = 3;
    public static final byte TAG_LONG = 4;
    public static final byte TAG_FLOAT = 5;
    public static final byte TAG_DOUBLE = 6;
    public static final byte TAG_BYTE_ARRAY = 7;
    public static final byte TAG_STRING = 8;
    public static final byte TAG_LIST = 9;
    public static final byte TAG_COMPOUND = 10;
    public static final byte TAG_INT_ARRAY = 11;
    public static final byte TAG_LONG_ARRAY = 12;

    private final DataOutputStream stream;

    private final String name;

    private final MojangsonCompound compound;

    private NbtWriter(DataOutputStream stream, String name, MojangsonCompound compound) {
        this.stream = stream;
        this.name = name;
        this.compound = compound;
    }

    private void write() throws IOException {
        stream.writeByte(TAG_COMPOUND);
        string(MojangsonString.valueOf(name));
        compound(compound);
    }

    private byte typeId(MojangsonValueType<?> type) {
        if (type == MojangsonValueTypes.BYTE) return TAG_BYTE;
        else if (type == MojangsonValueTypes.SHORT) return TAG_SHORT;
        else if (type == MojangsonValueTypes.INT) return TAG_INT;
        else if (type == MojangsonValueTypes.LONG) return TAG_LONG;
        else if (type == MojangsonValueTypes.FLOAT) return TAG_FLOAT;
        else if (type == MojangsonValueTypes.DOUBLE) return TAG_DOUBLE;
        else if (type == MojangsonValueTypes.BYTE_ARRAY) return TAG_BYTE_ARRAY;
        else if (type == MojangsonValueTypes.STRING) return TAG_STRING;
        else if (type == MojangsonValueTypes.LIST) return TAG_LIST;
        else if (type == MojangsonValueTypes.COMPOUND) return TAG_COMPOUND;
        else if (type == MojangsonValueTypes.INT_ARRAY) return TAG_INT_ARRAY;
        else if (type == MojangsonValueTypes.LONG_ARRAY) return TAG_LONG_ARRAY;
        else throw new NbtWriteException("不明な型です: " + type.getClass().getName());
    }

    private void value(MojangsonValue<?> value) throws IOException {
        switch (value) {
            case MojangsonByte v -> stream.writeByte(v.byteValue());
            case MojangsonShort v -> stream.writeShort(v.shortValue());
            case MojangsonInt v -> stream.writeInt(v.intValue());
            case MojangsonLong v -> stream.writeLong(v.longValue());
            case MojangsonFloat v -> stream.writeFloat(v.floatValue());
            case MojangsonDouble v -> stream.writeDouble(v.doubleValue());
            case MojangsonByteArray v -> byteArray(v);
            case MojangsonString v -> string(v);
            case MojangsonList v -> list(v);
            case MojangsonCompound v -> compound(v);
            case MojangsonIntArray v -> intArray(v);
            case MojangsonLongArray v -> longArray(v);
            default -> throw new NbtWriteException("不明なMojangsonクラスです: " + value.getClass().getName());
        }
    }

    private void byteArray(MojangsonByteArray array) throws IOException {
        final byte[] bytes = array.toArray();
        stream.writeInt(bytes.length);
        stream.write(bytes);
    }

    private void string(MojangsonString string) throws IOException {
        final byte[] bytes = string.getValue().getBytes(StandardCharsets.UTF_8);
        stream.writeShort(bytes.length);
        stream.write(bytes);
    }

    private void list(MojangsonList list) throws IOException {
        if (list.isEmpty()) {
            stream.writeByte(TAG_END);
            stream.writeInt(0);
        }
        else {
            final MojangsonValueType<?> type = list.getTypeAt(0);
            final TypedMojangsonList<?> typed = list.typed(type);
            stream.writeByte(typeId(type));
            stream.writeInt(typed.length());
            for (final var element: typed) {
                value(element);
            }
        }
    }

    private void compound(MojangsonCompound compound) throws IOException {
        for (final String key : compound.keys()) {
            final MojangsonValueType<?> type = compound.getTypeOf(key);
            stream.writeByte(typeId(type));
            string(MojangsonString.valueOf(key));
            value(compound.get(key, type));
        }
        stream.writeByte(TAG_END);
    }

    private void intArray(MojangsonIntArray array) throws IOException {
        final int[] ints = array.toArray();
        stream.writeInt(ints.length);
        for (int i : ints) {
            stream.writeInt(i);
        }
    }

    private void longArray(MojangsonLongArray array) throws IOException {
        final long[] ints = array.toArray();
        stream.writeInt(ints.length);
        for (long l : ints) {
            stream.writeLong(l);
        }
    }

    public static void compress(File file, MojangsonCompound compound) {
        try (final DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file))))) {
            final var writer = new NbtWriter(stream, new String(new char[0]), compound);
            writer.write();
        }
        catch (IOException e) {
            throw new NbtWriteException(e);
        }
    }

    public static void raw(File file, MojangsonCompound compound) {
        try (final DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            final var writer = new NbtWriter(stream, new String(new char[0]), compound);
            writer.write();
        }
        catch (IOException e) {
            throw new NbtWriteException(e);
        }
    }

    public static byte[] compress(MojangsonCompound compound) {
        final ByteArrayOutputStream stream1 = new ByteArrayOutputStream();

        try ( final DataOutputStream stream2 = new DataOutputStream(new GZIPOutputStream(stream1))) {
            final var writer = new NbtWriter(stream2, new String(new char[0]), compound);
            writer.write();
        }
        catch (IOException e) {
            throw new NbtWriteException(e);
        }

        return stream1.toByteArray();
    }

    public static byte[] raw(MojangsonCompound compound) {
        final ByteArrayOutputStream stream1 = new ByteArrayOutputStream();

        try (final DataOutputStream stream2 = new DataOutputStream(stream1)) {
            final var writer = new NbtWriter(stream2, new String(new char[0]), compound);
            writer.write();
        }
        catch (IOException e) {
            throw new NbtWriteException(e);
        }

        return stream1.toByteArray();
    }
}
