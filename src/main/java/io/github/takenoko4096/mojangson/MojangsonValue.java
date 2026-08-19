package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.*;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * mojangson構造を構成する「値」を表します。
 * 0, 1, 1b, Hello, [-1s, 0, 0b], {key: value} 等はすべてこのクラスまたはそのサブクラスによって表現されます。
 * @param <T> ラップされる型。 java.lang.Boolean, java.lang.String 等
 */
public abstract class MojangsonValue<T> {
    /**
     * ラップされた値 必要に応じてサブクラスで編集される可能性があります。
     */
    protected final T value;

    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされる値
     */
    protected MojangsonValue(T value) {
        this.value = value;
    }

    /**
     * この値と引数に渡された値が等価であるかを調べます。出力はラップされた型が実装する equals(Object) に依存します。
     * MojangsonValue のインスタンスでない値との比較は常に false を返します。
     * @param o 比較対象の値
     * @return ラップされたクラスの equals() の実装に依存
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
    public abstract MojangsonValueType<?> getType();

    /**
     * この値の文字列表現を返します。出力はラップされた型が実装する toString() に依存し、mojangsonフォーマットへの整形は行われません。
     * mojangsonフォーマットに整形する場合は MojangsonSerializer を使用してください。
     * @return ラップされたクラスの toString() の実装に依存
     * @see MojangsonSerializer
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * 渡された値に対応するmojangson構造を返します。
     * @param value  null を含む任意のオブジェクト。
     * @return 引数をmojangson構造に変換したオブジェクト。 MojangsonValue が渡された場合、引数をそのまま返します。
     */
    public static MojangsonValue<?> valueOf(@Nullable Object value) {
        return switch (value) {
            case null -> MojangsonNull.NULL;
            case Boolean v -> MojangsonByte.valueOf(v ? 1 : 0);
            case Byte b -> MojangsonByte.valueOf(b.byteValue());
            case Short s -> MojangsonShort.valueOf(s.shortValue());
            case Integer i -> MojangsonInt.valueOf(i.intValue());
            case Long l -> MojangsonLong.valueOf(l.longValue());
            case Float f -> MojangsonFloat.valueOf(f.floatValue());
            case Double d -> MojangsonDouble.valueOf(d.doubleValue());
            case Character c -> MojangsonString.valueOf(c);
            case String s -> MojangsonString.valueOf(s);
            case byte[] a -> new MojangsonByteArray(a);
            case int[] a -> new MojangsonIntArray(a);
            case long[] a -> new MojangsonLongArray(a);
            case Map<?, ?> m -> MojangsonCompound.valueOf(m);
            case Object[] a -> MojangsonList.valueOf(Arrays.asList(a));
            case Iterable<?> i -> MojangsonList.valueOf(i);
            case MojangsonValue<?> v -> v;
            default -> throw new IllegalArgumentException("mojangson値に変換できない型です: " + value.getClass().getName());
        };
    }
}
