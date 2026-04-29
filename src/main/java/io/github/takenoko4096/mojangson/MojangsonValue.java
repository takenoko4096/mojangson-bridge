package io.github.takenoko4096.mojangson;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * mojangson構造を構成するすべての型のスーパークラス。
 * @param <T> Javaにおける値。String、Integer, Mapなど。
 */
@NullMarked
public abstract class MojangsonValue<T> {
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

    public abstract MojangsonValueType<?> getType();

    @Override
    public String toString() {
        return value.toString();
    }
}
