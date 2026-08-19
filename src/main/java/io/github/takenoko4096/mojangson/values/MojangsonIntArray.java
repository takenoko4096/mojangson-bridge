package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * mojangsonにおける int[] を表現します。
 */
public class MojangsonIntArray extends MojangsonArray<int[], MojangsonInt> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされるプリミティブ配列。
     */
    public MojangsonIntArray(int[] value) {
        super(value);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.INT_ARRAY;
    }

    @Override
    public MojangsonValueType<MojangsonInt> getElementType() {
        return MojangsonValueTypes.INT;
    }

    @Override
    public MojangsonIntArray copy() {
        return new MojangsonIntArray(toArray());
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
     * @return int
     */
    public int getOrThrow(int index) {
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
    public void set(int index, int value) {
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
        for (int i : value) {
            if (i != 0) {
                Arrays.fill(value, 0);
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<MojangsonInt> iterator() {

        final List<MojangsonInt> bytes = new ArrayList<>();
        for (final int intValue : value) {
            bytes.add(MojangsonInt.valueOf(intValue));
        }
        return bytes.iterator();
    }

    @Override
    public String toString() {
        return "int" + Arrays.toString(value);
    }

    @Override
    public int[] toArray() {
        return Arrays.copyOf(value, value.length);
    }

    /**
     * MojangsonList から MojangsonIntArray への変換を試みます。
     * @param list MojangsonInt のみを要素に持つリスト。
     * @return MojangsonIntArray
     */
    public static MojangsonIntArray from(TypedMojangsonList<MojangsonInt> list) {
        final int[] ints = new int[list.length()];

        for (int i = 0; i < list.length(); i++) {
            ints[i] = list.getOrThrow(i).intValue();
        }

        return new MojangsonIntArray(ints);
    }
}
