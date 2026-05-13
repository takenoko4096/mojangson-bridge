package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JSONValue;

/**
 * json構造を構成するプリミティブ値を表現します。真偽値・数値・文字列・nullのみが該当します。
 * @param <T> ラップされる型。
 */
public abstract class JSONPrimitive<T> extends JSONValue<T> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされる値。
     */
    protected JSONPrimitive(T value) {
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
