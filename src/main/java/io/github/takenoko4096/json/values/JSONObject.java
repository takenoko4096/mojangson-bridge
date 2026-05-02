package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * jsonにおけるobjectを表現します。
 */
@NullMarked
public final class JSONObject extends JSONValue<Map<String, JSONValue<?>>> implements JSONStructure {
    public JSONObject() {
        super(new HashMap<>());
    }

    public JSONObject(Map<String, JSONValue<?>> map) {
        super(map);
    }

    @Override
    public JSONValueType<?> getType() {
        return JSONValueTypes.OBJECT;
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
    public JSONValueType<?> getTypeOf(String key) throws IllegalArgumentException {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return JSONValueType.get(value.get(key));
    }

    /**
     * 引数に渡されたキーに紐づけられた値を返します。
     * @param key キー。
     * @param type 期待する型。
     * @return キーに紐づけられた値。
     * @param <T> 期待する型。
     * @throws IllegalArgumentException キーが存在しないか、型が予期しないものの場合。
     */
    public <T extends JSONValue<?>> T get(String key, JSONValueType<T> type) throws IllegalArgumentException {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOf(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型(" + getTypeOf(key) + ")の値と紐づけられていません: " + type);
        }

        return type.toJSON(value.get(key));
    }

    /**
     * 引数に渡されたキーに任意の値を紐づけます。
     * @param key キー。
     * @param value 値。
     */
    public void set(String key, Object value) {
        this.value.put(key, JSONValueType.get(value).toJSON(value));
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
        return value.keySet();
    }

    public void merge(JSONObject jsonObject) {
        for (final String key : jsonObject.keys()) {
            set(key, jsonObject.value.get(key));
        }
    }

    /**
     * このオブジェクトを再帰的にMapに変換します。
     * @return Map形式のディープコピー。
     */
    public Map<String, @Nullable Object> asMap() {
        final Map<String, Object> map = new HashMap<>();

        for (final String key : keys()) {
            final JSONValueType<?> type = getTypeOf(key);

            if (type.equals(JSONValueTypes.OBJECT)) {
                final JSONObject object = get(key, JSONValueTypes.OBJECT);
                map.put(key, object.asMap());
            }
            else if (type.equals(JSONValueTypes.ARRAY)) {
                final JSONArray array = get(key, JSONValueTypes.ARRAY);
                map.put(key, array.asList());
            }
            else if (value.get(key) instanceof JSONPrimitive<?> primitive) {
                map.put(key, primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(key).getClass().getName());
            }
        }

        return map;
    }

    @Override
    public JSONObject copy() {
        return JSONValueTypes.OBJECT.toJSON(asMap());
    }

    /**
     * 引数に渡された構造体がこの構造体の部分構造であるかを返します。
     * @param other 構造体。
     * @return 部分構造であれば、真。
     */
    public boolean isSuperOf(JSONObject other) {
        for (final String key : other.keys()) {
            if (has(key)) {
                final JSONValue<?> conditionValue = other.get(key, other.getTypeOf(key));

                switch (conditionValue) {
                    case JSONObject jsonObject -> {
                        if (!get(key, JSONValueTypes.OBJECT).isSuperOf(jsonObject)) {
                            return false;
                        }
                    }
                    case JSONArray jsonArray -> {
                        if (!get(key, JSONValueTypes.ARRAY).isSuperOf(jsonArray)) {
                            return false;
                        }
                    }
                    default -> {
                        if (!get(key, getTypeOf(key)).equals(conditionValue)) {
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
    public boolean has(JSONPath path) {
        try {
            final Boolean flag = path.access(this, JSONPath.JSONPathReference::has, false);
            if (flag == null) throw new IllegalStateException("NEVER HAPPENS");
            return flag;
        }
        catch (JSONPathUnableToAccessException e) {
            return false;
        }
    }

    /**
     * 引数に渡されたパスの型を返します。
     * @param path パス。
     * @return パスに紐づけられた値の型。
     * @throws IllegalArgumentException パスが存在しない場合。
     */
    public JSONValueType<?> getTypeOf(JSONPath path) {
        try {
            final JSONValueType<?> type = path.access(this, JSONPath.JSONPathReference::getType, false);
            if (type == null) throw new IllegalStateException("NEVER HAPPENS");
            return type;
        }
        catch (JSONPathUnableToAccessException e) {
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
    public <T extends JSONValue<?>> T get(JSONPath path, JSONValueType<T> type) {
        try {
            final T value = path.access(this, reference -> reference.get(type), false);
            if (value == null) throw new IllegalStateException("NEVER HAPPENS");
            return value;
        }
        catch (JSONPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 引数に渡されたパスを削除します。
     * @param path パス。
     * @return 削除に成功した場合、真。
     */
    public boolean delete(JSONPath path) {
        try {
            final Boolean flag = path.access(this, JSONPath.JSONPathReference::delete, false);
            if (flag == null) throw new IllegalStateException("NEVER HAPPENS");
            return flag;
        }
        catch (JSONPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 引数に渡されたパスに任意の値を紐づけます。
     * @param path パス。
     * @param value 値。
     */
    public void set(JSONPath path, Object value) {
        try {
            path.access(this, reference -> {
                reference.set(value);
                return null;
            }, true);
        }
        catch (JSONPathUnableToAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * MapをJSONObjectに変換します。
     * @param value Map。
     * @return JSONObject。
     */
    public static JSONObject valueOf(Map<?, ?> value) {
        final var map = new HashMap<String, JSONValue<?>>();

        for (final var kv : value.entrySet()) {
            map.put(kv.getKey().toString(), valueOf(kv.getValue()));
        }

        return new JSONObject(map);
    }
}
