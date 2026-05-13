package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import org.jspecify.annotations.NullMarked;

/**
 * mojangsonにおける数値を表現します。
 * @param <T> Numberの子クラス。
 */
@NullMarked
public abstract class MojangsonNumber<T extends Number> extends MojangsonPrimitive<T> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされる値。
     */
    protected MojangsonNumber(T value) {
        super(value);
    }

    /**
     * byteとして取得します。
     * @return byte。
     */
    public final byte byteValue() {
        return value.byteValue();
    }

    /**
     * shortとして取得します。
     * @return short。
     */
    public final short shortValue() {
        return value.shortValue();
    }

    /**
     * intとして取得します。
     * @return int。
     */
    public final int intValue() {
        return value.intValue();
    }

    /**
     * longとして取得します。
     * @return long。
     */
    public final long longValue() {
        return value.longValue();
    }

    /**
     * floatとして取得します。
     * @return float。
     */
    public final float floatValue() {
        return value.floatValue();
    }

    /**
     * doubleとして取得します。
     * @return double。
     */
    public final double doubleValue() {
        return value.doubleValue();
    }

    /**
     * NumberをMojangsonNumberに変換します。
     * @param value Number。
     * @return MojangsonNumber。
     */
    public static MojangsonNumber<?> upcastValueOf(Number value) {
        return (MojangsonNumber<?>) MojangsonValueType.get(value).toMojangson(value);
    }
}
