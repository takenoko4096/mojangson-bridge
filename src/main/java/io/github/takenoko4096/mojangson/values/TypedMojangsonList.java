package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 型付きのMojangsonList このクラスにラップされる要素はすべて T 型であることが確約されます。
 * @param <T> 要素の型
 */
public class TypedMojangsonList<T extends MojangsonValue<?>> extends MojangsonValue<List<T>> implements MojangsonIterable<T> {
    private final MojangsonValueType<T> type;

    /**
     * 要素の型と MojangsonValue の List から TypedMojangsonList を作成します。
     * @param type 要素の型を表現するオブジェクト
     * @param list 元となる List
     */
    public TypedMojangsonList(MojangsonValueType<T> type, List<T> list) {
        super(list);
        this.type = type;

        for (int i = 0; i < length(); i++) {
            final T element = value.get(i);

            if (!MojangsonValueType.of(element).equals(type)) {
                throw new IllegalArgumentException("TypedMojangsonListのインスタンス化に失敗しました: インデックス " + i + " は　" + type + " 型ではありません: " + MojangsonValueType.of(element) + " 型の " + element + " です");
            }
        }
    }

    /**
     * 長さ 0 の TypedMojangsonList を要素の型を指定して作成します。
     * @param type 要素の型を表現するオブジェクト
     */
    public TypedMojangsonList(MojangsonValueType<T> type) {
        this(type, new ArrayList<>());
    }

    @Override
    public MojangsonValueType<MojangsonList> getType() {
        return MojangsonValueTypes.LIST;
    }

    /**
     * 要素の型を返します。
     * @return 要素の型
     */
    public MojangsonValueType<T> getElementType() {
        return type;
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean has(int index) {
        if (index >= 0) return index < value.size();
        else if (value.size() + index >= 0) return has(value.size() + index);
        else return false;
    }

    /**
     * 添字に対応する位置に格納された値を返します。
     * @param index 添字
     * @return 添字に対応する位置にに格納された値。
     * @throws IllegalArgumentException 添字に対応する位置が存在しない場合。
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
        else return null;
    }

    /**
     * 添字に対応する位置に格納された値を返します。添字に対応する位置が存在しない場合にデフォルト値を返します。
     * @param index 添字
     * @param defaultValue デフォルト値
     * @return 添字に対応する位置に格納された値
     */
    public T getOrDefault(int index, Object defaultValue) {
        return Objects.requireNonNullElse(getOrNull(index), type.toMojangson(defaultValue));
    }

    /**
     * 添字に対応する位置に値を格納し、それ以降の値を後ろに追いやります。
     * @param index 添字
     * @param value 値
     * @throws IllegalArgumentException インデックスが不正な場合
     */
    public void add(int index, Object value) throws IllegalArgumentException {
        if (index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        final T t = type.toMojangson(value);

        if (index >= 0) this.value.add(index, t);
        else this.value.add(this.value.size() + index, t);
    }

    /**
     * リストの後ろに値を追加します。
     * @param value 値
     */
    public void add(Object value) {
        this.value.add(type.toMojangson(value));
    }

    /**
     * 引数に渡された添字に対応する位置の値を上書きします。
     * @param index 添字
     * @param value 値
     * @throws IllegalArgumentException インデックスが不正な場合
     */
    public void set(int index, Object value) throws IllegalArgumentException {
        if (index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        final T t = type.toMojangson(value);

        if (index >= 0) this.value.set(index, t);
        else this.value.set(this.value.size() + index, t);
    }

    /**
     * 構造体の指定の添字番目のオブジェクトを消去します。
     * @param index 添字
     * @return 削除に成功した場合 true
     */
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
     * T (MojangsonValue) の List に変換して返します。
     * @return List&lt;T&gt;
     */
    public List<T> toList() {
        return List.copyOf(value);
    }

    @Override
    public TypedMojangsonList<T> deepCopy() {
        return untyped().deepCopy().typed(type);
    }

    @Override
    public Iterator<T> iterator() {
        final List<T> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(getOrThrow(i));
        }

        return list.iterator();
    }

    /**
     * 型付きリストを型の保証のない MojangsonList に変換します。
     * @return MojangsonList
     */
    public MojangsonList untyped() {
        final MojangsonList list = new MojangsonList();
        for (int i = 0; i < length(); i++) {
            list.add(getOrThrow(i));
        }
        return list;
    }

    @Override
    public String toString() {
        return type + super.toString();
    }
}
