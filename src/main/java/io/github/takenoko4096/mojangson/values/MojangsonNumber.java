package io.github.takenoko4096.mojangson.values;

/**
 * mojangsonにおける数値を表現します。
 * @param <T> Number の子クラス
 */
public abstract class MojangsonNumber<T extends Number> extends MojangsonPrimitive<T> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされる値
     */
    protected MojangsonNumber(T value) {
        super(value);
    }

    /**
     * byte として取得します。
     * @return byte
     */
    public final byte byteValue() {
        return value.byteValue();
    }

    /**
     * short として取得します。
     * @return short
     */
    public final short shortValue() {
        return value.shortValue();
    }

    /**
     * int として取得します。
     * @return int
     */
    public final int intValue() {
        return value.intValue();
    }

    /**
     * long として取得します。
     * @return long
     */
    public final long longValue() {
        return value.longValue();
    }

    /**
     * float として取得します。
     * @return float
     */
    public final float floatValue() {
        return value.floatValue();
    }

    /**
     * double として取得します。
     * @return double
     */
    public final double doubleValue() {
        return value.doubleValue();
    }
}
