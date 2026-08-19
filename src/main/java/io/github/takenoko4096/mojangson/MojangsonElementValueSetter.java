package io.github.takenoko4096.mojangson;

/**
 * MojangsonArrayにラップされているプリミティブ型の配列上の特定の位置に要素を代入するための関数型インターフェース。
 * @param <A> プリミティブの配列型
 */
@FunctionalInterface
public interface MojangsonElementValueSetter<A> {
    /**
     * array[index] = value を実行します。
     * @param array プリミティブの配列
     * @param index 代入先の添字
     * @param value 代入する値
     */
    void set(A array, int index, Object value);
}
