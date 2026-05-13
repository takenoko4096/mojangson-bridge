package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JSONValue;
import io.github.takenoko4096.json.JSONValueType;
import io.github.takenoko4096.json.JSONValueTypes;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * json構造における配列を表現します。
 * 型付きのJSONArrayであり、このクラスにラップされる要素はすべてT型であることが確約されます。
 * @param <T> 要素の型。
 * @see JSONArray
 */
@NullMarked
public class TypedJSONArray<T extends JSONValue<?>> extends JSONValue<List<T>> implements JSONIterable<T> {
    private final JSONValueType<T> type;

    /**
     * 要素の型とJSONValueのListからTypedJSONArrayを作成します。
     * @param type 要素の型を表現するオブジェクト。
     * @param list 元となるList。
     */
    public TypedJSONArray(JSONValueType<T> type, List<T> list) {
        super(new ArrayList<>(list));
        this.type = type;

        for (int i = 0; i < length(); i++) {
            final T element = value.get(i);

            if (!JSONValueType.get(element).equals(type)) {
                throw new IllegalArgumentException("TypedJSONArrayのインスタンス化に失敗しました: インデックス " + i + " は　" + type + " 型ではありません: " + JSONValueType.get(element) + " 型の " + element + " です");
            }
        }
    }

    /**
     * 長さ0のTypedJSONArrayを要素の型を指定して作成します。
     * @param type 要素の型を表現するオブジェクト。
     */
    public TypedJSONArray(JSONValueType<T> type) {
        this(type, List.of());
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
     * 引数に渡されたインデックスに格納された値を返します。
     * @param index インデックス。
     * @return インデックスに格納された値。
     * @throws IllegalArgumentException インデックスが存在しない場合。
     */
    public T get(int index) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return value.get(index);
        else return value.get(value.size() + index);
    }

    /**
     * 引数に渡されたインデックスに値を格納し、そのインデックス以降の値を後ろに追いやります。
     * @param index インデックス。
     * @param value 格納する値。
     * @throws IllegalArgumentException インデックスが不正な場合。
     */
    public void add(int index, T value) throws IllegalArgumentException {
        if (index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.add(index, value);
        else this.value.add(this.value.size() + index, value);
    }

    /**
     * 配列の後ろに引数に渡された値を追加します。
     * @param value 格納する値。
     */
    public void add(T value) {
        this.value.add(value);
    }

    /**
     * 引数に渡されたインデックスの値を上書きします。
     * @param index インデックス。
     * @param value 格納する値。
     * @throws IllegalArgumentException インデックスが不正な場合。
     */
    public void set(int index, T value) throws IllegalArgumentException {
        if (index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.set(index, value);
        else this.value.set(this.value.size() + index, value);
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

    @Override
    public TypedJSONArray<T> copy() {
        return untyped().copy().typed(type);
    }

    @Override
    public Iterator<T> iterator() {
        final List<T> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i));
        }

        return list.iterator();
    }

    /**
     * 型付き配列を型の保証のないJSONArrayに変換します。
     * @return JSONArray。
     */
    public JSONArray untyped() {
        final JSONArray array = new JSONArray();
        for (int i = 0; i < length(); i++) {
            array.add(get(i));
        }
        return array;
    }

    @Override
    public String toString() {
        return type + super.toString();
    }
}
