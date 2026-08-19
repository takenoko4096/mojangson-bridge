package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValueType;
import io.github.takenoko4096.json.JsonValueTypes;

/**
 * json構造における number 型を表現します。
 */
public final class JsonNumber extends JsonPrimitive<Number> {
    private JsonNumber(Number value) {
        super(value);
    }

    @Override
    public JsonValueType<JsonNumber> getType() {
        return JsonValueTypes.NUMBER;
    }

    /**
     * byteとして取得します。
     * @return byte
     */
    public byte byteValue() {
        return value.byteValue();
    }

    /**
     * shortとして取得します。
     * @return short
     */
    public short shortValue() {
        return value.shortValue();
    }

    /**
     * intとして取得します。
     * @return int
     */
    public int intValue() {
        return value.intValue();
    }

    /**
     * longとして取得します。
     * @return long
     */
    public long longValue() {
        return value.longValue();
    }

    /**
     * floatとして取得します。
     * @return float
     */
    public float floatValue() {
        return value.floatValue();
    }

    /**
     * doubleとして取得します。
     * @return double
     */
    public double doubleValue() {
        return value.doubleValue();
    }

    /**
     * NumberをJsonNumberに変換します。
     * @param value Number
     * @return JsonNumber
     */
    public static JsonNumber valueOf(Number value) {
        return new JsonNumber(value);
    }
}
