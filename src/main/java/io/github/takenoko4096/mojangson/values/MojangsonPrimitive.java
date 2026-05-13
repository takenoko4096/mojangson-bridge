package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import org.jspecify.annotations.NullMarked;

/**
 * mojangsonにおけるプリミティブ値を表現します。
 * @param <T> Javaにおける値。String、Number, Mapなど。
 */
public abstract class MojangsonPrimitive<T> extends MojangsonValue<T> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされる値。
     */
    protected MojangsonPrimitive(T value) {
        super(value);
    }

    /**
     * ラップされている値を取得します。
     * @return ラップされている値。
     */
    public T getValue() {
        return value;
    }
}
