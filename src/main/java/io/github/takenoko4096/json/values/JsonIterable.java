package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValue;
import org.jspecify.annotations.NullMarked;

/**
 * jsonにおける反復可能オブジェクトを表現します。
 * @param <T> 要素の型。
 */
@NullMarked
public interface JsonIterable<T extends JsonValue<?>> extends JsonStructure, Iterable<T> {
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

    /**
     * 構造体の指定の添え字番目のオブジェクトを消去します。
     * @param index 添え字。
     * @return 削除に成功した場合、真。
     */
    boolean delete(int index);

    @Override
    boolean clear();

    @Override
    JsonIterable<T> deepCopy();
}
