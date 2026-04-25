package com.gmail.takenokoii78.json.values;

import com.gmail.takenokoii78.json.JSONValueType;
import com.gmail.takenokoii78.json.JSONValueTypes;
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
