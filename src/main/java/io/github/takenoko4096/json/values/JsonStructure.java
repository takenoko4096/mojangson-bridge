package io.github.takenoko4096.json.values;

import org.jspecify.annotations.NullMarked;

/**
 * jsonにおける構造体を表現します。
 */
@NullMarked
public interface JsonStructure {
    /**
     * 構造体が空であるかを返します。
     * @return 空の場合、真。
     */
    boolean isEmpty();

    /**
     * 構造体を空にします。
     * @return 空にすることができた場合、真。
     */
    boolean clear();

    /**
     * 構造体のディープコピーを作成します。
     * @return ディープコピーされたオブジェクト。
     */
    JsonStructure deepCopy();
}
