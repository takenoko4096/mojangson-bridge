package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * mojangsonにおける配列を表現します。
 * @param <A> 配列型
 * @param <B> mojangsonにおける表現
 */
public abstract sealed class MojangsonArray<A, B extends MojangsonValue<?>> extends MojangsonValue<A> implements MojangsonIterable<B> permits MojangsonByteArray, MojangsonIntArray, MojangsonLongArray {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされるプリミティブ配列
     */
    protected MojangsonArray(A value) {
        super(value);

        if (!value.getClass().isArray()) {
            throw new IllegalArgumentException("MojangsonArray のインスタンス化に失敗しました: 配列型でない値は MojangsonArray に変換できません");
        }
    }

    @Override
    public boolean has(int index) {
        if (index >= 0) return index < length();
        else return has(length() + index);
    }

    /**
     * 添字に対応する位置の値を boxing して返します。添字が無効の場合 {@code null} を返します。
     * @param index 添字
     * @return {@link B}
     */
    public abstract @Nullable B getBoxedOrNull(int index);

    /**
     * 添字に対応する位置に値を代入します。boxed 値の {@link B} も代入できます。
     * @param index 添字
     * @param value 値
     */
    protected abstract void setBoxed(int index, Object value);

    @Override
    public abstract boolean delete(int index);

    @Override
    public abstract MojangsonArray<A, B> copy();

    @Override
    public abstract MojangsonValueType<? extends MojangsonArray<A, B>> getType();

    /**
     * 要素の型を返します。
     * @return 要素の型
     */
    public abstract MojangsonValueType<B> getElementType();

    /**
     * プリミティブ配列としてコピーを取得します。
     * @return プリミティブ配列
     */
    public abstract A toArray();

    /**
     * プリミティブ配列としてのビューを返します。この操作は軽量です。
     * @return プリミティブ配列
     */
    public A arrayView() {
        return value;
    }

    /**
     * 要素をコピーした {@link TypedMojangsonList}&lt;{@link B}&gt; を作成して返します。
     * @return {@link TypedMojangsonList}&lt;{@link B}&gt;
     */
    public TypedMojangsonList<B> boxed() {
        final List<B> list = new ArrayList<>();
        forEach(list::add);
        return new TypedMojangsonList<>(getElementType(), list);
    }

    private @Nullable MojangsonList untypedView;

    /**
     * 型情報を排除した {@link MojangsonList} によるビューを返します。このリストに対する変更はこのオブジェクトにも反映されます。
     * 一部の操作は整合性の確保のため禁じられています。
     * @return {@link MojangsonList} 型のビュー
     */
    public MojangsonList untypedView() {
        if (untypedView == null) {
            final MojangsonArray<A, B> that = this;

            untypedView = new MojangsonList() {
                @Override
                public boolean isEmpty() {
                    return that.isEmpty();
                }

                @Override
                public boolean has(int index) {
                    return that.has(index);
                }

                @Override
                public int length() {
                    return that.length();
                }

                @Override
                public <T extends MojangsonValue<?>> T getOrThrow(int index, MojangsonValueType<T> type) throws IllegalArgumentException {
                    if (!type.equals(that.getElementType())) {
                        throw new IllegalArgumentException("インデックス " + index + " は期待される型 (" + type + ") の値と紐づけられていません: " + that.getElementType());
                    }

                    final B b = that.getBoxedOrNull(index);
                    if (b == null) throw new IllegalArgumentException("インデックス " + index + " に値は紐づけられていません");

                    return type.toMojangson(b);
                }

                @Override
                public <T extends MojangsonValue<?>> @Nullable T getOrNull(int index, MojangsonValueType<T> type) {
                    final B b = that.getBoxedOrNull(index);
                    if (b == null) return null;
                    if (!b.getType().equals(type)) return null;
                    return type.toMojangson(b);
                }

                @Override
                public <T extends MojangsonValue<?>> T getOrDefault(int index, MojangsonValueType<T> type, Object defaultValue) {
                    final B b = that.getBoxedOrNull(index);
                    if (b == null) return type.toMojangson(defaultValue);
                    if (!b.getType().equals(type)) return type.toMojangson(defaultValue);
                    return type.toMojangson(b);
                }

                @Override
                public void set(int index, Object value) throws IllegalArgumentException {
                    that.setBoxed(index, value);
                }

                @Override
                public boolean delete(int index) {
                    throw new UnsupportedOperationException(MojangsonArray.class.getSimpleName() + " の listView に対して配列長を変更する操作は無効です");
                }

                @Override
                public boolean clear() {
                    throw new UnsupportedOperationException(MojangsonArray.class.getSimpleName() + " の listView に対して配列長を変更する操作は無効です");
                }

                @Override
                public List<MojangsonValue<?>> toList() {
                    final List<MojangsonValue<?>> list = new ArrayList<>();
                    that.forEach(list::add);
                    return list;
                }

                @Override
                public MojangsonList copy() {
                    final List<MojangsonValue<?>> list = new ArrayList<>();
                    that.forEach(list::add);
                    return new MojangsonList(list);
                }

                @Override
                public Iterator<MojangsonValue<?>> iterator() {
                    final List<MojangsonValue<?>> list = new ArrayList<>();
                    that.forEach(list::add);
                    final List<MojangsonValue<?>> immutable = List.copyOf(list);
                    return immutable.iterator();
                }

                @Override
                public <T extends MojangsonValue<?>> TypedMojangsonList<T> typed(MojangsonValueType<T> type) {
                    if (!type.equals(that.getElementType())) {
                        throw new IllegalArgumentException("配列の要素の型は " + that.getElementType() + "ですが、 typed() に無効な型が渡されました: " + type);
                    }

                    final TypedMojangsonList<T> list = new TypedMojangsonList<>(type);
                    that.forEach(list::add);
                    return list;
                }

                @Override
                public void add(Object value) {
                    throw new UnsupportedOperationException(MojangsonArray.class.getSimpleName() + " の listView に対して配列長を変更する操作は無効です");
                }

                @Override
                public void add(int index, Object value) throws IllegalArgumentException {
                    throw new UnsupportedOperationException(MojangsonArray.class.getSimpleName() + " の listView に対して配列長を変更する操作は無効です");
                }

                @Override
                public String toString() {
                    return copy() + String.format("(view of %s)", that.getClass().getSimpleName());
                }
            };
        }

        return untypedView;
    }
}
