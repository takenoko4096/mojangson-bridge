package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JSONValueType;
import io.github.takenoko4096.json.JSONValueTypes;
import org.jspecify.annotations.NullMarked;

/**
 * json構造におけるboolean型を表現します。
 */
@NullMarked
public final class JSONBoolean extends JSONPrimitive<Boolean> {
    private JSONBoolean(boolean value) {
        super(value);
    }

    @Override
    public JSONValueType<?> getType() {
        return JSONValueTypes.BOOLEAN;
    }

    /**
     * booleanをJSONBooleanに変換します。
     * @param value boolean。
     * @return JSONBoolean。
     */
    public static JSONBoolean valueOf(boolean value) {
        return new JSONBoolean(value);
    }
}
