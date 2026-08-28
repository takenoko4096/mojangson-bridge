package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * mojangsonにおける byte[] を表現します。
 */
public class MojangsonByteArray extends MojangsonArray<byte[], MojangsonByte> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされるプリミティブ配列。
     */
    public MojangsonByteArray(byte[] value) {
        super(value);
    }

    @Override
    public MojangsonValueType<MojangsonByteArray> getType() {
        return MojangsonValueTypes.BYTE_ARRAY;
    }

    @Override
    public MojangsonValueType<MojangsonByte> getElementType() {
        return MojangsonValueTypes.BYTE;
    }

    @Override
    public MojangsonByteArray copy() {
        return new MojangsonByteArray(toArray());
    }

    @Override
    public boolean isEmpty() {
        return value.length == 0;
    }

    @Override
    public int length() {
        return value.length;
    }

    /**
     * 添字に対応する位置の値を返します。
     * @param index 添字
     * @return byte
     */
    public byte getOrThrow(int index) {
        if (index >= this.value.length) {
            throw new IllegalArgumentException("不正な添字です: " + index);
        }
        return value[index];
    }

    /**
     * 添字に対応する位置に値を代入します。
     * @param index 添字
     * @param value 値
     */
    public void set(int index, byte value) {
        if (index >= this.value.length) return;
        this.value[index] = value;
    }

    @Override
    public boolean delete(int index) {
        if (index >= value.length) return false;
        final boolean successful = value[index] != 0;
        value[index] = 0;
        return successful;
    }

    @Override
    public boolean clear() {
        for (final byte b : value) {
            if (b != 0) {
                Arrays.fill(value, (byte) 0);
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<MojangsonByte> iterator() {
        final List<MojangsonByte> bytes = new ArrayList<>();
        for (final byte b : value) {
            bytes.add(MojangsonByte.valueOf(b));
        }
        return bytes.iterator();
    }

    @Override
    public String toString() {
        return "byte" + Arrays.toString(value);
    }

    @Override
    public byte[] toArray() {
        return Arrays.copyOf(value, value.length);
    }

    /**
     * MojangsonList から MojangsonByteArray への変換を試みます。
     * @param list MojangsonByte のみを要素に持つリスト。
     * @return MojangsonByteArray
     */
    public static MojangsonByteArray from(TypedMojangsonList<MojangsonByte> list) {
        final byte[] bytes = new byte[list.length()];

        for (int i = 0; i < list.length(); i++) {
            bytes[i] = list.getOrThrow(i).byteValue();
        }

        return new MojangsonByteArray(bytes);
    }
}
