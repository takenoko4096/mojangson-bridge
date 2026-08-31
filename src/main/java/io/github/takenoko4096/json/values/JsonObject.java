package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.*;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * json構造における連想配列を表現します。
 */
public final class JsonObject extends JsonValue<Map<String, JsonValue<?>>> implements JsonStructure {
    /**
     * 空の JsonObject を作成します。
     */
    public JsonObject() {
        super(new HashMap<>());
    }

    /**
     * String と JsonValue&lt;?&gt; の Map から JsonObject を作成します。
     * @param map 元となる Map
     */
    public JsonObject(Map<String, JsonValue<?>> map) {
        super(map);
    }

    @Override
    public JsonValueType<JsonObject> getType() {
        return JsonValueTypes.OBJECT;
    }

    /**
     * キーに対応する位置が存在するかを返します。
     * @param key キー
     * @return 存在する場合 true
     */
    public boolean has(String key) {
        return value.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * キーに紐づけられた値の型を返します。
     * @param key キー
     * @return キーに紐づけられた値の型
     * @throws IllegalArgumentException キーに対応する位置が存在しない場合
     */
    public JsonValueType<?> getTypeOf(String key) throws IllegalArgumentException {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return JsonValueType.of(value.get(key));
    }

    /**
     * キーに紐づけられた値を返します。
     * @param key キー
     * @param type 期待する型
     * @return キーに紐づけられた値
     * @param <T> 期待する型
     * @throws IllegalArgumentException キーに対応する位置が存在しないか、型が予期しないものの場合
     */
    public <T extends JsonValue<?>> T getOrThrow(String key, JsonValueType<T> type) throws IllegalArgumentException {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOf(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型(" + getTypeOf(key) + ")の値と紐づけられていません: " + type);
        }

        return type.toJson(value.get(key));
    }

    /**
     * キーに紐づけられた値を返します。キーに対応する位置が存在しないか、型が予期しないものの場合nullを返します。
     * @param key キー
     * @param type 期待する型
     * @return キーに紐づけられた値
     * @param <T> 期待する型
     */
    public <T extends JsonValue<?>> @Nullable T getOrNull(String key, JsonValueType<T> type) {
        if (has(key)) {
            if (getTypeOf(key).equals(type)) {
                return getOrThrow(key, type);
            }
            else return null;
        }
        else return null;
    }

    /**
     * キーに紐づけられた値を返します。キーに対応する位置が存在しないか、型が予期しないものの場合デフォルト値を返します。
     * @param key キー
     * @param type 期待する型
     * @param defaultValue デフォルト値
     * @return キーに紐づけられた値
     * @param <T> 期待する型
     */
    public <T extends JsonValue<?>> T getOrDefault(String key, JsonValueType<T> type, Object defaultValue) {
        return Objects.requireNonNullElse(getOrNull(key, type), type.toJson(defaultValue));
    }

    /**
     * キーに任意の値を紐づけます。
     * @param key キー
     * @param value 値
     */
    public void set(String key, Object value) {
        this.value.put(key, JsonValue.valueOf(value));
    }

    /**
     * キーを削除します。
     * @param key キー
     * @return 削除に成功した場合 true
     */
    public boolean delete(String key) {
        if (has(key)) {
            value.remove(key);
            return true;
        }
        else return false;
    }

    @Override
    public boolean clear() {
        if (isEmpty()) return false;
        else {
            value.clear();
            return true;
        }
    }

    /**
     * このオブジェクトが持つキーの集合を返します。
     * @return すべてのキーを含む Set&lt;String&gt;
     */
    public Set<String> keys() {
        return Set.copyOf(value.keySet());
    }

    /**
     * このオブジェクトをMap&lt;String, JsonValue&lt;?&gt;&gt;に変換して返します。
     * @return Map&lt;String, JsonValue&lt;?&gt;&gt;
     */
    public Map<String, JsonValue<?>> toMap() {
        return Map.copyOf(value);
    }

    /**
     * このオブジェクトを再帰的にMap&lt;String, Object&gt;に変換します。
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, @Nullable Object> toMapRecursively() {
        final Map<String, Object> map = new HashMap<>();

        for (final String key : keys()) {
            final JsonValueType<?> type = getTypeOf(key);

            if (type.equals(JsonValueTypes.OBJECT)) {
                final JsonObject object = getOrThrow(key, JsonValueTypes.OBJECT);
                map.put(key, object.toMapRecursively());
            }
            else if (type.equals(JsonValueTypes.ARRAY)) {
                final JsonArray array = getOrThrow(key, JsonValueTypes.ARRAY);
                map.put(key, array.toListRecursively());
            }
            else if (value.get(key) instanceof JsonPrimitive<?> primitive) {
                map.put(key, primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(key).getClass().getName());
            }
        }

        return map;
    }

    @Override
    public JsonObject copy() {
        return JsonValueTypes.OBJECT.toJson(toMapRecursively());
    }

    /**
     * ある構造体がこの構造体の部分構造であるかを返します。
     * @param other 構造体
     * @return 部分構造であれば true
     */
    public boolean isSuperOf(JsonObject other) {
        for (final String key : other.keys()) {
            if (has(key)) {
                final JsonValue<?> conditionValue = other.getOrThrow(key, other.getTypeOf(key));

                switch (conditionValue) {
                    case JsonObject jsonObject -> {
                        if (!getOrThrow(key, JsonValueTypes.OBJECT).isSuperOf(jsonObject)) {
                            return false;
                        }
                    }
                    case JsonArray jsonArray -> {
                        if (!getOrThrow(key, JsonValueTypes.ARRAY).isSuperOf(jsonArray)) {
                            return false;
                        }
                    }
                    default -> {
                        if (!getOrThrow(key, getTypeOf(key)).equals(conditionValue)) {
                            return false;
                        }
                    }
                }
            }
            else return false;
        }

        return true;
    }

    /**
     * パスに対応する位置が存在するかを返します。
     * @param path パス
     * @return 存在する場合 true
     */
    public boolean has(JsonPath path) {
        try {
            final Boolean t = path.access(
                this,
                false,
                false,
                JsonObject::has,
                JsonArray::has
            );

            if (t == null) {
                return false;
            }

            return t;
        }
        catch (JsonPathUnableToAccessException e) {
            return false;
        }
    }

    /**
     * パスに紐づけられた値の型を返します。
     * @param path パス
     * @return パスに紐づけられた値の型
     * @throws IllegalArgumentException パスに対応する位置が存在しない場合
     */
    public JsonValueType<?> getTypeOf(JsonPath path) {
        try {
            final JsonValueType<?> t = path.access(
                this,
                false,
                true,
                JsonObject::getTypeOf,
                JsonArray::getTypeAt
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (JsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * パスに紐づけられた値を返します。
     * @param path パス
     * @param type 期待する型
     * @return パスに紐づけられた値
     * @param <T> 期待する型
     * @throws IllegalArgumentException パスに対応する位置が存在しないか、得られた値の型が予期しないものの場合
     */
    public <T extends JsonValue<?>> T getOrThrow(JsonPath path, JsonValueType<T> type) {
        try {
            final T t = path.access(
                this,
                false,
                true,
                (s, p) -> s.getOrThrow(p, type),
                (s, p) -> s.getOrThrow(p, type)
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (JsonPathUnableToAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * パスに紐づけられた値を返します。パスが存在しない、または型の不一致の場合にnullを返します。
     * @param path パス
     * @param type 期待する型
     * @return パスに紐づけられた値
     * @param <T> 期待する型
     */
    public <T extends JsonValue<?>> @Nullable T getOrNull(JsonPath path, JsonValueType<T> type) {
        try {
            final T t = path.access(
                this,
                false,
                true,
                (s, p) -> s.getOrNull(p, type),
                (s, p) -> s.getOrNull(p, type)
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (JsonPathUnableToAccessException _) {
            return null;
        }
    }

    /**
     * パスに紐づけられた値を返します。パスが存在しない、または型の不一致の場合にデフォルト値を返します。
     * @param path パス
     * @param type 期待する型
     * @param defaultValue デフォルト値
     * @return パスに紐づけられた値
     * @param <T> 期待する型
     */
    public <T extends JsonValue<?>> T getOrDefault(JsonPath path, JsonValueType<T> type, Object defaultValue) {
        final T defaultJsonValue = type.toJson(defaultValue);

        try {
            final T t = path.access(
                this,
                false,
                true,
                (s, p) -> s.getOrDefault(p, type, defaultValue),
                (s, p) -> s.getOrDefault(p, type, defaultValue)
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (JsonPathUnableToAccessException _) {
            return defaultJsonValue;
        }
    }

    /**
     * パスに対応する位置を削除します。
     * @param path パス
     * @return 削除に成功した場合 true
     */
    public boolean delete(JsonPath path) {
        try {
            final Boolean t = path.access(
                this,
                false,
                true,
                JsonObject::delete,
                JsonArray::delete
            );

            if (t == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            return t;
        }
        catch (JsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * パスに任意の値を紐づけます。
     * @param path パス
     * @param value 値
     */
    public void set(JsonPath path, Object value) {
        try {
            path.access(
                this,
                false,
                true,
                (s, p) -> {
                    s.set(p, value);
                    return null;
                },
                (s, p) -> {
                    s.set(p, value);
                    return null;
                }
            );
        }
        catch (JsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Map&lt;String, ?&gt; を JsonObject に変換します。
     * @param value Map&lt;String, ?&gt;
     * @return JsonObject
     */
    public static JsonObject valueOf(Map<String, ?> value) {
        final var map = new HashMap<String, JsonValue<?>>();

        for (final Map.Entry<String, ?> entry : value.entrySet()) {
            map.put(entry.getKey(), valueOf(entry.getValue()));
        }

        return new JsonObject(map);
    }
}
