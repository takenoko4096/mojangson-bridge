package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.JSONStructure;

/**
 * 検査例外 JSONPathUnableToAccessException を投げるための関数型インターフェース。
 * @param <S> json構造
 * @param <U> キーとなる値
 */
@FunctionalInterface
public interface JSONLocationAccessProvider<S extends JSONStructure, U> {
    /**
     * 構造体とキーまたはインデックスを使用して任意の位置を参照するために使用されます。
     * @param structure 使用する構造体。
     * @param parameter 構造体内部の参照に使用するキーまたはインデックスのオブジェクト。
     * @return 実装に依存する任意の戻り値。
     * @throws JSONPathUnableToAccessException パスの参照先へのアクセスに失敗した場合。
     */
    U use(S structure, Object parameter) throws JSONPathUnableToAccessException;
}
