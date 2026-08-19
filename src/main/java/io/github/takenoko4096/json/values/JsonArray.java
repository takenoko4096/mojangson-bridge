package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValue;
import io.github.takenoko4096.json.JsonValueType;
import io.github.takenoko4096.json.JsonValueTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * json構造における配列を表現します。
 */
@NullMarked
public final class JsonArray extends JsonValue<List<JsonValue<?>>> implements JsonIterable<JsonValue<?>> {
    /**
     * 長さ0のJsonArrayを作成します。
     */
    public JsonArray() {
        super(new ArrayList<>());
    }

    /**
     * JsonValueのListからJsonArrayを作成します。
     * @param list 元となるList。
     */
    public JsonArray(List<JsonValue<?>> list) {
        super(new ArrayList<>(list));
    }

    @Override
    public JsonValueType<?> getType() {
        return JsonValueTypes.ARRAY;
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
    public JsonValueType<?> getTypeAt(int index) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return JsonValueType.of(value.get(index));
        else return JsonValueType.of(value.get(value.size() + index));
    }

    /**
     * 引数に渡されたインデックスに格納された値を返します。
     * @param index インデックス。
     * @param type 期待する型。
     * @return インデックスに格納された値。
     * @param <T> 期待する型。
     * @throws IllegalArgumentException インデックスが存在しない、または予期しない型の場合。
     */
    public <T extends JsonValue<?>> T getOrThrow(int index, JsonValueType<T> type) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!getTypeAt(index).equals(type)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません");
        }

        if (index >= 0) return type.toJson(value.get(index));
        else return type.toJson(value.get(value.size() + index));
    }

    /**
     * 引数に渡されたインデックスに格納された値を返します。インデックスが存在しない、または型の不一致の場合にnullを返します。
     * @param index インデックス。
     * @param type 期待する型。
     * @return インデックスに格納された値。
     * @param <T> 期待する型。
     */
    public <T extends JsonValue<?>> @Nullable T getOrNull(int index, JsonValueType<T> type) {
        if (has(index)) {
            if (getTypeAt(index).equals(type)) {
                return getOrThrow(index, type);
            }
            else return null;
        }
        else return null;
    }

    /**
     * 引数に渡されたインデックスに格納された値を返します。インデックスが存在しない、または型の不一致の場合にデフォルト値を返します。
     * @param index インデックス。
     * @param type 期待する型。
     * @param defaultValue デフォルト値。
     * @return インデックスに格納された値。
     * @param <T> 期待する型。
     */
    public <T extends JsonValue<?>> T getOrDefault(int index, JsonValueType<T> type, T defaultValue) {
        return Objects.requireNonNullElse(getOrNull(index, type), defaultValue);
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

        final JsonValue<?> jsonValue = JsonValue.valueOf(value);

        if (index >= 0) {
            this.value.add(index, jsonValue);
        }
        else {
            this.value.add(this.value.size() + index, jsonValue);
        }
    }

    /**
     * 配列の後ろに引数に渡された値を追加します。
     * @param value 格納する値。
     */
    public void add(@Nullable Object value) {
        this.value.add(JsonValue.valueOf(value));
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

        final JsonValue<?> jsonValue = JsonValue.valueOf(value);

        if (index >= 0) {
            this.value.set(index, jsonValue);
        }
        else {
            this.value.set(this.value.size() + index, jsonValue);
        }
    }

    @Override
    public boolean delete(int index) {
        if (has(index)) {
            if (index >= 0) {
                value.remove(index);
            }
            else {
                value.remove(value.size() + index);
            }

            return true;
        }
        else return false;
    }

    @Override
    public boolean clear() {
        if (isEmpty()) {
            return false;
        }
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
    public Iterator<JsonValue<?>> iterator() {
        final List<JsonValue<?>> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(getOrThrow(i, getTypeAt(i)));
        }

        return list.iterator();
    }

    public List<JsonValue<?>> toList() {
        return List.copyOf(value);
    }

    /**
     * この配列を再帰的にListに変換します。
     * @return List形式のディープコピー。
     */
    public List<@Nullable Object> toListRecursively() {
        final List<Object> list = new ArrayList<>();

        for (int i = 0; i < length(); i++) {
            final JsonValueType<?> type = getTypeAt(i);

            if (type.equals(JsonValueTypes.OBJECT)) {
                final JsonObject object = getOrThrow(i, JsonValueTypes.OBJECT);
                list.add(object.toMapRecursively());
            }
            else if (type.equals(JsonValueTypes.ARRAY)) {
                final JsonArray array = getOrThrow(i, JsonValueTypes.ARRAY);
                list.add(array.toListRecursively());
            }
            else if (value.get(i) instanceof JsonPrimitive<?> primitive) {
                list.add(primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(i).getClass().getName());
            }
        }

        return list;
    }

    @Override
    public JsonArray deepCopy() {
        return JsonValueTypes.ARRAY.toJson(toListRecursively());
    }

    /**
     * 引数に渡された構造体がこの構造体の部分構造であるかを返します。
     * @param other 構造体。
     * @return 部分構造であれば、真。
     */
    public boolean isSuperOf(JsonArray other) {
        if (other.length() == 0) return true;

        for (final JsonValue<?> conditionValue : other) {
            if (value.stream().anyMatch(targetValue -> {
                if (targetValue instanceof JsonObject superVal && conditionValue instanceof JsonObject subVal) {
                    return superVal.isSuperOf(subVal);
                }
                else if (targetValue instanceof JsonArray superVal && conditionValue instanceof JsonArray subVal) {
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
    public boolean isArrayOf(JsonValueType<?> type) {
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
    public <T extends JsonValue<?>> TypedJsonArray<T> typed(JsonValueType<T> type) {
        final TypedJsonArray<T> array = new TypedJsonArray<>(type);

        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                throw new IllegalStateException("その型の値でない要素が見つかりました: " + getTypeAt(i).toString());
            }

            final T element = getOrThrow(i, type);
            array.add(element);
        }

        return array;
    }

    /**
     * IterableをJsonArrayに変換します。
     * @param iterable Iterable。
     * @return JsonArray。
     */
    public static JsonArray valueOf(Iterable<?> iterable) {
        final List<JsonValue<?>> list = new ArrayList<>();

        for (final var e : iterable) {
            list.add(valueOf(e));
        }

        return new JsonArray(list);
    }
}
