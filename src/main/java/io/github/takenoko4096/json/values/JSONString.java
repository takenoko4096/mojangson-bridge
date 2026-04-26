package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JSONValueType;
import io.github.takenoko4096.json.JSONValueTypes;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class JSONString extends JSONPrimitive<String> {
    private JSONString(String value) {
        super(value);
    }

    @Override
    public JSONValueType<?> getType() {
        return JSONValueTypes.STRING;
    }

    public static JSONString valueOf(String value) {
        return new JSONString(value);
    }
}
