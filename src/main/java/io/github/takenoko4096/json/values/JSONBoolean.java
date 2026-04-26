package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JSONValueType;
import io.github.takenoko4096.json.JSONValueTypes;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class JSONBoolean extends JSONPrimitive<Boolean> {
    private JSONBoolean(boolean value) {
        super(value);
    }

    @Override
    public JSONValueType<?> getType() {
        return JSONValueTypes.BOOLEAN;
    }

    public static JSONBoolean valueOf(boolean value) {
        return new JSONBoolean(value);
    }
}
