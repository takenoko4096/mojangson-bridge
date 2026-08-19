package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValue;

/**
 * json構造を構成するプリミティブ値を表現します。真偽値・数値・文字列・nullのみが該当します。
 * @param <T> ラップされる型。
 */
public abstract class JsonPrimitive<T> extends JsonValue<T> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされる値。
     */
    protected JsonPrimitive(T value) {
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
