package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * mojangsonにおけるListを表現します。
 */
@NullMarked
public class MojangsonList extends MojangsonValue<List<MojangsonValue<?>>> implements MojangsonIterable<MojangsonValue<?>> {
    public MojangsonList(List<MojangsonValue<?>> value) {
        super(value);
    }

    public MojangsonList() {
        this(new ArrayList<>());
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.LIST;
    }

    @Override
    public boolean has(int index) {
        if (index >= 0) return index < value.size();
        else return has(value.size() + index);
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
    public MojangsonValueType<?> getTypeAt(int index) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return MojangsonValueType.get(value.get(index));
        else return MojangsonValueType.get(value.get(value.size() + index));
    }

    /**
     * 引数に渡されたインデックスに格納された値を返します。
     * @param index インデックス。
     * @param type 期待する型。
     * @return インデックスに格納された値。
     * @param <T> 期待する型。
     * @throws IllegalArgumentException インデックスが存在しない、または予期しない型の場合。
     */
    public <T extends MojangsonValue<?>> T get(int index, MojangsonValueType<T> type) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!getTypeAt(index).equals(type)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません");
        }

        if (index >= 0) return type.toMojangson(value.get(index));
        else return type.toMojangson(value.get(value.size() + index));
    }

    /**
     * 引数に渡されたインデックスに値を格納し、そのインデックス以降の値を後ろに追いやります。
     * @param index インデックス。
     * @param value 格納する値。
     * @throws IllegalArgumentException インデックスが不正な場合。
     */
    public void add(int index, Object value) throws IllegalArgumentException {
        if (index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.add(index, MojangsonValueType.get(value).toMojangson(value));
        else this.value.add(this.value.size() + index, MojangsonValueType.get(value).toMojangson(value));
    }

    /**
     * リストの後ろに引数に渡された値を追加します。
     * @param value 格納する値。
     */
    public void add(Object value) {
        this.value.add(MojangsonValueType.get(value).toMojangson(value));
    }

    /**
     * 引数に渡されたインデックスの値を上書きします。
     * @param index インデックス。
     * @param value 格納する値。
     * @throws IllegalArgumentException インデックスが不正な場合。
     */
    public void set(int index, Object value) throws IllegalArgumentException {
        if (index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.set(index, MojangsonValueType.get(value).toMojangson(value));
        else this.value.set(this.value.size() + index, MojangsonValueType.get(value).toMojangson(value));
    }

    /**
     * 構造体の指定の添え字番目のオブジェクトを消去します。
     * @param index 添え字。
     * @return 削除に成功した場合、真。
     */
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
    public Iterator<MojangsonValue<?>> iterator() {
        final List<MojangsonValue<?>> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i, getTypeAt(i)));
        }

        return list.iterator();
    }

    /**
     * このリストを再帰的にListに変換します。
     * @return List形式のディープコピー。
     */
    public List<Object> toList() {
        final List<Object> arrayList = new ArrayList<>();

        for (int i = 0; i < length(); i++) {
            final MojangsonValueType<?> type = getTypeAt(i);

            if (type.equals(MojangsonValueTypes.COMPOUND)) {
                final MojangsonCompound compound = get(i, MojangsonValueTypes.COMPOUND);
                arrayList.add(compound.toMap());
            }
            else if (type.equals(MojangsonValueTypes.LIST)) {
                final MojangsonList list = get(i, MojangsonValueTypes.LIST);
                arrayList.add(list.toList());
            }
            else if (value.get(i) instanceof MojangsonArray<?, ?> array) {
                arrayList.add(array.toArray());
            }
            else if (value.get(i) instanceof MojangsonPrimitive<?> primitive) {
                arrayList.add(primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(i).getClass().getName());
            }
        }

        return arrayList;
    }

    @Override
    public MojangsonList copy() {
        return MojangsonValueTypes.LIST.toMojangson(toList());
    }

    /**
     * 引数に渡された構造体がこの構造体の部分構造であるかを返します。
     * @param other 構造体。
     * @return 部分構造であれば、真。
     */
    public boolean isSuperOf(MojangsonList other) {
        if (other.length() == 0) return true;
        for (final MojangsonValue<?> conditionValue : other) {
            if (value.stream().anyMatch(targetValue -> {
                if (targetValue instanceof MojangsonCompound superVal && conditionValue instanceof MojangsonCompound subVal) {
                    return superVal.isSuperOf(subVal);
                }
                else if (targetValue instanceof MojangsonList superVal && conditionValue instanceof MojangsonList subVal) {
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
     * このリストが引数に渡された型のみを要素に持つリストであるかを返します。
     * @param type 任意の型。
     * @return このリストがその型のリストであれば、真。
     */
    public boolean isListOf(MojangsonValueType<?> type) {
        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                return false;
            }
        }

        return true;
    }

    /**
     * このリストが引数に渡された型のみを要素に持つリストであれば、その型の型付きリストに変換して返します。
     * @param type 任意の型。
     * @return 型付きリスト。
     * @param <T> 任意の型。
     */
    public <T extends MojangsonValue<?>> TypedMojangsonList<T> typed(MojangsonValueType<T> type) {
        final TypedMojangsonList<T> array = new TypedMojangsonList<>(type);

        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                throw new IllegalStateException("MojangsonListの型付きリストへの変換に失敗しました: " + type + " 型の値でない要素がインデックス " + i + " に見つかりました: " + getTypeAt(i));
            }

            final T element = get(i, type);
            array.add(element);
        }

        return array;
    }

    /**
     * IterableをMojangsonArrayに変換します。
     * @param iterable Iterable。
     * @return MojangsonArray。
     */
    public static MojangsonList valueOf(Iterable<?> iterable) {
        final List<MojangsonValue<?>> list = new ArrayList<>();

        for (final var e : iterable) {
            list.add(valueOf(e));
        }

        return new MojangsonList(list);
    }
}
