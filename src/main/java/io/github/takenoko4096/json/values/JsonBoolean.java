package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValueType;
import io.github.takenoko4096.json.JsonValueTypes;

/**
 * json構造における boolean 型を表現します。
 */
public final class JsonBoolean extends JsonPrimitive<Boolean> {
    private JsonBoolean(boolean value) {
        super(value);
    }

    @Override
    public JsonValueType<JsonBoolean> getType() {
        return JsonValueTypes.BOOLEAN;
    }

    /**
     * boolean を JsonBoolean に変換します。
     * @param value boolean
     * @return JsonBoolean
     */
    public static JsonBoolean valueOf(boolean value) {
        return new JsonBoolean(value);
    }
}
