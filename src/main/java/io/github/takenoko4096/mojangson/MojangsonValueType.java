package io.github.takenoko4096.mojangson;

import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * mojangsonにおける型を表現します。
 * @param <T> 値 java.lang.String、java.lang.Number, java.util.Map 等
 */
public abstract class MojangsonValueType<T extends MojangsonValue<?>> {
    /**
     * クラスオブジェクト
     */
    protected final Class<T> clazz;

    /**
     * サブクラスのためのコンストラクタ。
     * @param clazz クラスオブジェクト
     */
    protected MojangsonValueType(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 特定の型に対応するオブジェクトのみをmojangson値に変換し、それ以外は例外を投げます。
     * @param value nullを含む任意のオブジェクト
     * @return 引数をmojangson構造に変換したオブジェクト MojangsonValue が渡された場合、引数をそのまま返します。
     * @throws IllegalArgumentException 不適切な型の場合
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

    /**
     * この型の表現に使用されるクラスを返します。
     * @return クラス
     */
    public Class<T> getMojangsonClass() {
        return clazz;
    }

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }

    /**
     * オブジェクトに対応する型を返します。
     * @param value null を含む任意のオブジェクト
     * @return 引数に渡されたオブジェクトの型によるmojangson型
     */
    public static MojangsonValueType<?> of(@Nullable Object value) {
        return switch (value) {
            case MojangsonValue<?> v -> v.getType();
            case Boolean _, Byte _ -> MojangsonValueTypes.BYTE;
            case Short _ -> MojangsonValueTypes.SHORT;
            case Integer _ -> MojangsonValueTypes.INT;
            case Long _ -> MojangsonValueTypes.LONG;
            case Float _ -> MojangsonValueTypes.FLOAT;
            case Double _ -> MojangsonValueTypes.DOUBLE;
            case Character _, String _ -> MojangsonValueTypes.STRING;
            case byte[] _ -> MojangsonValueTypes.BYTE_ARRAY;
            case int[] _ -> MojangsonValueTypes.INT_ARRAY;
            case long[] _ -> MojangsonValueTypes.LONG_ARRAY;
            case Map<?, ?> v -> {
                try {
                    MojangsonValueTypes.COMPOUND.toMojangson(v);
                    yield MojangsonValueTypes.COMPOUND;
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("対応していない型の値(" + value.getClass().getName() + "型)が渡されました", e);
                }
            }
            case Iterable<?> v -> {
                try {
                    MojangsonValueTypes.LIST.toMojangson(v);
                    yield MojangsonValueTypes.LIST;
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("対応していない型の値(" + value.getClass().getName() + "型)が渡されました", e);
                }
            }
            case null -> MojangsonValueTypes.NULL;
            default -> throw new IllegalArgumentException("対応していない型の値(" + value.getClass().getName() + "型)が渡されました");
        };
    }
}
