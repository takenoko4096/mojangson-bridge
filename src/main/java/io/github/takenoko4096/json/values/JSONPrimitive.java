package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JSONValue;

public abstract class JSONPrimitive<T> extends JSONValue<T> {
    protected JSONPrimitive(T value) {
        super(value);
    }

    public T getValue() {
        return value;
    }
}
