package io.github.takenoko4096.json;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * jsonにおける型を表現します。
 * @param <T> 値 String、Number, Map等
 */
public abstract class JsonValueType<T extends JsonValue<?>> {
    /**
     * クラスオブジェクト
     */
    protected final Class<T> clazz;

    /**
     * サブクラスのためのコンストラクタ。
     * @param clazz クラスオブジェクト
     */
    protected JsonValueType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        JsonValueType<?> that = (JsonValueType<?>) object;
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
    public Class<T> getJsonClass() {
        return clazz;
    }

    /**
     * 特定の型に対応するオブジェクトのみをjson値に変換し、それ以外は例外を投げます。
     * @param value nullを含む任意のオブジェクト
     * @return 引数をjson構造に変換したオブジェクト JsonValueが渡された場合、引数をそのまま返します。
     * @throws IllegalArgumentException 不適切な型の場合
     */
    public abstract T toJson(@Nullable Object value) throws IllegalArgumentException;

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }

    /**
     * 引数に渡されたオブジェクトに対応する型オブジェクトを返します。
     * @param value null を含む任意のオブジェクト
     * @return 引数に渡されたオブジェクトの型によるjson型
     */
    public static JsonValueType<?> of(@Nullable Object value) {
        return switch (value) {
            case JsonValue<?> v -> of(v.value);
            case Boolean _ -> JsonValueTypes.BOOLEAN;
            case Number _ -> JsonValueTypes.NUMBER;
            case String _ -> JsonValueTypes.STRING;
            case Map<?, ?> _ -> JsonValueTypes.OBJECT;
            case Iterable<?> _ -> JsonValueTypes.ARRAY;
            case Character _ -> JsonValueTypes.STRING;
            case null -> JsonValueTypes.NULL;
            default -> {
                if (value.getClass().isArray()) yield JsonValueTypes.ARRAY;
                else throw new IllegalArgumentException("渡された値はjsonで使用できない型です: " + value.getClass().getName());
            }
        };
    }
}
