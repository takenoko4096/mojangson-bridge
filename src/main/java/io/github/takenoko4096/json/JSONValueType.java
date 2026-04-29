package io.github.takenoko4096.json;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * jsonにおける型を表現します。
 * @param <T> Javaにおける値。String、Number, Mapなど。
 */
@NullMarked
public abstract class JSONValueType<T extends JSONValue<?>> {
    /**
     * クラスオブジェクト。
     */
    protected final Class<T> clazz;

    protected JSONValueType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        JSONValueType<?> that = (JSONValueType<?>) object;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    /**
     * 特定の型に対応するオブジェクトのみをjson値に変換し、それ以外は例外を投げます。
     * @param value nullを含む任意のオブジェクト。
     * @return 引数をjson構造に変換したオブジェクト。JSONValueが渡された場合、引数をそのまま返します。
     * @throws IllegalArgumentException 不適切な型の場合。
     */
    public abstract T toJSON(@Nullable Object value) throws IllegalArgumentException;

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }

    /**
     * 引数に渡されたオブジェクトに対応する型オブジェクトを返します。
     * @param value nullを含む任意のオブジェクト。
     * @return 引数に渡されたオブジェクトの型によるjson型。
     */
    public static JSONValueType<?> get(@Nullable Object value) {
        return switch (value) {
            case JSONValue<?> jsonValue -> get(jsonValue.value);
            case Boolean ignored -> JSONValueTypes.BOOLEAN;
            case Number ignored -> JSONValueTypes.NUMBER;
            case String ignored -> JSONValueTypes.STRING;
            case Map<?, ?> ignored -> JSONValueTypes.OBJECT;
            case Iterable<?> ignored -> JSONValueTypes.ARRAY;
            case Character ignored -> JSONValueTypes.STRING;
            case null -> JSONValueTypes.NULL;
            default -> {
                if (value.getClass().isArray()) yield JSONValueTypes.ARRAY;
                else throw new IllegalArgumentException("渡された値はjsonで使用できない型です: " + value.getClass().getName());
            }
        };
    }
}
