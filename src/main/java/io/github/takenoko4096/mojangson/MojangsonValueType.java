package io.github.takenoko4096.mojangson;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * mojangsonにおける型を表現します。
 * @param <T> Javaにおける値。String、Number, Mapなど。
 */
@NullMarked
public abstract class MojangsonValueType<T extends MojangsonValue<?>> {
    /**
     * クラスオブジェクト。
     */
    protected final Class<T> clazz;

    /**
     * サブクラスのためのコンストラクタ。
     * @param clazz クラスオブジェクト。
     */
    protected MojangsonValueType(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 特定の型に対応するオブジェクトのみをmojangson値に変換し、それ以外は例外を投げます。
     * @param value nullを含む任意のオブジェクト。
     * @return 引数をmojangson構造に変換したオブジェクト。MojangsonValueが渡された場合、引数をそのまま返します。
     * @throws IllegalArgumentException 不適切な型の場合。
     */
    public abstract T toMojangson(@Nullable Object value) throws IllegalArgumentException;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MojangsonValueType<?> that = (MojangsonValueType<?>) object;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }

    /**
     * 引数に渡されたオブジェクトに対応する型オブジェクトを返します。
     * @param value nullを含む任意のオブジェクト。
     * @return 引数に渡されたオブジェクトの型によるmojangson型。
     */
    public static MojangsonValueType<?> get(@Nullable Object value) {
        return switch (value) {
            case Boolean ignored -> MojangsonValueTypes.BYTE;
            case Byte ignored -> MojangsonValueTypes.BYTE;
            case Short ignored -> MojangsonValueTypes.SHORT;
            case Integer ignored -> MojangsonValueTypes.INT;
            case Long ignored -> MojangsonValueTypes.LONG;
            case Float ignored -> MojangsonValueTypes.FLOAT;
            case Double ignored -> MojangsonValueTypes.DOUBLE;
            case Character ignored -> MojangsonValueTypes.STRING;
            case String ignored -> MojangsonValueTypes.STRING;
            case byte[] ignored -> MojangsonValueTypes.BYTE_ARRAY;
            case int[] ignored -> MojangsonValueTypes.INT_ARRAY;
            case long[] ignored -> MojangsonValueTypes.LONG_ARRAY;
            case Map<?, ?> v -> {
                MojangsonValueTypes.COMPOUND.toMojangson(v);
                yield MojangsonValueTypes.COMPOUND;
            }
            case Collection<?> v -> {
                MojangsonValueTypes.LIST.toMojangson(v);
                yield MojangsonValueTypes.LIST;
            }
            case MojangsonValue<?> v -> get(v.value);
            case null -> MojangsonValueTypes.NULL;
            default -> throw new IllegalArgumentException("対応していない型の値(" + value.getClass().getName() + "型)が渡されました");
        };
    }
}
