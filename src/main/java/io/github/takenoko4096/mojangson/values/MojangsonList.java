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
 * mojangsonにおけるListを表現します。
 */
public class MojangsonList extends MojangsonValue<List<MojangsonValue<?>>> implements MojangsonIterable<MojangsonValue<?>> {
    /**
     * MojangsonValue の List から MojangsonList を作成します。
     * @param list 元となる List
     */
    public MojangsonList(List<MojangsonValue<?>> list) {
        super(list);
    }

    /**
     * 長さ 0 の MojangsonList を作成します。
     */
    public MojangsonList() {
        this(new ArrayList<>());
    }

    @Override
    public MojangsonValueType<MojangsonList> getType() {
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
     * 添字に対応する位置に格納された値の型を返します。
     * @param index 添字
     * @return 添字に対応する位置に格納された値の型。
     * @throws IllegalArgumentException 添字に対応する位置が存在しない場合
     */
    public MojangsonValueType<?> getTypeAt(int index) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return MojangsonValueType.of(value.get(index));
        else return MojangsonValueType.of(value.get(value.size() + index));
    }

    /**
     * 添字に対応する位置に格納された値を返します。
     * @param index 添字
     * @param type 期待する型
     * @return 添字に対応する位置に格納された値
     * @param <T> 期待する型
     * @throws IllegalArgumentException 添字に対応する位置が存在しない、または予期しない型の場合
     */
    public <T extends MojangsonValue<?>> T getOrThrow(int index, MojangsonValueType<T> type) throws IllegalArgumentException {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!getTypeAt(index).equals(type)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません: " + getTypeAt(index));
        }

        if (index >= 0) return type.toMojangson(value.get(index));
        else return type.toMojangson(value.get(value.size() + index));
    }

    /**
     *添字に対応する位置に格納された値を返します。添字に対応する位置が存在しない、または型の不一致の場合にnullを返します。
     * @param index 添字
     * @param type 期待する型
     * @return 添字に対応する位置に格納された値
     * @param <T> 期待する型
     */
    public <T extends MojangsonValue<?>> @Nullable T getOrNull(int index, MojangsonValueType<T> type) {
        if (has(index)) {
            if (getTypeAt(index).equals(type)) {
                return getOrThrow(index, type);
            }
            else return null;
        }
        else return null;
    }

    /**
     * 添字に対応する位置に格納された値を返します。添字に対応する位置が存在しない、または型の不一致の場合にデフォルト値を返します。
     * @param index 添字
     * @param type 期待する型
     * @param defaultValue デフォルト値
     * @return 添字に対応する位置に格納された値
     * @param <T> 期待する型
     */
    public <T extends MojangsonValue<?>> T getOrDefault(int index, MojangsonValueType<T> type, Object defaultValue) {
        return Objects.requireNonNullElse(getOrNull(index, type), type.toMojangson(defaultValue));
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

        if (index >= 0) this.value.add(index, MojangsonValue.valueOf(value));
        else this.value.add(this.value.size() + index, MojangsonValue.valueOf(value));
    }

    /**
     * リストの後ろに引数に渡された値を追加します。
     * @param value 値
     */
    public void add(Object value) {
        this.value.add(MojangsonValue.valueOf(value));
    }

    /**
     * 添字に対応する位置の値を上書きします。
     * @param index 添字
     * @param value 値
     * @throws IllegalArgumentException 添字が不正な場合。
     */
    public void set(int index, Object value) throws IllegalArgumentException {
        if (index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.set(index, MojangsonValue.valueOf(value));
        else this.value.set(this.value.size() + index, MojangsonValue.valueOf(value));
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
    public Iterator<MojangsonValue<?>> iterator() {
        final List<MojangsonValue<?>> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(getOrThrow(i, getTypeAt(i)));
        }

        return list.iterator();
    }

    /**
     * MojangsonValue&lt;?&gt; の List に変換して返します。
     * @return List&lt;MojangsonValue&lt;?&gt;&gt;
     */
    public List<MojangsonValue<?>> toList() {
        return List.copyOf(value);
    }

    /**
     * この配列を再帰的に List に変換します。
     * @return List&lt;Object&gt;
     */
    public List<Object> toListRecursively() {
        final List<Object> arrayList = new ArrayList<>();

        for (int i = 0; i < length(); i++) {
            final MojangsonValueType<?> type = getTypeAt(i);

            if (type.equals(MojangsonValueTypes.COMPOUND)) {
                final MojangsonCompound compound = getOrThrow(i, MojangsonValueTypes.COMPOUND);
                arrayList.add(compound.toMapRecursively());
            }
            else if (type.equals(MojangsonValueTypes.LIST)) {
                final MojangsonList list = getOrThrow(i, MojangsonValueTypes.LIST);
                arrayList.add(list.toListRecursively());
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
        return MojangsonValueTypes.LIST.toMojangson(toListRecursively());
    }

    /**
     * 引数に渡された構造体がこの構造体の部分構造であるかを返します。
     * @param other 構造体
     * @return 部分構造であれば true
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
     * @param type 任意の型
     * @return このリストのすべての要素がその型であれば true
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
     * @param type 任意の型
     * @return 型付きリスト
     * @param <T> 任意の型
     */
    public <T extends MojangsonValue<?>> TypedMojangsonList<T> typed(MojangsonValueType<T> type) {
        final TypedMojangsonList<T> array = new TypedMojangsonList<>(type);

        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                throw new IllegalStateException("MojangsonListの型付きリストへの変換に失敗しました: " + type + " 型の値でない要素がインデックス " + i + " に見つかりました: " + getTypeAt(i));
            }

            final T element = getOrThrow(i, type);
            array.add(element);
        }

        return array;
    }

    /**
     * Iterable&lt;?&gt; を MojangsonList に変換します。
     * @param iterable Iterable
     * @return MojangsonList
     */
    public static MojangsonList valueOf(Iterable<?> iterable) {
        final List<MojangsonValue<?>> list = new ArrayList<>();

        for (final var e : iterable) {
            list.add(valueOf(e));
        }

        return new MojangsonList(list);
    }
}
