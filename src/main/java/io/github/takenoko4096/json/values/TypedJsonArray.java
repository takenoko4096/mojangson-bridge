package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValue;
import io.github.takenoko4096.json.JsonValueType;
import io.github.takenoko4096.json.JsonValueTypes;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * json構造における配列を表現します。
 * 型付きの JsonArray であり、このクラスにラップされる要素はすべて T 型であることが確約されます。
 * @param <T> 要素の型
 * @see JsonArray
 */
public class TypedJsonArray<T extends JsonValue<?>> extends JsonValue<List<T>> implements JsonIterable<T> {
    private final JsonValueType<T> type;

    /**
     * 要素の型と JsonValue の List から TypedJsonArray を作成します。
     * @param type 要素の型を表現するオブジェクト。
     * @param list 元となる List
     */
    public TypedJsonArray(JsonValueType<T> type, List<T> list) {
        super(new ArrayList<>(list));
        this.type = type;

        for (int i = 0; i < length(); i++) {
            final T element = value.get(i);

            if (!JsonValueType.of(element).equals(type)) {
                throw new IllegalArgumentException("TypedJsonArrayのインスタンス化に失敗しました: インデックス " + i + " は　" + type + " 型ではありません: " + JsonValueType.of(element) + " 型の " + element + " です");
            }
        }
    }

    /**
     * 長さ 0 の TypedJsonArray を要素の型を指定して作成します。
     * @param type 要素の型を表現するオブジェクト
     */
    public TypedJsonArray(JsonValueType<T> type) {
        this(type, List.of());
    }

    @Override
    public JsonValueType<JsonArray> getType() {
        return JsonValueTypes.ARRAY;
    }

    /**
     * 要素の型を返します。
     * @return 要素の型
     */
    public JsonValueType<T> getElementType() {
        return type;
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
     * 添字に対応する位置に格納された値を返します。
     * @param index 添字
     * @return 添字に対応する位置に格納された値
     * @throws IllegalArgumentException 添字に対応する位置が存在しない場合
     */
    public T getOrThrow(int index) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return value.get(index);
        else return value.get(value.size() + index);
    }

    /**
     * 添字に対応する位置に格納された値を返します。添字に対応する位置が存在しない場合に null を返します。
     * @param index 添字
     * @return 添字に対応する位置に格納された値
     */
    public @Nullable T getOrNull(int index) {
        if (has(index)) {
            return getOrThrow(index);
        }
        else {
            return null;
        }
    }

    /**
     * 添字に対応する位置に格納された値を返します。添字に対応する位置が存在しない場合にデフォルト値を返します。
     * @param index 添字
     * @param defaultValue デフォルト値
     * @return 添字に対応する位置に格納された値
     */
    public T getOrDefault(int index, Object defaultValue) {
        return Objects.requireNonNullElse(getOrNull(index), type.toJson(defaultValue));
    }

    /**
     * 添字に対応する位置に値を格納し、それ以降の値を後ろに追いやります。
     * @param index 添字
     * @param value 値
     * @throws IllegalArgumentException 添字が不正な場合
     */
    public void add(int index, Object value) throws IllegalArgumentException {
        if (index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        final T t = type.toJson(value);

        if (index >= 0) this.value.add(index, t);
        else this.value.add(this.value.size() + index, t);
    }

    /**
     * 配列の後ろに値を追加します。
     * @param value 値
     */
    public void add(Object value) {
        this.value.add(type.toJson(value));
    }

    /**
     * 添字に対応する位置の値を上書きします。
     * @param index 添字
     * @param value 値
     * @throws IllegalArgumentException 添字が不正な場合
     */
    public void set(int index, Object value) throws IllegalArgumentException {
        if (index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.set(index, type.toJson(value));
        else this.value.set(this.value.size() + index, type.toJson(value));
    }

    @Override
    public boolean delete(int index) {
        if (has(index)) {
            if (index >= 0) value.remove(index);
            else value.remove(this.value.size() + index);
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

    /**
     * T (JsonValue) の List に変換して返します。
     * @return List&lt;T&gt;
     */
    public List<T> toList() {
        return List.copyOf(value);
    }

    @Override
    public TypedJsonArray<T> copy() {
        return untyped().copy().typed(type);
    }

    @Override
    public Iterator<T> iterator() {
        return value.iterator();
    }

    /**
     * 型付き配列を型の保証のない JsonArray に変換します。
     * @return JsonArray
     */
    public JsonArray untyped() {
        final JsonArray array = new JsonArray();
        for (int i = 0; i < length(); i++) {
            array.add(getOrThrow(i));
        }
        return array;
    }

    @Override
    public String toString() {
        return type + super.toString();
    }
}
