package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValue;
import org.jspecify.annotations.NullMarked;

/**
 * mojangsonにおける反復可能オブジェクトを表現します。
 * @param <T> 要素の型。
 */
@NullMarked
public interface MojangsonIterable<T extends MojangsonValue<?>> extends MojangsonStructure, Iterable<T> {
    @Override
    boolean isEmpty();

    /**
     * 構造体の指定の添え字番目が存在するかどうかを返します。
     * @param index 添え字。
     * @return 存在する場合、真。
     */
    boolean has(int index);

    /**
     * 構造体の長さを取得します。
     * @return 長さ。
     */
    int length();

    @Override
    boolean clear();

    @Override
    MojangsonIterable<T> deepCopy();
}
