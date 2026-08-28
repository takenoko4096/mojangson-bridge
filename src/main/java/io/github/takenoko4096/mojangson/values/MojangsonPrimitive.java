package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueType;

/**
 * mojangsonにおけるプリミティブ値を表現します。
 * @param <T> 値 java.lang.String、java.lang.Number, java.util.Map&lt;String, ?&gt; など。
 */
public abstract class MojangsonPrimitive<T> extends MojangsonValue<T> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされる値
     */
    protected MojangsonPrimitive(T value) {
        super(value);
    }

    @Override
    public abstract MojangsonValueType<? extends MojangsonPrimitive<T>> getType();

    /**
     * ラップされている値を取得します。
     * @return ラップされている値
     */
    public T getValue() {
        return value;
    }
}
