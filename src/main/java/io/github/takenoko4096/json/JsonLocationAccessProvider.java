package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.Nullable;

/**
 * 検査例外 JsonPathUnableToAccessException を投げるための関数型インターフェース。
 * @param <S> json構造
 * @param <U> キーとなる値
 */
@FunctionalInterface
public interface JsonLocationAccessProvider<S extends JsonStructure, U> {
    /**
     * 構造体とキーまたはインデックスを使用して任意の位置を参照するために使用されます。
     * @param structure 使用する構造体
     * @param parameter 構造体内部の参照に使用するキーまたはインデックスのオブジェクト
     * @return 実装に依存する任意の戻り値
     * @throws JsonPathUnableToAccessException パスの参照先へのアクセスに失敗した場合
     */
    @Nullable U use(S structure, Object parameter) throws JsonPathUnableToAccessException;
}
