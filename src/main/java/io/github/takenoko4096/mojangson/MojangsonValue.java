package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * mojangson構造を構成する「値」を表します。
 * 0, 1, 1b, Hello, [-1s, 0, 0b], {key: value} 等はすべてこのクラスまたはそのサブクラスによって表現されます。
 * @param <T> ラップされる型。java.lang.Boolean, java.lang.Stringなど。
 */
@NullMarked
public abstract class MojangsonValue<T> {
    /**
     * ラップされた値。必要に応じてサブクラスで編集される可能性があります。
     */
    protected final T value;

    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされる値。
     */
    protected MojangsonValue(T value) {
        this.value = value;
    }

    /**
     * この値と引数に渡された値が等価であるかを調べます。出力はラップされた型が実装するequals(Object)に依存します。
     * MojangsonValueのインスタンスでない値との比較は常にfalseを返します。
     * @param o 比較対象の値。
     * @return ラップされた値のequals()の戻り値をそのまま返します。
     */
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;

        if (o instanceof MojangsonValue<?> jsonValue) {
            return Objects.equals(value, jsonValue.value);
        }
        else return false;
    }

    /**
     * この値のハッシュコードを返します。
     * @return ハッシュコード。
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), value);
    }

    /**
     * 値の型を取得します。
     * @return この値の型を表現するオブジェクト。
     */
    public abstract MojangsonValueType<?> getType();

    /**
     * この値の文字列表現を返します。出力はラップされた型が実装するtoString()に依存し、mojangsonフォーマットへの整形は行われません。
     * mojangsonフォーマットに整形する場合はMojangsonSerializerを使用してください。
     * @return ラップされた値のtoString()の戻り値をそのまま返します。
     * @see MojangsonSerializer
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * 渡されたJavaの値に対応するmojangson構造を返します。
     * @param value nullを含む任意のオブジェクト。
     * @return 引数をmojangson構造に変換したオブジェクト。MojangsonValueが渡された場合、引数をそのまま返します。
     */
    public static MojangsonValue<?> valueOf(@Nullable Object value) {
        return switch (value) {
            case null -> MojangsonNull.NULL;
            case Boolean v -> MojangsonByte.valueOf(v ? 1 : 0);
            case Number v -> MojangsonNumber.upcastValueOf(v);
            case Character v -> MojangsonString.valueOf(v);
            case String v -> MojangsonString.valueOf(v);
            case byte[] v -> new MojangsonByteArray(v);
            case int[] v -> new MojangsonIntArray(v);
            case long[] v -> new MojangsonLongArray(v);
            case Map<?, ?> v -> MojangsonCompound.valueOf(v);
            case Object[] v -> MojangsonList.valueOf(Arrays.asList(v));
            case Iterable<?> v -> MojangsonList.valueOf(v);
            case MojangsonValue<?> v -> v;
            default -> throw new IllegalArgumentException("mojangson値に変換できない型です: " + value.getClass().getName());
        };
    }
}
