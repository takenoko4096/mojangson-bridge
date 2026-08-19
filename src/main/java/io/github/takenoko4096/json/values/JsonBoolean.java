package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValueType;
import io.github.takenoko4096.json.JsonValueTypes;
import org.jspecify.annotations.NullMarked;

/**
 * json構造におけるboolean型を表現します。
 */
@NullMarked
public final class JsonBoolean extends JsonPrimitive<Boolean> {
    private JsonBoolean(boolean value) {
        super(value);
    }

    @Override
    public JsonValueType<?> getType() {
        return JsonValueTypes.BOOLEAN;
    }

    /**
     * booleanをJsonBooleanに変換します。
     * @param value boolean。
     * @return JsonBoolean。
     */
    public static JsonBoolean valueOf(boolean value) {
        return new JsonBoolean(value);
    }
}
