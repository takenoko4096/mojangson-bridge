package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.MojangsonByte;
import io.github.takenoko4096.mojangson.values.MojangsonInt;
import io.github.takenoko4096.mojangson.values.MojangsonLong;

/**
 * MojangsonArrayにラップされているプリミティブ型の配列上の特定の位置に要素を代入するための関数型インターフェース。
 * @param <A> プリミティブの配列型
 */
@FunctionalInterface
public interface MojangsonArrayElementValueSetter<A> {
    /**
     * array[index] = value を実行します。
     * @param array プリミティブの配列
     * @param index 代入先の添字
     * @param value 代入する値
     */
    void set(A array, int index, Object value);

    MojangsonArrayElementValueSetter<byte[]> BYTE_ARRAY = (array, index, value) -> {
        if (value instanceof MojangsonByte mojangsonByte) {
            array[index] = mojangsonByte.getValue();
        }
        else if (value instanceof Byte byteValue) {
            array[index] = byteValue;
        }
        else {
            throw new IllegalArgumentException("配列に代入できない値です: " + value);
        }
    };

    MojangsonArrayElementValueSetter<int[]> INT_ARRAY = (array, index, value) -> {
        if (value instanceof MojangsonInt mojangsonInt) {
            array[index] = mojangsonInt.getValue();
        }
        else if (value instanceof Integer intValue) {
            array[index] = intValue;
        }
        else {
            throw new IllegalArgumentException("配列に代入できない値です: " + value);
        }
    };

    MojangsonArrayElementValueSetter<long[]> LONG_ARRAY = (array, index, value) -> {
        if (value instanceof MojangsonLong mojangsonLong) {
            array[index] = mojangsonLong.getValue();
        }
        else if (value instanceof Long longValue) {
            array[index] = longValue;
        }
        else {
            throw new IllegalArgumentException("配列に代入できない値です: " + value);
        }
    };
}
