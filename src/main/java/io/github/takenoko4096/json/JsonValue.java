package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.*;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * json構造を構成する「値」を表します。
 * 0, 1, true, "Hello", [null, 0, false], {"key": "value"} 等はすべてこのクラスまたはそのサブクラスによって表現されます。
 * @param <T> ラップされる型 java.lang.Boolean, java.lang.String など。
 */
public abstract class JsonValue<T> {
    /**
     * ラップされた値 必要に応じてサブクラスで編集される可能性があります。
     */
    protected final T value;

    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされる値
     */
    protected JsonValue(T value) {
        this.value = value;
    }

    /**
     * この値の文字列表現を返します。出力はラップされた型が実装する toString() に依存し、jsonフォーマットへの整形は行われません。
     * jsonフォーマットに整形する場合は JsonSerializer を使用してください。
     * @return ラップされたクラスの toString() の実装に依存
     * @see JsonSerializer
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * この値と引数に渡された値が等価であるかを調べます。出力はラップされた型が実装する equals(Object) に依存します。
     * JsonValue のインスタンスでない値との比較は常に false を返します。
     * @param o 比較対象の値。
     * @return ラップされたクラスの equals() の実装に依存
     */
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;

        if (o instanceof JsonValue<?> jsonValue) {
            return Objects.equals(value, jsonValue.value);
        }
        else return false;
    }

    /**
     * この値のハッシュコードを返します。
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), value);
    }

    /**
     * 値の型を取得します。
     * @return この値の型を表現するオブジェクト
     */
    public abstract JsonValueType<?> getType();

    /**
     * 渡された値に対応するjson表現を返します。
     * @param value null を含む任意のオブジェクト
     * @return json構造に変換されたオブジェクト JsonValue が渡された場合、引数をそのまま返します。
     */
    public static JsonValue<?> valueOf(@Nullable Object value) {
        return switch (value) {
            case JsonValue<?> v -> v;
            case null -> JsonNull.NULL;
            case Boolean b -> JsonBoolean.valueOf((boolean) b);
            case Number n -> JsonNumber.valueOf(n);
            case String s -> JsonString.valueOf(s);
            case Map<?, ?> m -> JsonObject.valueOf(m);
            case Object[] a -> JsonArray.valueOf(Arrays.asList(a));
            case Iterable<?> i -> JsonArray.valueOf(i);
            default -> throw new IllegalArgumentException("json値に変換できない型です: " + value.getClass().getName());
        };
    }
}
