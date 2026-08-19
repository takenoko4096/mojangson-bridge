package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * mojangsonにおける long[] を表現します
 */
public class MojangsonLongArray extends MojangsonArray<long[], MojangsonLong> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされるプリミティブ配列。
     */
    public MojangsonLongArray(long[] value) {
        super(value);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.LONG_ARRAY;
    }

    @Override
    protected MojangsonValueType<MojangsonLong> getElementType() {
        return MojangsonValueTypes.LONG;
    }

    @Override
    protected MojangsonLong getZero() {
        return MojangsonLong.valueOf(0L);
    }

    @Override
    public MojangsonLongArray deepCopy() {
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
    public Iterator<MojangsonLong> iterator() {
        final List<MojangsonLong> longs = new ArrayList<>();
        for (final long longValue : value) {
            longs.add(MojangsonLong.valueOf(longValue));
        }
        return longs.iterator();
    }

    @Override
    public String toString() {
        return "long" + Arrays.toString(value);
    }

    @Override
    public long[] toArray() {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public TypedMojangsonList<MojangsonLong> listView() {
        return getView((arr, ind, val) -> arr[ind] = (long) val);
    }

    /**
     * MojangsonList から MojangsonLongArray への変換を試みます。
     * @param list MojangsonLong のみを要素に持つリスト。
     * @return MojangsonLongArray
     */
    public static MojangsonLongArray from(TypedMojangsonList<MojangsonLong> list) {
        final long[] longs = new long[list.length()];

        for (int i = 0; i < list.length(); i++) {
            longs[i] = list.getOrThrow(i).longValue();
        }

        return new MojangsonLongArray(longs);
    }
}
