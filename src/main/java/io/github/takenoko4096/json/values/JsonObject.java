package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * json構造における連想配列を表現します。
 */
@NullMarked
public final class JsonObject extends JsonValue<Map<String, JsonValue<?>>> implements JsonStructure {
    /**
     * 空のJsonObjectを作成します。
     */
    public JsonObject() {
        super(new HashMap<>());
    }

    /**
     * StringとJsonValueのMapからJsonObjectを作成します。
     * @param map 元となるMap。
     */
    public JsonObject(Map<String, JsonValue<?>> map) {
        super(map);
    }

    @Override
    public JsonValueType<?> getType() {
        return JsonValueTypes.OBJECT;
    }

    /**
     * 引数に渡されたキーが存在するかを返します。
     * @param key キー。
     * @return 存在する場合、真。
     */
    public boolean has(String key) {
        return value.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * 引数に渡されたキーの型を返します。
     * @param key キー。
     * @return キーに紐づけられた値の型。
     * @throws IllegalArgumentException キーが存在しない場合。
     */
    public JsonValueType<?> getTypeOf(String key) throws IllegalArgumentException {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return JsonValueType.of(value.get(key));
    }

    /**
     * 引数に渡されたキーに紐づけられた値を返します。
     * @param key キー。
     * @param type 期待する型。
     * @return キーに紐づけられた値。
     * @param <T> 期待する型。
     * @throws IllegalArgumentException キーが存在しないか、型が予期しないものの場合。
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
     * 引数に渡されたキーに紐づけられた値を返します。キーが存在しないか、型が予期しないものの場合nullを返します。
     * @param key キー。
     * @param type 期待する型。
     * @return キーに紐づけられた値。
     * @param <T> 期待する型。
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

    public <T extends JsonValue<?>> T getOrDefault(String key, JsonValueType<T> type, T defaultValue) {
        return Objects.requireNonNullElse(getOrNull(key, type), defaultValue);
    }

    /**
     * 引数に渡されたキーに任意の値を紐づけます。
     * @param key キー。
     * @param value 値。
     */
    public void set(String key, Object value) {
        this.value.put(key, JsonValue.valueOf(value));
    }

    /**
     * 引数に渡されたキーを削除します。
     * @param key キー。
     * @return 削除に成功した場合、真。
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
     * @return すべてのキーのSet。
     */
    public Set<String> keys() {
        return Set.copyOf(value.keySet());
    }

    public Map<String, JsonValue<?>> toMap() {
        return Map.copyOf(value);
    }

    /**
     * このオブジェクトを再帰的にMapに変換します。
     * @return Map形式のディープコピー。
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
    public JsonObject deepCopy() {
        return JsonValueTypes.OBJECT.toJson(toMapRecursively());
    }

    /**
     * 引数に渡された構造体がこの構造体の部分構造であるかを返します。
     * @param other 構造体。
     * @return 部分構造であれば、真。
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
     * 引数に渡されたパスが存在するかを返します。
     * @param path パス。
     * @return 存在する場合、真。
     */
    public boolean has(JsonPath path) {
        try {
            final Boolean flag = path.access(this, JsonPath.JsonPathReference::has, false);
            if (flag == null) throw new IllegalStateException("NEVER HAPPENS");
            return flag;
        }
        catch (JsonPathUnableToAccessException e) {
            return false;
        }
    }

    /**
     * 引数に渡されたパスの型を返します。
     * @param path パス。
     * @return パスに紐づけられた値の型。
     * @throws IllegalArgumentException パスが存在しない場合。
     */
    public JsonValueType<?> getTypeOf(JsonPath path) {
        try {
            final JsonValueType<?> type = path.access(this, JsonPath.JsonPathReference::getType, false);
            if (type == null) throw new IllegalStateException("NEVER HAPPENS");
            return type;
        }
        catch (JsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 引数に渡されたパスに紐づけられた値を返します。
     * @param path パス。
     * @param type 期待する型。
     * @return パスに紐づけられた値。
     * @param <T> 期待する型。
     * @throws IllegalArgumentException パスが存在しないか、型が予期しないものの場合。
     */
    public <T extends JsonValue<?>> T getOrThrow(JsonPath path, JsonValueType<T> type) {
        try {
            final T value = path.access(this, reference -> reference.getOrThrow(type), false);
            if (value == null) {
                throw new IllegalArgumentException("値の取得に失敗しました: アクセスの戻り値が null です");
            }
            return value;
        }
        catch (JsonPathUnableToAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 引数に渡されたパスに紐づけられた値を返します。パスが存在しない、または型の不一致の場合にnullを返します。
     * @param path パス。
     * @param type 期待する型。
     * @return パスに紐づけられた値。
     * @param <T> 期待する型。
     */
    public <T extends JsonValue<?>> @Nullable T getOrNull(JsonPath path, JsonValueType<T> type) {
        try {
            return path.access(this, reference -> reference.getOrNull(type), false);
        }
        catch (JsonPathUnableToAccessException _) {
            return null;
        }
    }

    public <T extends JsonValue<?>> T getOrDefault(JsonPath path, JsonValueType<T> type, T defaultValue) {
        try {
            final T value = path.access(this, reference -> reference.getOrDefault(type, defaultValue), false);
            if (value == null) {
                return defaultValue;
            }
            return value;
        }
        catch (JsonPathUnableToAccessException _) {
            return defaultValue;
        }
    }

    /**
     * 引数に渡されたパスを削除します。
     * @param path パス。
     * @return 削除に成功した場合、真。
     */
    public boolean delete(JsonPath path) {
        try {
            final Boolean flag = path.access(this, JsonPath.JsonPathReference::delete, false);
            if (flag == null) throw new IllegalStateException("NEVER HAPPENS");
            return flag;
        }
        catch (JsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 引数に渡されたパスに任意の値を紐づけます。
     * @param path パス。
     * @param value 値。
     */
    public void set(JsonPath path, Object value) {
        try {
            path.access(this, reference -> {
                reference.set(value);
                return null;
            }, true);
        }
        catch (JsonPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * MapをJsonObjectに変換します。
     * @param value Map。
     * @return JsonObject。
     */
    public static JsonObject valueOf(Map<?, ?> value) {
        final var map = new HashMap<String, JsonValue<?>>();

        for (final var kv : value.entrySet()) {
            map.put(kv.getKey().toString(), valueOf(kv.getValue()));
        }

        return new JsonObject(map);
    }
}
