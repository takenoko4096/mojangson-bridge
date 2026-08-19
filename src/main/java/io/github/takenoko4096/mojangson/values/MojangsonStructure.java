package io.github.takenoko4096.mojangson.values;

/**
 * mojangsonにおける構造体を表現します。
 */
public interface MojangsonStructure {
    /**
     * 構造体が空であるかを返します。
     * @return 空の場合 true
     */
    boolean isEmpty();

    /**
     * 構造体を空にします。
     * @return 空にすることができた場合 true
     */
    boolean clear();

    /**
     * 構造体のディープコピーを作成します。
     * @return ディープコピーされたオブジェクト
     */
    MojangsonStructure copy();
}
