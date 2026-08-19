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
    public MojangsonValueType<MojangsonLong> getElementType() {
        return MojangsonValueTypes.LONG;
    }

    @Override
    public MojangsonLongArray copy() {
        return new MojangsonLongArray(toArray());
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
     * @return long
     */
    public long getOrThrow(int index) {
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
    public void set(int index, long value) {
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
        for (final long l : value) {
            if (l != 0) {
                Arrays.fill(value, 0L);
                return true;
            }
        }

        return false;
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
