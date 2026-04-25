package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.json.values.*;

import java.util.Map;
import java.util.Objects;

public abstract class JSONValue<T> {
    protected final T value;

    protected JSONValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONValue<?> jsonValue = (JSONValue<?>) o;
        return Objects.equals(value, jsonValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public abstract JSONValueType<?> getType();

    public static JSONValue<?> valueOf(Object value) {
        return switch (value) {
            case null -> JSONNull.NULL;
            case Boolean b -> JSONBoolean.valueOf((boolean) b);
            case Number n -> JSONNumber.valueOf(n);
            case String s -> JSONString.valueOf(s);
            case Map<?, ?> m -> JSONObject.valueOf(m);
            case Iterable<?> i -> JSONArray.valueOf(i);
            case JSONValue<?> j -> j;
            default -> JSONString.valueOf(value.getClass().getName());
        };
    }
}
