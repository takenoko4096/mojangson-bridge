package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JSONValueType;
import io.github.takenoko4096.json.JSONValueTypes;
import org.jspecify.annotations.NullMarked;

/**
 * json構造におけるnumber型を表現します。
 */
@NullMarked
public final class JSONNumber extends JSONPrimitive<Number> {
    private JSONNumber(Number value) {
        super(value);
    }

    @Override
    public JSONValueType<?> getType() {
        return JSONValueTypes.NUMBER;
    }

    /**
     * byteとして取得します。
     * @return byte。
     */
    public byte byteValue() {
        return value.byteValue();
    }

    /**
     * shortとして取得します。
     * @return short。
     */
    public short shortValue() {
        return value.shortValue();
    }

    /**
     * intとして取得します。
     * @return int。
     */
    public int intValue() {
        return value.intValue();
    }

    /**
     * longとして取得します。
     * @return long。
     */
    public long longValue() {
        return value.longValue();
    }

    /**
     * floatとして取得します。
     * @return float。
     */
    public float floatValue() {
        return value.floatValue();
    }

    /**
     * doubleとして取得します。
     * @return double。
     */
    public double doubleValue() {
        return value.doubleValue();
    }

    /**
     * NumberをJSONNumberに変換します。
     * @param value Number。
     * @return JSONNumber。
     */
    public static JSONNumber valueOf(Number value) {
        return new JSONNumber(value);
    }
}
