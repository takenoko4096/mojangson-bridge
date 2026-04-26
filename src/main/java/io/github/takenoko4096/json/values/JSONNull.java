package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JSONValueType;
import io.github.takenoko4096.json.JSONValueTypes;
import org.jspecify.annotations.Nullable;

public final class JSONNull extends JSONPrimitive<Object> {
    private JSONNull() {
        super(null);
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public JSONValueType<?> getType() {
        return JSONValueTypes.NULL;
    }

    @Override
    public @Nullable Object getValue() {
        return super.getValue();
    }

    public static final JSONNull NULL = new JSONNull();
}
