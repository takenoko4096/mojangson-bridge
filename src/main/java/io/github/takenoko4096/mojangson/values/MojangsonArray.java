package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
     * 配列に一時リストビューを用いて書き込みます。
     * @param function
     * @return
     * @param <U>
     */
    public final <U> @Nullable U write(Function<MojangsonList, @Nullable U> function) {
        final MojangsonList list = boxed().untyped();
        final U u = function.apply(list);

        if (list.length() != length()) {
            throw new IllegalStateException("配列長は変更できません");
        }

        switch (this) {
            case MojangsonByteArray byteArray -> {
                for (int i = 0; i < length(); i++) {
                    byteArray.set(i, list.getOrDefault(i, MojangsonValueTypes.BYTE, byteArray.getOrThrow(i)).byteValue());
                }
            }
            case MojangsonIntArray intArray -> {
                for (int i = 0; i < length(); i++) {
                    intArray.set(i, list.getOrDefault(i, MojangsonValueTypes.INT, intArray.getOrThrow(i)).intValue());
                }
            }
            case MojangsonLongArray longArray -> {
                for (int i = 0; i < length(); i++) {
                    longArray.set(i, list.getOrDefault(i, MojangsonValueTypes.LONG, longArray.getOrThrow(i)).longValue());
                }
            }
        }

        return u;
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
