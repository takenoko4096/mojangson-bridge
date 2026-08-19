package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValueType;
import io.github.takenoko4096.json.JsonValueTypes;

/**
 * json構造における string 型を表現します。
 */
public final class JsonString extends JsonPrimitive<String> {
    private JsonString(String value) {
        super(value);
    }

    @Override
    public JsonValueType<JsonString> getType() {
        return JsonValueTypes.STRING;
    }

    /**
     * StringをJsonStringに変換します。
     * @param value String
     * @return JsonString
     */
    public static JsonString valueOf(String value) {
        return new JsonString(value);
    }
}
