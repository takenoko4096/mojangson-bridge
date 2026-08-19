package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 型付きのMojangsonList。このクラスにラップされる要素はすべてT型であることが確約されます。
 * @param <T> 要素の型。
 */
@NullMarked
public class TypedMojangsonList<T extends MojangsonValue<?>> extends MojangsonValue<List<T>> implements MojangsonIterable<T> {
    private final MojangsonValueType<T> type;

    /**
     * 要素の型とMojangsonValueのListからTypedMojangsonListを作成します。
     * @param type 要素の型を表現するオブジェクト。
     * @param list 元となるList。
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
     * 長さ0のTypedMojangsonListを要素の型を指定して作成します。
     * @param type 要素の型を表現するオブジェクト。
     */
    public TypedMojangsonList(MojangsonValueType<T> type) {
        this(type, new ArrayList<>());
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.LIST;
    }

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
     * 引数に渡されたインデックスに格納された値を返します。
     * @param index インデックス。
     * @return インデックスに格納された値。
     * @throws IllegalArgumentException インデックスが存在しない場合。
     */
    public T getOrThrow(int index) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return value.get(index);
        else return value.get(value.size() + index);
    }

    public @Nullable T getOrNull(int index) {
        if (has(index)) {
            return getOrThrow(index);
        }
        else return null;
    }

    public T getOrDefault(int index, T defaultValue) {
        return Objects.requireNonNullElse(getOrNull(index), defaultValue);
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
     * リストの後ろに引数に渡された値を追加します。
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

    /**
     * 構造体の指定の添え字番目のオブジェクトを消去します。
     * @param index 添え字。
     * @return 削除に成功した場合、真。
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
     * 型付きリストを型の保証のないMojangsonListに変換します。
     * @return MojangsonList。
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
