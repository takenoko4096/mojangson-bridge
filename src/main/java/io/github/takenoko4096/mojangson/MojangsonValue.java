package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * mojangson構造を構成するすべての型のスーパークラス。
 * @param <T> Javaにおける値。String、Integer, Mapなど。
 */
@NullMarked
public abstract class MojangsonValue<T> {
    /**
     * ラップされた値。必要に応じてサブクラスで編集される可能性があります。
     */
    protected final T value;

    protected MojangsonValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MojangsonValue<?> mv = (MojangsonValue<?>) o;
        return Objects.equals(value, mv.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * 値の型を取得します。
     * @return この値の型を表現するオブジェクト。
     */
    public abstract MojangsonValueType<?> getType();

    @Override
    public String toString() {
        return value.toString();
    }

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
