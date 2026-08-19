package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonArrayElementValueSetter;
import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * mojangsonにおける配列を表現します。
 * @param <T> 配列型
 * @param <U> mojangsonにおける表現
 */
public abstract class MojangsonArray<T, U extends MojangsonValue<?>> extends MojangsonValue<T> implements MojangsonIterable<U> {
    /**
     * サブクラスのためのコンストラクタ。
     * @param value ラップされるプリミティブ配列
     */
    protected MojangsonArray(T value) {
        super(value);

        if (!value.getClass().isArray()) {
            throw new IllegalArgumentException("MojangsonArrayのインスタンス化に失敗しました: 配列型でない値はMojangsonArrayに変換できません");
        }
    }

    @Override
    public boolean has(int index) {
        if (index >= 0) return index < length();
        else return has(length() + index);
    }

    @Override
    public abstract boolean delete(int index);

    @Override
    public abstract MojangsonArray<T, U> deepCopy();

    /**
     * 要素の型を返します。
     * @return 要素の型
     */
    public abstract MojangsonValueType<U> getElementType();

    /**
     * 配列要素のデフォルト値を返します。
     * @return 多くの場合 0 を表現するオブジェクト
     */
    protected abstract U getZero();

    /**
     * プリミティブ配列として取得します。
     * @return プリミティブ配列
     */
    public abstract T toArray();

    protected abstract void updateView(TypedMojangsonList<U> list);

    /**
     * リスト型のビューを作成します。
     * @param setter セッター関数
     * @return リスト型のビュー
     */
    protected TypedMojangsonList<U> getView(MojangsonArrayElementValueSetter<T> setter) {
        final T array = value;

        final List<U> values = new ArrayList<>();
        forEach(values::add);

        return new TypedMojangsonList<>(getElementType(), values) {
            private void update() {
                updateView(this);
            }

            @Override
            public U getOrThrow(int index) throws IllegalArgumentException {
                update();
                return super.getOrThrow(index);
            }

            @Override
            public @Nullable U getOrNull(int index) {
                update();
                return super.getOrNull(index);
            }

            @Override
            public U getOrDefault(int index, Object defaultValue) {
                update();
                return super.getOrDefault(index, defaultValue);
            }

            @Override
            public Iterator<U> iterator() {
                update();
                return super.iterator();
            }

            @Override
            public MojangsonList untyped() {
                update();
                return super.untyped();
            }

            @Override
            public List<U> toList() {
                update();
                return super.toList();
            }

            @Override
            public boolean has(int index) {
                update();
                return super.has(index);
            }

            @Override
            public int hashCode() {
                update();
                return super.hashCode();
            }

            @Override
            public TypedMojangsonList<U> deepCopy() {
                update();
                return super.deepCopy();
            }

            @Override
            public boolean equals(@Nullable Object o) {
                update();
                return super.equals(o);
            }

            @Override
            public void set(int index, Object value) throws IllegalArgumentException {
                getElementType().toMojangson(value);
                super.set(index, value);
                setter.set(array, (index >= 0) ? index : super.length() + index, value);
            }

            @Override
            public void add(int index, Object value) {
                throw new UnsupportedOperationException("MojangsonArray から作成された MojangsonList においてこの操作は禁じられています");
            }

            @Override
            public void add(Object value) {
                throw new UnsupportedOperationException("MojangsonArray から作成された MojangsonList においてこの操作は禁じられています");
            }

            @Override
            public boolean delete(int index) {
                final U zero = getZero();

                if (getOrThrow(index) == zero) {
                    return false;
                }
                else {
                    set(index, zero);
                    return true;
                }
            }

            @Override
            public boolean clear() {
                final int length = length();
                boolean successful = false;

                for (int i = 0; i < length; i++) {
                    if (delete(i)) {
                        successful = true;
                    }
                }

                return successful;
            }

            @Override
            public String toString() {
                update();
                return super.toString() + ' ' + "(View of MojangsonArray)";
            }
        };
    }

    /**
     * この配列へのビューを返します。
     * @return リスト型のビュー このリストに対する変更は配列にも反映されます。なお一部の操作 (add) は整合性の確保のため禁じられています。
     */
    public abstract TypedMojangsonList<U> listView();
}
