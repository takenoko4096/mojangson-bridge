package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.*;
import io.github.takenoko4096.json.JSONValue;
import io.github.takenoko4096.json.JSONValueType;
import io.github.takenoko4096.json.JSONValueTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * json構造における配列を表現します。
 */
@NullMarked
public final class JSONArray extends JSONValue<List<JSONValue<?>>> implements JSONIterable<JSONValue<?>> {
    /**
     * 長さ0のJSONArrayを作成します。
     */
    public JSONArray() {
        super(new ArrayList<>());
    }

    /**
     * JSONValueのListからJSONArrayを作成します。
     * @param list 元となるList。
     */
    public JSONArray(List<JSONValue<?>> list) {
        super(new ArrayList<>(list));
    }

    @Override
    public JSONValueType<?> getType() {
        return JSONValueTypes.ARRAY;
    }

    @Override
    public boolean has(int index) {
        if (index >= 0) return index < value.size();
        else if (value.size() + index >= 0) return has(value.size() + index);
        else return false;
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * 引数に渡されたインデックスに格納された値の型を返します。
     * @param index インデックス。
     * @return インデックスに格納された値の型。
     * @throws IllegalArgumentException インデックスが存在しない場合。
     */
    public JSONValueType<?> getTypeAt(int index) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return JSONValueType.get(value.get(index));
        else return JSONValueType.get(value.get(value.size() + index));
    }

    /**
     * 引数に渡されたインデックスに格納された値を返します。
     * @param index インデックス。
     * @param type 期待する型。
     * @return インデックスに格納された値。
     * @param <T> 期待する型。
     * @throws IllegalArgumentException インデックスが存在しない、または予期しない型の場合。
     */
    public <T extends JSONValue<?>> T get(int index, JSONValueType<T> type) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!getTypeAt(index).equals(type)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません");
        }

        if (index >= 0) return type.toJSON(value.get(index));
        else return type.toJSON(value.get(value.size() + index));
    }

    /**
     * 引数に渡されたインデックスに値を格納し、そのインデックス以降の値を後ろに追いやります。
     * @param index インデックス。
     * @param value 格納する値。
     * @throws IllegalArgumentException インデックスが不正な場合。
     */
    public void add(int index, @Nullable Object value) throws IllegalArgumentException {
        if (index > this.value.size()) {
            throw new IllegalArgumentException("インデックス " + index + " はサイズを超えるため使用できません");
        }

        if (index >= 0) this.value.add(index, JSONValueType.get(value).toJSON(value));
        else this.value.add(this.value.size() + index, JSONValueType.get(value).toJSON(value));
    }

    /**
     * 配列の後ろに引数に渡された値を追加します。
     * @param value 格納する値。
     */
    public void add(@Nullable Object value) {
        this.value.add(JSONValueType.get(value).toJSON(value));
    }

    /**
     * 引数に渡されたインデックスの値を上書きします。
     * @param index インデックス。
     * @param value 格納する値。
     * @throws IllegalArgumentException インデックスが不正な場合。
     */
    public void set(int index, @Nullable Object value) throws IllegalArgumentException {
        if (index >= this.value.size()) {
            throw new IllegalArgumentException("インデックス " + index + " はサイズを超えるため使用できません");
        }

        if (index >= 0) this.value.set(index, JSONValueType.get(value).toJSON(value));
        else this.value.set(this.value.size() + index, JSONValueType.get(value).toJSON(value));
    }

    @Override
    public boolean delete(int index) {
        if (has(index)) {
            if (index >= 0) value.remove(index);
            else value.remove(value.size() + index);
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

    @Override
    public int length() {
        return value.size();
    }

    @Override
    public Iterator<JSONValue<?>> iterator() {
        final List<JSONValue<?>> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i, getTypeAt(i)));
        }

        return list.iterator();
    }

    /**
     * この配列を再帰的にListに変換します。
     * @return List形式のディープコピー。
     */
    public List<@Nullable Object> asList() {
        final List<Object> list = new ArrayList<>();

        for (int i = 0; i < length(); i++) {
            final JSONValueType<?> type = getTypeAt(i);

            if (type.equals(JSONValueTypes.OBJECT)) {
                final JSONObject object = get(i, JSONValueTypes.OBJECT);
                list.add(object.asMap());
            }
            else if (type.equals(JSONValueTypes.ARRAY)) {
                final JSONArray array = get(i, JSONValueTypes.ARRAY);
                list.add(array.asList());
            }
            else if (value.get(i) instanceof JSONPrimitive<?> primitive) {
                list.add(primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(i).getClass().getName());
            }
        }

        return list;
    }

    @Override
    public JSONArray copy() {
        return JSONValueTypes.ARRAY.toJSON(asList());
    }

    /**
     * 引数に渡された構造体がこの構造体の部分構造であるかを返します。
     * @param other 構造体。
     * @return 部分構造であれば、真。
     */
    public boolean isSuperOf(JSONArray other) {
        if (other.length() == 0) return true;

        for (final JSONValue<?> conditionValue : other) {
            if (value.stream().anyMatch(targetValue -> {
                if (targetValue instanceof JSONObject superVal && conditionValue instanceof JSONObject subVal) {
                    return superVal.isSuperOf(subVal);
                }
                else if (targetValue instanceof JSONArray superVal && conditionValue instanceof JSONArray subVal) {
                    return superVal.isSuperOf(subVal);
                }
                else {
                    return targetValue.equals(conditionValue);
                }
            })) {
                return true;
            }
        }

        return false;
    }

    /**
     * この配列が引数に渡された型のみを要素に持つ配列であるかを返します。
     * @param type 任意の型。
     * @return この配列がその型の配列であれば、真。
     */
    public boolean isArrayOf(JSONValueType<?> type) {
        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                return false;
            }
        }

        return true;
    }

    /**
     * この配列が引数に渡された型のみを要素に持つ配列であれば、その型の型付き配列に変換して返します。
     * @param type 任意の型。
     * @return 型付き配列。
     * @param <T> 任意の型。
     */
    public <T extends JSONValue<?>> TypedJSONArray<T> typed(JSONValueType<T> type) {
        final TypedJSONArray<T> array = new TypedJSONArray<>(type);

        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                throw new IllegalStateException("その型の値でない要素が見つかりました: " + getTypeAt(i).toString());
            }

            final T element = get(i, type);
            array.add(element);
        }

        return array;
    }

    /**
     * IterableをJSONArrayに変換します。
     * @param iterable Iterable。
     * @return JSONArray。
     */
    public static JSONArray valueOf(Iterable<?> iterable) {
        final List<JSONValue<?>> list = new ArrayList<>();

        for (final var e : iterable) {
            list.add(valueOf(e));
        }

        return new JSONArray(list);
    }
}
