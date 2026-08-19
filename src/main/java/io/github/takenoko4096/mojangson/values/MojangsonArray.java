package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueType;

import java.util.ArrayList;
import java.util.List;

/**
 * mojangsonにおける配列を表現します。
 * @param <A> 配列型
 * @param <B> mojangsonにおける表現
 */
public abstract class MojangsonArray<A, B extends MojangsonValue<?>> extends MojangsonValue<A> implements MojangsonIterable<B> {
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

    @Override
    public abstract boolean delete(int index);

    @Override
    public abstract MojangsonArray<A, B> copy();

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
     * TypedMojangsonList&lt;B&gt; を作成して返します。
     * @return TypedMojangsonList&lt;B&gt;
     */
    public TypedMojangsonList<B> boxed() {
        final List<B> list = new ArrayList<>();
        forEach(list::add);
        return new TypedMojangsonList<>(getElementType(), list);
    }
}
