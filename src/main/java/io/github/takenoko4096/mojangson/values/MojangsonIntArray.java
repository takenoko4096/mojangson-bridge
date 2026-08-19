package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonArrayElementValueSetter;
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
    protected MojangsonInt getZero() {
        return MojangsonInt.valueOf(0);
    }

    @Override
    public MojangsonIntArray deepCopy() {
        return from(listView());
    }

    @Override
    public boolean isEmpty() {
        return value.length == 0;
    }

    @Override
    public int length() {
        return value.length;
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
        boolean successful = false;
        for (int i = 0; i < value.length; i++) {
            if (value[i] != 0) {
                value[i] = 0;
                successful = true;
            }
        }
        return successful;
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

    @Override
    protected void updateView(TypedMojangsonList<MojangsonInt> list) {
        for (int i = 0; i < value.length; i++) {
            list.__internal__().set(i, MojangsonInt.valueOf(value[i]));
        }
    }

    @Override
    public TypedMojangsonList<MojangsonInt> listView() {
        return getView(MojangsonArrayElementValueSetter.INT_ARRAY);
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
