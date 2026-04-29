package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.*;
import io.github.takenoko4096.json.values.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * json構造を構成するすべての型のスーパークラス。
 * @param <T> Javaにおける値。String、Number, Mapなど。
 */
@NullMarked
public abstract class JSONValue<T> {
    /**
     * ラップされた値。必要に応じてサブクラスで編集される可能性があります。
     */
    protected final T value;

    protected JSONValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONValue<?> jsonValue = (JSONValue<?>) o;
        return Objects.equals(value, jsonValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * 値の型を取得します。
     * @return この値の型を表現するオブジェクト。
     */
    public abstract JSONValueType<?> getType();

    /**
     * 渡されたJavaの値に対応するjson構造を返します。
     * @param value nullを含む任意のオブジェクト。
     * @return 引数をjson構造に変換したオブジェクト。JSONValueが渡された場合、引数をそのまま返します。
     */
    public static JSONValue<?> valueOf(@Nullable Object value) {
        return switch (value) {
            case null -> JSONNull.NULL;
            case Boolean b -> JSONBoolean.valueOf((boolean) b);
            case Number n -> JSONNumber.valueOf(n);
            case String s -> JSONString.valueOf(s);
            case Map<?, ?> m -> JSONObject.valueOf(m);
            case Iterable<?> i -> JSONArray.valueOf(i);
            case JSONValue<?> j -> j;
            default -> JSONString.valueOf(value.getClass().getName());
        };
    }
}
