package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public abstract class MojangsonArray<T, U extends MojangsonValue<?>> extends MojangsonValue<T> implements MojangsonIterable<U> {
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

    /**
     * プリミティブ配列として取得します。
     * @return プリミティブ配列。
     */
    public abstract T toArray();

    /**
     * リスト型のビューを作成します。
     * @param setter セッター関数。第一引数の配列の第二引数の添え字に対応する位置に対して第三引数を値を代入することが期待されます。
     * @return リスト型のビュー。
     */
    protected MojangsonList getView(TriConsumer<T, Integer, Object> setter) {
        final T array = value;

        final List<MojangsonValue<?>> values = new ArrayList<>();
        forEach(values::add);

        return new MojangsonList(values) {
            @Override
            public void set(int index, Object value1) {
                super.set(index, value1);

                setter.accept(array, (index >= 0) ? index : super.length() + index, value1);
            }

            @Override
            public void add(int index, Object value1) {
                throw new IllegalStateException("MojangsonArrayから作成されたMojangsonListにおいてこの操作は禁じられています");
            }

            @Override
            public void add(Object value1) {
                throw new IllegalStateException("MojangsonArrayから作成されたMojangsonListにおいてこの操作は禁じられています");
            }

            @Override
            public boolean delete(int index) {
                throw new IllegalStateException("MojangsonArrayから作成されたMojangsonListにおいてこの操作は禁じられています");
            }

            @Override
            public boolean clear() {
                throw new IllegalStateException("MojangsonArrayから作成されたMojangsonListにおいてこの操作は禁じられています");
            }

            @Override
            public String toString() {
                return super.toString() + ' ' + "(View of MojangsonArray)";
            }
        };
    }

    /**
     * この配列へのビューを返します。
     * @return リスト型のビュー。このリストに対する変更は配列にも反映されます。なお一部の操作は整合性の確保のため禁じられています。
     */
    public abstract MojangsonList listView();

    @FunctionalInterface
    public interface TriConsumer<S, T, U> {
        void accept(S s, T t, U u);
    }
}
