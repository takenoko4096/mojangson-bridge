package io.github.takenoko4096.nbt;

import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.values.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * nbtバイナリをデコードするクラス。
 */
public final class NbtDecoder {
    private static final byte TAG_END = 0;
    private static final byte TAG_BYTE = 1;
    private static final byte TAG_SHORT = 2;
    private static final byte TAG_INT = 3;
    private static final byte TAG_LONG = 4;
    private static final byte TAG_FLOAT = 5;
    private static final byte TAG_DOUBLE = 6;
    private static final byte TAG_BYTE_ARRAY = 7;
    private static final byte TAG_STRING = 8;
    private static final byte TAG_LIST = 9;
    private static final byte TAG_COMPOUND = 10;
    private static final byte TAG_INT_ARRAY = 11;
    private static final byte TAG_LONG_ARRAY = 12;

    private final DataInputStream stream;

    private NbtDecoder(DataInputStream stream) {
        this.stream = stream;
    }

    private MojangsonCompound read() throws IOException {
        final byte type = stream.readByte();
        if (type != TAG_COMPOUND) throw new IOException("ルートタグがコンパウンドではありません: " + type);
        string();
        return compound();
    }

    private MojangsonValue<?> value(byte type) throws IOException {
        return switch (type) {
            case TAG_BYTE -> MojangsonByte.valueOf(stream.readByte());
            case TAG_SHORT -> MojangsonShort.valueOf(stream.readShort());
            case TAG_INT -> MojangsonInt.valueOf(stream.readInt());
            case TAG_LONG -> MojangsonLong.valueOf(stream.readLong());
            case TAG_FLOAT -> MojangsonFloat.valueOf(stream.readFloat());
            case TAG_DOUBLE -> MojangsonDouble.valueOf(stream.readDouble());
            case TAG_BYTE_ARRAY -> byteArray();
            case TAG_STRING -> string();
            case TAG_LIST -> list();
            case TAG_COMPOUND -> compound();
            case TAG_INT_ARRAY -> intArray();
            case TAG_LONG_ARRAY -> longArray();
            default -> throw new IOException("Unknown tag type: " + type);
        };
    }

    private MojangsonByteArray byteArray() throws IOException {
        final int length = stream.readInt();
        final byte[] array = new byte[length];
        stream.readFully(array);
        return new MojangsonByteArray(array);
    }

    private MojangsonString string() throws IOException {
        final short length = stream.readShort();
        final byte[] bytes = new byte[length];
        stream.readFully(bytes);
        final String string = new String(bytes, StandardCharsets.UTF_8);
        return MojangsonString.valueOf(string);
    }

    private MojangsonList list() throws IOException {
        final byte elementType = stream.readByte();
        final int length = stream.readInt();
        final List<MojangsonValue<?>> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            list.add(value(elementType));
        }

        return new MojangsonList(list);
    }

    private MojangsonCompound compound() throws IOException {
        final MojangsonCompound compound = new MojangsonCompound();

        while (true) {
            final byte type = stream.readByte();
            if (type == TAG_END) break;
            final String key = string().getValue();
            final Object value = value(type);
            compound.set(key, value);
        }

        return compound;
    }

    private MojangsonIntArray intArray() throws IOException {
        final int length = stream.readInt();
        final int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = stream.readInt();
        }
        return new MojangsonIntArray(array);
    }

    private MojangsonLongArray longArray() throws IOException {
        final int length = stream.readInt();
        final long[] array = new long[length];
        for (int i = 0; i < length; i++) {
            array[i] = stream.readLong();
        }
        return new MojangsonLongArray(array);
    }

    /**
     * 引数に渡されたファイルをGZip圧縮された形式のバイナリファイルであるものとしてデコードします。
     * @param file 読み取るファイル
     * @return デコード結果のコンパウンド
     */
    public static MojangsonCompound decompress(File file) throws NbtReadException {
        try (final DataInputStream input = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))))) {
            final var reader = new NbtDecoder(input);
            return reader.read();
        }
        catch (IOException e) {
            throw new NbtReadException(e);
        }
    }

    /**
     * 引数に渡されたファイルを圧縮されていない形式のバイナリファイルであるものとしてデコードします。
     * @param file 読み取るファイル
     * @return デコード結果のコンパウンド
     */
    public static MojangsonCompound raw(File file) throws NbtReadException {
        try (final DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            final var reader = new NbtDecoder(stream);
            return reader.read();
        }
        catch (IOException e) {
            throw new NbtReadException(e);
        }
    }

    /**
     * GZip圧縮された形式のファイルであるかどうかを返します。
     * @param file 読み取るファイル
     * @return GZip圧縮されているならば true バイト列が短すぎる場合例外を投げます。
     * @throws NbtReadException デコードに失敗した場合、またはバイト列が短すぎて圧縮形式を判別できない場合
     */
    public static boolean isCompressed(File file) throws NbtReadException {
        try {
            final byte[] bytes = Files.readAllBytes(file.toPath());
            return isCompressed(bytes);
        }
        catch (IOException e) {
            throw new NbtReadException(e);
        }
    }

    /**
     * 引数に渡されたバイト列をGZip圧縮された形式であるものとしてデコードします。
     * @param bytes 解析するバイト列
     * @return デコード結果のコンパウンド
     */
    public static MojangsonCompound decompress(byte[] bytes) {
        try (final DataInputStream stream = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)))) {
            final var reader = new NbtDecoder(stream);
            return reader.read();
        }
        catch (IOException e) {
            throw new NbtReadException(e);
        }
    }

    /**
     * 引数に渡されたバイト列を圧縮されていない形式であるものとしてデコードします。
     * @param bytes 解析するバイト列
     * @return デコード結果のコンパウンド
     */
    public static MojangsonCompound raw(byte[] bytes) {
        try (final DataInputStream stream = new DataInputStream((new ByteArrayInputStream(bytes)))) {
            final var reader = new NbtDecoder(stream);
            return reader.read();
        }
        catch (IOException e) {
            throw new NbtReadException(e);
        }
    }

    /**
     * GZip圧縮された形式のバイト列であるかどうかを返します。
     * @param bytes 解析するバイト列
     * @return GZip圧縮されているならば true バイト列が短すぎる場合例外を投げます。
     * @throws NbtReadException デコードに失敗した場合、またはバイト列が短すぎて圧縮形式を判別できない場合
     */
    public static boolean isCompressed(byte[] bytes) throws NbtReadException {
        if (bytes.length <= 1) {
            throw new NbtReadException("バイト列のサイズが短すぎるため判断できません: " + bytes.length);
        }
        else {
            return (bytes[0] & 0xFF) == 0x1F && (bytes[1] & 0xFF) == 0x8B;
        }
    }
}
