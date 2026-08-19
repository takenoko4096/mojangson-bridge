package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * すべての型オブジェクトがこのクラスの静的フィールドで定義されています。
 * @see JsonValueType
 */
public final class JsonValueTypes {
    private JsonValueTypes() {}

    /**
     * boolean に対応。
     * @see JsonBoolean
     */
    public static final JsonValueType<JsonBoolean> BOOLEAN = new JsonValueType<>(JsonBoolean.class) {
        @Override
        public JsonBoolean toJson(@Nullable Object value) {
            if (value instanceof JsonBoolean v) return v;
            else if (value instanceof Boolean v) return JsonBoolean.valueOf((boolean) v);
            else throw new IllegalArgumentException("value is not name boolean value");
        }
    };

    /**
     * java.lang.Number に対応。
     * @see JsonNumber
     */
    public static final JsonValueType<JsonNumber> NUMBER = new JsonValueType<>(JsonNumber.class) {
        @Override
        public JsonNumber toJson(@Nullable Object value) {
            if (value instanceof JsonNumber v) return v;
            else if (value instanceof Number v) return JsonNumber.valueOf(v);
            else throw new IllegalArgumentException("value is not name number value");
        }
    };

    /**
     * java.lang.String に対応。
     * @see JsonString
     */
    public static final JsonValueType<JsonString> STRING = new JsonValueType<>(JsonString.class) {
        @Override
        public JsonString toJson(@Nullable Object value) {
            return switch (value) {
                case JsonString v -> v;
                case String v -> JsonString.valueOf(v);
                case Character v -> JsonString.valueOf(String.valueOf(v));
                case null, default -> throw new IllegalArgumentException("value is not name string value");
            };
        }
    };

    /**
     * java.util.Map&lt;String, ?&gt; に対応。
     * @see JsonObject
     */
    public static final JsonValueType<JsonObject> OBJECT = new JsonValueType<>(JsonObject.class) {
        @Override
        public JsonObject toJson(@Nullable Object value) {
            if (value instanceof JsonObject jsonObject) return jsonObject;

            if (value instanceof Map<?, ?> map) {
                final Map<String, JsonValue<?>> object = new HashMap<>();

                for (final Object key : map.keySet()) {
                    if (key instanceof String string) {
                        object.put(string, JsonValue.valueOf(map.get(string)));
                    }
                    else {
                        throw new IllegalArgumentException("A key of Map is not name string");
                    }
                }

                return new JsonObject(object);
            }
            else throw new IllegalArgumentException("value is not name json object value: " + (value == null ? null : value.getClass().getName()));
        }
    };

    /**
     * java.util.List&lt;?&gt; に対応。
     * @see JsonArray
     */
    public static final JsonValueType<JsonArray> ARRAY = new JsonValueType<>(JsonArray.class) {
        @Override
        public JsonArray toJson(@Nullable Object value) {
            switch (value) {
                case JsonArray jsonArray -> {
                    return jsonArray;
                }
                case TypedJsonArray<?> typedJsonArray -> {
                    return typedJsonArray.untyped();
                }
                case Iterable<?> iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case Object[] iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case boolean[] iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case byte[] iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case short[] iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case int[] iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case long[] iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case char[] iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case float[] iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case double[] iterable -> {
                    final List<JsonValue<?>> listOfJsonValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJsonValue.add(JsonValue.valueOf(element));
                    }

                    return new JsonArray(listOfJsonValue);
                }
                case null -> throw new IllegalArgumentException("value is not name json array value: null");
                default -> throw new IllegalArgumentException("value is not name json array value: " + value.getClass().getName());
            }
        }
    };

    /**
     * null に対応。
     * @see JsonNull
     */
    public static final JsonValueType<JsonNull> NULL = new JsonValueType<>(JsonNull.class) {
        @Override
        public JsonNull toJson(@Nullable Object value) {
            if (value instanceof JsonNull jsonNull) return jsonNull;
            else if (value == null) return JsonNull.NULL;
            else throw new IllegalArgumentException("value is not name null value");
        }
    };
}
